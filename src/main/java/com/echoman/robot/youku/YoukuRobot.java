package com.echoman.robot.youku;

import com.echoman.model.RobotBean;
import com.echoman.robot.AbstractRobot;
import com.echoman.robot.Robot;

public class YoukuRobot extends AbstractRobot {
	
	public YoukuRobot(RobotBean bean){
		super(bean);
	}

	@Override
	public Robot login() {
		return this;
	}

	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/youku/";
	}

	@Override
	public void sign() {
		
	}

	@Override
	public boolean isLogin() {
		// TODO Auto-generated method stub
		return false;
	}

}
