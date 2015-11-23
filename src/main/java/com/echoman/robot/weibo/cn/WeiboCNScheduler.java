package com.echoman.robot.weibo.cn;

import java.sql.SQLException;
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
	
	private static final String TABLE_PREFIX = "jtyd_";
	
	Queue<RobotBean> accQueue = Queues.newArrayDeque();
	private BlockingQueue<WeiboUser> taskQueue = Queues.newLinkedBlockingQueue();
	private BlockingQueue<WeiboUser> completeQueue = Queues.newLinkedBlockingQueue();
	
	Set<WeiboUser> allUsers = Sets.newHashSet();
	
	private String feed;
	private volatile WeiboCNRobot currRobot;
	private AsyncSuperDao asyncDao;
	
	public WeiboCNScheduler(){
		asyncDao = new AsyncSuperDao(TABLE_PREFIX, 3);
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
	
	public int taskSize(){
		return taskQueue.size();
	}
	
	public int dbTaskSize(){
		return asyncDao.taskLength();
	}
	
	public void addUser(WeiboUser user){
		
		if(!(taskQueue.contains(user) || completeQueue.contains(user))){
			taskQueue.add(user);
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
		
		String getSql = "select * from jtyd_fans_keywords where del_flag = 0 limit 1";
		FansKeywords kw = null;
		try {
			
			kw = asyncDao.superDao().getBean(getSql, FansKeywords.class);
			
			if(kw == null){
				System.out.println("Waiting for keywords ...");
				CommonUtil.wait2(2000, 5000);
			}
			
			String updateSql = "update jtyd_fans_keywords set del_flag = 1 where id = ?";
			asyncDao.superDao().update(updateSql, new Object[]{kw.getId()});
			System.out.println(kw);
			
			currRobot.searchUser(kw.getKeywords());
			
		} catch (Exception e) {
			LOG.error("Error when search user ", e);
		}
		
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
//			System.out.println("4>>> " + user);
			asyncDao.save(user);
			
			Thread.sleep(CommonUtil.random(1000, 2000));
			
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
	
	/**
	 * 
	 */
	public void doBroadcast(){
		String getSql0 = "select * from jtyd_send_tasks limit 1";
		
		try {
			SendTasks task = asyncDao.superDao().getBean(getSql0, SendTasks.class);
			System.out.println(task);
			
			String getSql1 = "select * from jtyd_weibo_user where grab_tag = '"+ task.getFansKeywords() +"' limit 1";
			WeiboUser user = asyncDao.superDao().getBean(getSql1, WeiboUser.class);
			System.out.println(user);
			
			RobotBean bean = new RobotBean("WEIBO", task.getUserName(), task.getUserPassword());
			WeiboCNRobot robot = new WeiboCNRobot(bean);
			robot.login();
			
			robot.chatUser(user.getUserId(), "恭喜发财");
			
		} catch (SQLException e) {
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
	
	private class FillANDSaveUserTask implements Runnable{
		@Override
		public void run() {
			for(;;) doFillAndSaveUser();
		}
	}
	
	private class BroadcastTask implements Runnable{
		@Override
		public void run() {
			
		}
	}
}
