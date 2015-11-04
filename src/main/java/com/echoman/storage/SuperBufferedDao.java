package com.echoman.storage;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.storage.Storable;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

public class SuperBufferedDao{
	
	private final static Logger LOG = LoggerFactory.getLogger(SuperBufferedDao.class);
		
	private BlockingQueue<Storable> taskQueue;
	private int batchSize = 2;
	SuperDao dao = new SuperDao();
	
	public SuperBufferedDao(){
		taskQueue = Queues.newLinkedBlockingQueue();
		startTasks();
	}

	private void startTasks() {
		Thread saveForumWorke = new Thread(new SaveForumTask());
		saveForumWorke.start();
	}

	public void save(Storable bean){
		if(!dao.exist(bean)) taskQueue.add(bean);
		else LOG.info("Exist forum: {}", bean);
	}
	
	private void doSave(){
		try {
			List<Storable> list = Lists.newArrayList();
			for(int i = 0; i < batchSize; i++){
				list.add(taskQueue.take());
			}
			dao.batchSave(list);
		} catch (Exception e) {
			LOG.error("Error doSaveForum, ", e);
		}
	}
	
	private class SaveForumTask implements Runnable{
		@Override
		public void run() {
			for(;;) doSave();
		}
	}
	
	public static void main(String...strings) throws SQLException{
		
	}
}
