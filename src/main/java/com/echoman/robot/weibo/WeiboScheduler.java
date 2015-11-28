package com.echoman.robot.weibo;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.model.RobotBean;
import com.echoman.robot.RobotType;
import com.echoman.robot.weibo.model.WeiboUser;
import com.echoman.storage.AsyncSuperDao;
import com.echoman.util.CommonUtil;
import com.echoman.util.Config;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

public class WeiboScheduler {
	
	private final static Logger LOG = LoggerFactory.getLogger(WeiboScheduler.class);
	
	private static WeiboScheduler scheduler = new WeiboScheduler();
	
	public static WeiboScheduler instance(){
		return scheduler;
	}
	
	Queue<RobotBean> accQueue = Queues.newArrayDeque();
	private BlockingQueue<WeiboUser> taskQueue = Queues.newLinkedBlockingQueue();
	private BlockingQueue<WeiboUser> completeQueue = Queues.newLinkedBlockingQueue();
	
	Set<WeiboUser> allUsers = Sets.newHashSet();
	
	private String feed;
	private volatile WeiboRobot currRobot;
	private AsyncSuperDao asyncDao;
	
	public WeiboScheduler(){
		asyncDao = new AsyncSuperDao("");
		accQueue.addAll(Config.getRobotBeans(RobotType.WEIBO));
		changeCurrRobot();
	}
	
	public WeiboScheduler setFeed(String feed){
		this.feed = feed;
		return this;
	}
	
	private void changeCurrRobot(){
		
//		WeiboRobot oldRobot = currRobot;
		
		RobotBean bean = accQueue.poll();
		WeiboRobot robot = new WeiboRobot(bean);
		robot.login();
		
		currRobot = robot;
		accQueue.offer(bean);
	}
	
	public WeiboRobot getCurrRobot(){
		return this.currRobot;
	}
	
	private void checkAndChangeRobot(){
		if(currRobot.getRequestCount() > 100){
			LOG.info("Change account before: {}", currRobot.getAccount());
			changeCurrRobot();
			LOG.info("Change account after: {}", currRobot.getAccount());
		}
	}
	
	public void addUser(WeiboUser user){
		
		if(!(taskQueue.contains(user) || completeQueue.contains(user))){
			taskQueue.add(user);
		}
		
		if(allUsers.add(user)) asyncDao.save(user);
	}
	
	public void addAllUser(Set<WeiboUser> users){
		for(WeiboUser user: users){
			addUser(user);
		}
	}
	
	public WeiboUser takeUser() throws InterruptedException{
		return taskQueue.take();
	}
	
	public void addIDTask(WeiboUser task){
		taskQueue.add(task);
	}
	
	public void start(){
		
		collectIDByTranverse();
//		collectIDBySearch();
	}
	
	private void collectIDBySearch(){
		String keyword = "";
		for(int i = 0; i < 20; i++){
			currRobot.searchUser(keyword, i);
		}
	}

	/**
	 * collect user info start from a seed
	 */
	public void collectIDByTranverse(){
		currRobot.getFollows(feed);
		Thread worker = new Thread(new CollectIDTask());
		worker.start();
	}
	
	private void doCollectID(){
		/**
		 * do not do much work at a time
		 */
		checkAndChangeRobot();
		try {
//			if(completeQueue.contains(""))
			WeiboUser user = takeUser();
			completeQueue.add(user);
			
			currRobot.getFollows(user.getUserId());
			Thread.sleep(CommonUtil.random(5000, 20000));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class CollectIDTask implements Runnable{
		@Override
		public void run() {
			for(;;) doCollectID();
		}
	}
}
