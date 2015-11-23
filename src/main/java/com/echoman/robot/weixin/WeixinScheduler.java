package com.echoman.robot.weixin;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.model.RobotBean;
import com.echoman.robot.RobotType;
import com.echoman.robot.weibo.model.FansKeywords;
import com.echoman.robot.weibo.model.WeiboUser;
import com.echoman.robot.weixin.model.WeixinArticle;
import com.echoman.robot.weixin.model.WeixinGZH;
import com.echoman.storage.AsyncSuperDao;
import com.echoman.util.CommonUtil;
import com.echoman.util.Config;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

public class WeixinScheduler {
	
	private final static Logger LOG = LoggerFactory.getLogger(WeixinScheduler.class);
	
	private static WeixinScheduler scheduler = new WeixinScheduler();
	
	public static WeixinScheduler instance(){
		return scheduler;
	}
	
	private static final String TABLE_PREFIX = "jtyd_";
	
	Queue<RobotBean> accQueue = Queues.newArrayDeque();
	private BlockingQueue<WeixinArticle> taskQueue = Queues.newLinkedBlockingQueue();
	private BlockingQueue<WeixinArticle> completeQueue = Queues.newLinkedBlockingQueue();
	
	Set<WeiboUser> allUsers = Sets.newHashSet();
	
	private String feed;
	private volatile WeixinRobot currRobot;
	private AsyncSuperDao asyncDao;
	
	public WeixinScheduler(){
		asyncDao = new AsyncSuperDao(TABLE_PREFIX);
		accQueue.addAll(Config.getRobotBeans(RobotType.WEIBO));
		changeCurrRobot();
	}
	
	public WeixinScheduler setFeed(String feed){
		this.feed = feed;
		return this;
	}
	
	private void changeCurrRobot(){
		
		RobotBean bean = accQueue.poll();
		WeixinRobot robot = new WeixinRobot();
		currRobot = robot;
	}
	
	public WeixinRobot getCurrRobot(){
		return this.currRobot;
	}
	
	private void checkAndChangeRobot(){
//		if(currRobot.getRequestCount() > 200){
//			LOG.info("Change account before: {}", currRobot.getAccount());
//			changeCurrRobot();
//			LOG.info("Change account after: {}", currRobot.getAccount());
//		}
	}
	
	public void addArticle(WeixinArticle article){
		
		asyncDao.save(article);
		
	}
	
	public void addAllArticle(Set<WeixinArticle> users){
		for(WeixinArticle user: users){
			addArticle(user);
		}
	}
	
	public WeixinArticle takeUser() throws InterruptedException{
		return taskQueue.take();
	}
	
	public void addIDTask(WeixinArticle task){
		taskQueue.add(task);
	}
	
	public void start(){
		
//		collectIDByTranverse();
		collectBySearch();
	}
	
	private void collectBySearch(){
		Thread t1 = new Thread(new SearchUserTask(), "SEARCH-USER-WORKER");
		t1.start();
	}
	
	private void doSearchUser(){
		
		String getSql = "select * from jtyd_weixin_user where del_flag = 0 limit 1";
		WeixinGZH kw = null;
		try {
			
			kw = asyncDao.superDao().getBean(getSql, WeixinGZH.class);

			if(kw == null) return;
			
			String updateSql = "update jtyd_weixin_user set del_flag = 1 where id = ?";
			asyncDao.superDao().update(updateSql, new Object[]{kw.getId()});
			System.out.println("Seed: " + kw);
			
			currRobot.searchGZH(kw.getWeixinCode());
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		CommonUtil.wait2(1500, 25000);
	}
	
	private class SearchUserTask implements Runnable{
		@Override
		public void run() {
			for(;;) {doSearchUser();}
		}
	}
}
