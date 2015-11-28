package com.echoman.storage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.storage.Storable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class AsyncSuperDao{
	
	private final static Logger LOG = LoggerFactory.getLogger(AsyncSuperDao.class);
		
	private BlockingQueue<Storable> taskQueue;
	private int batchSize = 1;
	private boolean autoCreateTable = false;
	protected SuperDao dao;
	
	public AsyncSuperDao(String tablePrefix){
		dao = new SuperDao(tablePrefix);
		taskQueue = Queues.newLinkedBlockingQueue();
		if(autoCreateTable) createTables();
		startTasks();
	}
	
	public AsyncSuperDao(String tablePrefix, int batchSize){
		this(tablePrefix);
		this.batchSize = batchSize;
	}
	
	public int taskLength(){
		return taskQueue.size();
	}
	
	@SuppressWarnings("rawtypes")
	public void createTables(){
		try {
			Set<Class> tableClasses = getTableBeans();
			for(Class klass: tableClasses){
				int result = dao.createTable(klass);
				if(result == 1) LOG.info("Create success, {}");
				else if(result == 0) LOG.info("Exist table, {}");
			}
		}catch (Exception e) {
			LOG.error("Create tables fail, ", e);
		}
	}
	
	public SuperDao superDao(){
		return dao;
	}
	
	public void setBatchSize(int batchSize){
		this.batchSize = batchSize;
	}
	
	/**
	 * 
	 */
	private void startTasks() {
		Thread saveForumWorke = new Thread(new SaveForumTask(), "DATA-SAVE-WORKER");
		saveForumWorke.start();
	}

	public void save(Storable bean){
		if(!dao.exist(bean)) taskQueue.add(bean);
		else LOG.info("Exist Storable: {}", bean);
	}
	
	private Map<String, Saver> savers = Maps.newHashMap();
	
	private void doSave0(){
		try {
			
			Storable bean = taskQueue.take();
			String saverKey = bean.getClass().getSimpleName();
			Saver saver = savers.get(saverKey);
			if(saver == null){
				saver = new Saver();
				savers.put(saverKey, saver);
			}
			saver.doSave(bean);
		} catch (Exception e) {
			LOG.error("Error doSaveForum", e);
		}
	}
	
	private class Saver{
		private List<Storable> list = Lists.newArrayList();
		public void doSave(Storable bean){
			list.add(bean);
			if(list.size() == batchSize){
				try {
					dao.batchSave(list);
					LOG.info("Save {} items", list.size());
				} catch (SQLException e) {
					LOG.error("Error saver.doSave ", e);
					/**
					 * if error, requeue
					 */
					if(e.getMessage().contains("No operations allowed")){
						for(Storable bean0: list) save(bean0);
					}
				}
				list.clear();
			}
		}
	}
	
	private class SaveForumTask implements Runnable{
		@Override
		public void run() {
			for(;;) doSave0();
		}
	}
	

	@SuppressWarnings({"rawtypes"})
	private Set<Class> getTableBeans() throws IOException, URISyntaxException{
		
		ClassLoader loader = getClass().getClassLoader();
		ClassPath cp;
		Set<ClassInfo> rss = null;
		try {
			cp = ClassPath.from(loader);
			rss = cp.getAllClasses();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Set<Class> results = Sets.newHashSet();
		
		for(ClassInfo info: rss){
			String className = info.getName();
			if(exclude(className))continue;
			Class clazz;
			try {
				clazz = loader.loadClass(className);
				if(Storable.class.isAssignableFrom(clazz)
						&& clazz != Storable.class){
					results.add(clazz);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return results;
	}
	
	private static boolean exclude(String input){
		for(String name: EXCLUDE_PACKAGE){
			if(input.startsWith(name)) return true;
		}
		return false;
	}
	
	
	public static final String[] EXCLUDE_PACKAGE = {
		"java.", "sun.", "javax.", "com.sun.", "org.apache.", "org.slf4j.", "com.google.",
		"org.springframework","org.w3c","org.dom4j","org.codehaus","org.xml","org.junit",
		"org.jsoup","org.hamcrest","org.aopalliance","junit","jodd","it.unimi","io.protostuff",
		"org.json","com.mysql","com.mchange","io."
	};
	
	public static void main(String...strings) throws SQLException{
		AsyncSuperDao dao = new AsyncSuperDao("jtyd_");
	}
}
