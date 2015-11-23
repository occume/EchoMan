package com.echoman.robot.weixin.bro;

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

public class WeixinBroRobot {
	
	public static void htmlUnit(){
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.setJavascriptEnabled(true);
		
		driver.get("http://weixin.sogou.com/weixin?type=1&query=cctv&ie=utf8");
		
		List<WebElement> elems = driver.findElementsByCssSelector(".txt-box h3");
		String name = elems.get(0).getText();
		System.out.println(name);
		System.out.println(driver.getTitle());
	}

	private static void ff(){
//		System.setProperty("webdriver.chrome.driver",
//				"D:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
//				"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
		
		System.setProperty("webdriver.firefox.bin",
				"C:/Program Files (x86)/Mozilla Firefox/firefox.exe");
		FirefoxDriver driver = new FirefoxDriver();
//		ChromeDriver driver1 = new ChromeDriver();

		driver.get("http://weixin.sogou.com/");
//		driver.
		System.out.println(driver.getTitle());
//		WebElement element = driver.findElement(By.name("q"));
//		element.sendKeys("水龙头");
//		element.submit();
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		String content = driver.getPageSource();
//		driver.quit();
//		System.out.println(content);
	}
	

	private WeixinBroRobotHelper helper = new WeixinBroRobotHelper(this);
	
	public void saveArticle(WeixinArticle article){
		WeixinBroScheduler.instance().addArticle(article);
	}
	
	public void searchGZH(String keyword){
		
		Set<WeixinGZH> gzhs = helper.getGZHList(keyword);
		
		for(WeixinGZH gzh: gzhs){
			getArticleList(gzh);
			break;
		}
		
	}
	
	public void getArticleList(WeixinGZH gzh){
		
		CommonUtil.wait2(2000, 6000);
		
		Set<String> urls = helper.getArticleList(gzh, 1);
//		int totalPage = gzh.getTotalPage();
//		getArticle(urls, gzh);
//		
//		for(int page = 2; page <= totalPage; page++){
//			urls = helper.getArticleList(gzh, page);
//			getArticle(urls, gzh);
//		}
		
	}
	
	public void getArticle(Set<String> urls, WeixinGZH gzh){
		
		for(String url: urls){
			WeixinArticle article = helper.getArticle(url, gzh);
			WeixinBroScheduler.instance().addArticle(article);
			CommonUtil.wait2(50000, 80000);
		}
	}
	
	public static void webdriver(){
		System.setProperty("webdriver.chrome.driver",
				"D:\\lib\\Java\\NLP\\chromedriver.exe");
//		ChromeDriverService chromeDriverService = 
//				new ChromeDriverService(new File("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe"), 
//						port, args, environment)
		WebDriver dv = new ChromeDriver();
		dv.get("http://weixin.sogou.com");
		String title = dv.getTitle();
		System.out.println(title);
	}
	
	public static void main(String[] args) {
		WeixinBroRobot robot = new WeixinBroRobot();
		robot.searchGZH("xyjj0002");
	}
}
