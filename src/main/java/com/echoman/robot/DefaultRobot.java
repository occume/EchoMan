package com.echoman.robot;

import com.echoman.model.RobotBean;


public class DefaultRobot extends AbstractRobot {
	
	public static final DefaultRobot instance = new DefaultRobot();
	
	public DefaultRobot() {
	}

	public DefaultRobot(RobotBean bean) {
		super(bean);
	}

	@Override
	public Robot login() {
		return this;
	}

	@Override
	public boolean isLogin() {
		return false;
	}

	@Override
	public String getJSFileDirectory() {
		return null;
	}

	@Override
	public void sign() {
		
	}

}
