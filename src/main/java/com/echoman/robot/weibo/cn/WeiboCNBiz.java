package com.echoman.robot.weibo.cn;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.model.RobotBean;
import com.echoman.model.SendTasks;
import com.echoman.robot.RobotType;
import com.echoman.robot.weibo.model.FansKeywords;
import com.echoman.robot.weibo.model.WeiboUser;
import com.echoman.util.CommonUtil;
import com.echoman.util.Config;
import com.echoman.util.DataSourceFactory;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

public class WeiboCNBiz {
	
	private final static Logger LOG = LoggerFactory.getLogger(WeiboCNBiz.class);
	
	private static WeiboCNBiz scheduler = new WeiboCNBiz();
	
	public static WeiboCNBiz instance(){
		return scheduler;
	}
	
	private static final String TABLE_PREFIX = "jtyd_";
	
	Queue<RobotBean> accQueue = Queues.newArrayDeque();
	private BlockingQueue<WeiboUser> taskQueue = Queues.newLinkedBlockingQueue();
	private BlockingQueue<WeiboUser> completeQueue = Queues.newLinkedBlockingQueue();
	
	Set<WeiboUser> allUsers = Sets.newHashSet();
	
	private String feed;
	private volatile WeiboCNRobot currRobot;
	private WeiboCNDao dao;
	private WeiboCNTaskManager taskManager;
	
	public WeiboCNBiz(){
		dao = new WeiboCNDao(TABLE_PREFIX, 3);
		taskManager = new WeiboCNTaskManager(dao);
		accQueue.addAll(Config.getRobotBeans(RobotType.WEIBO));
		changeCurrRobot();
	}
	
	public WeiboCNBiz setFeed(String feed){
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
	
	public void checkAndChangeRobot(){
		if(currRobot.getRequestCount() > 200){
			LOG.info("Change account before: {}", currRobot.getAccount());
			changeCurrRobot();
			LOG.info("Change account after: {}", currRobot.getAccount());
		}
	}
	
	public int taskSize(){
		return taskQueue.size();
	}
	
	public int dbTaskSize(){
		return dao.taskLength();
	}
	
	public void addUser(WeiboUser user){
		
		if(!(taskQueue.contains(user) || completeQueue.contains(user))){
			if(taskQueue.add(user))
				LOG.info("AddUser: {}", user);
			else
				LOG.info("AddUser fail...");
		}
		else{
			LOG.info("Repeat user: {}", user);
		}
		
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
//		doBroadcast();
	}
	
	private void collectBySearch(){
		new 
		Thread(new SearchUserTask(), "SEARCH-USER-WORKER")
		.start();
		
		new 
		Thread(new FillANDSaveUserTask(), "FILL-USER-INFO-WORKER")
		.start();
	}
	
	/**
	 * Search user with given keywords
	 */
	private void doSearchUser(){
		
		FansKeywords kw = dao.getAndUpdateKeyword();
		
		if(kw == null){
			System.out.println("Waiting for keywords ...");
			CommonUtil.wait2(2000, 5000);
			return;
		}
		LOG.info("Get keyword: {}", kw);
		
		currRobot.searchUser(kw.getKeywords());
			
		CommonUtil.wait2(1000, 2000);
	}
	
	private void collectByGetFans(){
		WeiboUser user = dao.getAndUpdateWeiboUserD1();
		
		if(user == null){
			System.out.println("Waiting for keywords ...");
			CommonUtil.wait2(2000, 5000);
			return;
		}
		LOG.info("Get user: {}", user);
		
		currRobot.getFollows(user);
			
		CommonUtil.wait2(1000, 2000);
	}
	/**
	 * Complete user info and save it
	 */
	private void doFillAndSaveUser(){
		/**
		 * do not do much work at a time
		 */
		checkAndChangeRobot();
		
		try {

			WeiboUser user = takeUser();
			completeQueue.add(user);
			
			currRobot.fillUserInfo(user);
			dao.save(user);
			
			Thread.sleep(CommonUtil.random(1000, 2000));
			
		} catch (Exception e) {
			LOG.error("", e);
		}
	}

	/**
	 * collect user info start from a seed
	 */
	public void collectIDByTranverse(){
//		currRobot.getFollows(feed);
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
			
//			currRobot.getFollows(user.getUserId());
			Thread.sleep(CommonUtil.random(5000, 20000));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void doBroadcast(){
		
		SendTasks sendTasks = dao.getSendTasks();
		
		if(sendTasks == null){
			LOG.info("Waiting for sendTasks ...");
			CommonUtil.wait2(2000, 5000);
			return;
		}
		LOG.info("Get sendTask: {}", sendTasks);
		taskManager.sendMessage4GZH(sendTasks);
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
			for(;;) {
				try{
					doSearchUser();
//					collectByGetFans();
				}
				catch(Throwable e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private class FillANDSaveUserTask implements Runnable{
		@Override
		public void run() {
			for(;;){
				try{
					doFillAndSaveUser();
				}
				catch(Throwable e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private class BroadcastTask implements Runnable{
		@Override
		public void run() {
			
		}
	}
	
	public String stats(){
		StringBuffer sb = new StringBuffer();
		sb.append(DataSourceFactory.stats());
		return sb.toString();
	}
}
