package com.echoman.robot.jd.bro;

public class JDBroRobot {

	private JDBroRobotHelper helper = new JDBroRobotHelper(this);
	
	public void login(){
		helper.login();
	}
	
	public void fetchProductList(){
		helper.getProductList();
	}
	
	public void test(){
		
		helper.test();
		
	}
	
	public static void main(String[] args) {
		JDBroRobot robot = new JDBroRobot();
		robot.login();
		robot.fetchProductList();
	}
}
