package com.echoman.robot.jd.bro;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.echoman.robot.weixin.model.WeixinArticle;
import com.echoman.robot.weixin.model.WeixinGZH;
import com.echoman.util.CommonUtil;

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
