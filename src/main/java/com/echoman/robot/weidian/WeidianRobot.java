package com.echoman.robot.weidian;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeidianRobot {
	
	private final static Logger LOG = LoggerFactory.getLogger(WeidianRobot.class);

	WeidianHelper helper = new WeidianHelper();
	
	public void login(){
		helper.login();
	}
	
	public void getListByKeyWord(){
		helper.getListByKeyWord();
	}
}
