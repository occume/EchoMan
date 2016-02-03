package com.echoman.robot.jd;

import com.echoman.robot.AbstractRobot;
import com.echoman.robot.Robot;

public class JDRobot{
	
	private JDRobotHelper helper;
	
	public JDRobot(){
		helper = new JDRobotHelper();
	}

	public void login() {
//		String url = "https://passport.jd.com/uc/loginService?uuid=cb05cc49-0905-49db-9703-816d3d7223ec&ReturnUrl=http%3A%2F%2Fwww.jd.com%2F&r=0.045042300702584526&version=2015";
		helper.login();
	}

	public void test(){
		helper.test();
	}

	public static void main(String...strings){
		JDRobot robot = new JDRobot();
		robot.test();
	}
}
