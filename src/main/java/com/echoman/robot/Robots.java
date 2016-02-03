package com.echoman.robot;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.echoman.SpringContext;
import com.echoman.model.RobotBean;
import com.echoman.robot.baidu.BaiduRobot;
import com.echoman.robot.hujiang.HujiangRobot;
import com.echoman.robot.qq.QQRobot;
import com.echoman.util.Config;

public class Robots {
	
	private final static Logger LOG = LoggerFactory.getLogger(Robots.class);
	
	private static ConcurrentHashMap<String, Robot> robots = new ConcurrentHashMap<>();
	
	public Robot getRobot(String vender, String owner){
		return robots.get(rename(vender, owner));
	}
	
	public void enroll(String vender, String owner, Robot robot){
		robots.put(rename(vender, owner), robot);
	}
	
	private String rename(String vender, String owner){
		return owner + "@" + vender;
	}
	
	@PostConstruct
	public void onStart(){
		
		Set<RobotBean> robots = null;
		try {
			robots = Config.getObjects(RobotBean.class);
		} catch (Exception e) {
			LOG.error("Fail to get RobotBeans, {}", e);
			return;
		}
		
//		for(RobotBean bean: robots){
//			enroll(bean.getType(), bean.getAccount(), newRobot(bean));
//		}
		
		LOG.info("{} robots are enrolled: {}", robots.size(), robots);
	}
	
	
	
	@Scheduled(cron = "0 30 10 * * *")
	public void backgroundSign(){
		
		LOG.info("------ Start sign at {}", new Date());
		
		for(Robot robot: robots.values()){
			robot.backgroundSign();
		}
		
		LOG.info("------ End sign at {}", new Date());
	}
	
	@Scheduled(cron = "0 0/10 6-23 * * *")
	public void backgroundProcess(){
		LOG.info("++++++ Start wander at {}; {} robots", new Date(), robots.size());

		for(Robot robot: robots.values()){
			robot.backgroundProcess();
		}
		
		LOG.info("++++++ End wander at {}", new Date());
	}
}
