package com.echoman.robot.jd;

import com.echoman.robot.AbstractRobot;
import com.echoman.robot.Robot;

public class JDRobot extends AbstractRobot {

	@Override
	public Robot login() {
		String url = "https://passport.jd.com/uc/loginService?uuid=cb05cc49-0905-49db-9703-816d3d7223ec&ReturnUrl=http%3A%2F%2Fwww.jd.com%2F&r=0.045042300702584526&version=2015";
		return this;
	}

	public void test(){
		String url = "https://passport.jd.com/uc/rememberMeCheck?r=0.4092557379655254&version=2015";
		String html = http.get(url);
		System.out.println(html);
	}
	
	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/jd/";
	}
	
	public static void main(String...strings){
		JDRobot robot = new JDRobot();
		robot.test();
	}

	@Override
	public void sign() {
		
	}
}
