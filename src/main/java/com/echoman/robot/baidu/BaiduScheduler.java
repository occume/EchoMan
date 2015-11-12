package com.echoman.robot.baidu;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.model.RobotBean;
import com.echoman.robot.RobotType;
import com.echoman.robot.baidu.model.BaiduUser;
import com.echoman.robot.baidu.model.BaiduForum;
import com.echoman.storage.AsyncSuperDao;
import com.echoman.util.CommonUtil;
import com.echoman.util.Config;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

public class BaiduScheduler {
	
	private final static Logger LOG = LoggerFactory.getLogger(BaiduScheduler.class);
	
	private static BaiduScheduler scheduler = new BaiduScheduler();
	
	public static BaiduScheduler instance(){
		return scheduler;
	}
	
	public BaiduScheduler(){
		asyncDao = new AsyncSuperDao();
		accQueue = Queues.newArrayDeque(Config.getRobotBeans(RobotType.BAIDU));
		initCurrRobot();
	}
	
	private AsyncSuperDao asyncDao;
	
	private Queue<RobotBean> accQueue = Queues.newArrayDeque();
	private volatile BaiduRobot currRobot;
	
	private Set<BaiduUser> allUsers = Sets.newHashSet();
	private BlockingQueue<BaiduUser> taskQueue = Queues.newLinkedBlockingQueue();
	
//	private Set<BaiduForum> allForums = Sets.newHashSet();
	
	private String seed;
	
	public BaiduScheduler setSeed(String seed){
		this.seed = seed;
		return this;
	}
	
	public BaiduRobot getCurrRobot(){
		return this.currRobot;
	}
	
	public void addForum(BaiduForum forum){
		currRobot.getForumInfo(forum);
		CommonUtil.wait2(1000, 3000);
		asyncDao.save(forum);
	}
	
	public void addAllForums(Set<BaiduForum> forums){
		for(BaiduForum forum: forums){
			addForum(forum);
		}
	}
	
	public void addUser(BaiduUser user){
		if(!allUsers.contains(user)){
			taskQueue.add(user);
		}
		if(allUsers.add(user)){
			String aa = user.getUserName() + "\t" + user.getUserId() + "\n";
			Path subPath = Paths.get("D:/tmp/53/baidu_user");
			try {
				Files.write(subPath, aa.getBytes("UTF-8"), StandardOpenOption.APPEND);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addAllUser(Set<BaiduUser> users){
		for(BaiduUser user: users){
			addUser(user);
		}
	}
	
	public void offUser(BaiduUser user){
		taskQueue.add(user);
	}
	
	public BaiduUser takeUser() throws InterruptedException{
		return taskQueue.take();
	}

	private void initCurrRobot() {
		changeRobot();
	}
	
	private void changeRobot(){
		
		RobotBean bean = accQueue.poll();
		BaiduRobot robot = new BaiduRobot(bean);
		robot.login();
		
		currRobot = robot;
		accQueue.offer(bean);
	}

	public void start(){
//		startReplyReplyMe();
		startStroll();
		
//		currRobot.getUserByName(seed);
//		startTraverse();
	}
	
	public void completeForum(){
		
	}
	
	public void startTraverse(){
		Thread worker = new Thread(new TraverseTask());
		worker.start();
	}
	
	private void doTraverse(){
		try {
			BaiduUser user = takeUser();
			currRobot.getUserByName(user.getUserName());
			Thread.sleep(CommonUtil.random(5000, 20000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private class TraverseTask implements Runnable{
		@Override
		public void run() {
			for(;;){
				doTraverse();
			}
		}
	}
	/**
	 * start stroll all
	 */
	public void startStroll(){
		try {
			stroll("红豆爱阿翁");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stroll(String forumName) throws Exception{
		
		List<String> tlist = currRobot.getPostsOfForum(forumName);
		
		LOG.info("Get {} threads", tlist.size());
		
		Collections.shuffle(tlist);
		int i = 0;
		
		for(String tid: tlist){
			if(i >= 3) break;
			String errCode = currRobot.doReply(tid, "nice work");
			if(errCode.equals("0")) i++;
			Thread.sleep(CommonUtil.random(2000, 10000));
		}
	}
	
	/**
	 * start auto reply
	 */
	private void startReplyReplyMe(){
		Thread worker = new Thread(new ReplyReplyMeTask());
		worker.start();
	}
	
	private void replyReplyMe(){
		try {
			currRobot.replyReplyme();
			Thread.sleep(CommonUtil.random(5 * 60 * 1000, 15 * 60 * 1000));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class ReplyReplyMeTask implements Runnable{
		@Override
		public void run() {
			for(;;){
				 replyReplyMe();
			}
		}
	}
}
