package com.echoman.robot.weibo.cn;

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

public class WeiboCNScheduler {
	
	private final static Logger LOG = LoggerFactory.getLogger(WeiboCNScheduler.class);
	
	private static WeiboCNScheduler scheduler = new WeiboCNScheduler();
	
	public static WeiboCNScheduler instance(){
		return scheduler;
	}
	
	Queue<RobotBean> accQueue = Queues.newArrayDeque();
	private BlockingQueue<WeiboUser> taskQueue = Queues.newLinkedBlockingQueue();
	private BlockingQueue<WeiboUser> completeQueue = Queues.newLinkedBlockingQueue();
	
	Set<WeiboUser> allUsers = Sets.newHashSet();
	
	private String feed;
	private volatile WeiboCNRobot currRobot;
	private AsyncSuperDao asyncDao;
	
	public WeiboCNScheduler(){
		asyncDao = new AsyncSuperDao();
		accQueue.addAll(Config.getRobotBeans(RobotType.WEIBO));
		changeCurrRobot();
	}
	
	public WeiboCNScheduler setFeed(String feed){
		this.feed = feed;
		return this;
	}
	
	private void changeCurrRobot(){
		
		RobotBean bean = accQueue.poll();
		WeiboCNRobot robot = new WeiboCNRobot(bean);
		robot.login();
		
		currRobot = robot;
		accQueue.offer(bean);
	}
	
	public WeiboCNRobot getCurrRobot(){
		return this.currRobot;
	}
	
	private void checkAndChangeRobot(){
		if(currRobot.getRequestCount() > 200){
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
		
//		collectIDByTranverse();
		collectBySearch();
	}
	
	private void collectBySearch(){
		Thread t1 = new Thread(new SearchUserTask(), "SEARCH-USER-WORKER");
		Thread t2 = new Thread(new FillUserInfoTask(), "FILL-USER-INFO-WORKER");
		t1.start();
		t2.start();
	}
	
	private void doSearchUser(){
		String keyword = "宝妈";
		currRobot.searchUser(keyword);
	}
	
	private void doFillUserInfo(){
		/**
		 * do not do much work at a time
		 */
		checkAndChangeRobot();
		try {
//			if(completeQueue.contains(""))
			WeiboUser user = takeUser();
			completeQueue.add(user);
			
			currRobot.fillUserInfo(user);
			Thread.sleep(CommonUtil.random(2000, 5000));
		} catch (Exception e) {
			LOG.error("");
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
	
	private void doCollect(){
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
			for(;;) doCollect();
		}
	}
	
	private class SearchUserTask implements Runnable{
		@Override
		public void run() {
			for(;;) doSearchUser();
		}
	}
	
	private class FillUserInfoTask implements Runnable{
		@Override
		public void run() {
			for(;;) doFillUserInfo();
		}
	}
}
