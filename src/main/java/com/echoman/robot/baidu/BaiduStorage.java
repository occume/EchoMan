package com.echoman.robot.baidu;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.robot.baidu.dao.BaiduForumDao;
import com.echoman.robot.baidu.model.BaiduForum;
import com.echoman.storage.Storable;
import com.echoman.util.DataSourceFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

public class BaiduStorage{
	
	private final static Logger LOG = LoggerFactory.getLogger(BaiduStorage.class);
	
	private BaiduForumDao forumDao;
	
	private BlockingQueue<BaiduForum> taskQueue;
	private int batchSize = 2;
	
	public BaiduStorage(){
		forumDao = new BaiduForumDao();
		taskQueue = Queues.newLinkedBlockingQueue();
		startTasks();
	}

	private void startTasks() {
		Thread saveForumWorke = new Thread(new SaveForumTask());
		saveForumWorke.start();
	}

	public void saveForum(BaiduForum forum){
		if(!forumDao.exist(forum)) taskQueue.add(forum);
		else LOG.info("Exist forum: {}", forum);
	}
	
	private void doSaveForum(){
		try {
			List<Storable> list = Lists.newArrayList();
			for(int i = 0; i < batchSize; i++){
				list.add(taskQueue.take());
			}
			forumDao.batchSave(list);
		} catch (Exception e) {
			LOG.error("Error doSaveForum, ", e);
		}
	}
	
	private class SaveForumTask implements Runnable{
		@Override
		public void run() {
			for(;;) doSaveForum();
		}
	}
	
	public static void main(String...strings) throws SQLException{
		
		System.out.println(DataSourceFactory.getDataSource());
		
		BaiduStorage storage = new BaiduStorage();
		BaiduForum forum = new BaiduForum("002", "JD", "123");
//		System.out.println(storage.exist(forum));
//		BaiduForum forum = new BaiduForum("001", "JD", "123");
//		forum.setMemberNum(99);
//		forum.setPostNum(999);
//		forum.setDesc("First one");
//		int id = storage.save(forum);
//		System.out.println("id = " + id);
		
//		String aaa = CommonUtil.underscoreName("BaiduForum");
//		System.out.println(aaa);
		
//		String a = "91F33907";
//		byte[] buf = BinaryUtil.HexStringToBinary(a);
//		ByteBuffer buffer = ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN);
//		buffer.put(buf);
//		buffer.flip();
//		System.out.println(buffer.getInt());
//		System.out.println(buffer.get());
//		System.out.println(buffer.get());
//		System.out.println(buffer.get());
//		System.out.println(buffer.get());
//		
//		System.out.println(0x91F33907);
//		
//		byte[] msgBuf = BinaryUtil.HexStringToBinary("91F33907");
//		for(int i = 0; i < msgBuf.length; i++){
//			System.out.print(msgBuf[i] + " ");
//		}
	}
}
