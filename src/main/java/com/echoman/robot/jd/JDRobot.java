package com.echoman.robot.jd;

import com.echoman.AbstractRobot;

public class JDRobot extends AbstractRobot {

	@Override
	public void login() {
		String url = "https://passport.jd.com/uc/loginService?uuid=cb05cc49-0905-49db-9703-816d3d7223ec&ReturnUrl=http%3A%2F%2Fwww.jd.com%2F&r=0.045042300702584526&version=2015";
	}

	public void test(){
		String url = "https://passport.jd.com/uc/rememberMeCheck?r=0.4092557379655254&version=2015";
		String html = http.get(url);
		System.out.println(html);
	}
	public static void main(String...strings){
		JDRobot robot = new JDRobot();
		robot.test();
	}
}
