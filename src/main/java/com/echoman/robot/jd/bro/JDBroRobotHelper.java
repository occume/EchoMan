package com.echoman.robot.jd.bro;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.robot.AbstractHelper;
import com.echoman.robot.jd.JDDocParser;
import com.echoman.robot.jd.model.RecommendProduct;
import com.echoman.robot.jd.model.TopTaobao;
import com.echoman.storage.AsyncSuperDao;
import com.echoman.util.CommonUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class JDBroRobotHelper extends AbstractHelper {
	
	private final static Logger LOG = LoggerFactory.getLogger(JDBroRobotHelper.class);
	
	private JDBroRobot robot;
	private ChromeDriver driver;
	private int scrollTop;
	private AsyncSuperDao dao = new AsyncSuperDao("sph_", 3);
	
	public JDBroRobotHelper(JDBroRobot robot){
		System.setProperty("webdriver.chrome.driver", "D:\\IDEA\\JTYD\\resources\\chromedriver.exe");
		driver = new ChromeDriver();
		this.robot = robot;
	}
	
	public void login(){
		
		String url = "http://media.jd.com/";
		LOG.info("Launcher browser");
		
		driver.get(url);
		
		WebElement loginFormFrame = driver.findElement(By.cssSelector("#login-reg iframe"));
		System.out.println(loginFormFrame);
		
		driver.switchTo().frame(loginFormFrame);
		
		WebElement loginName = driver.findElement(By.cssSelector("#loginname"));
		WebElement loginPwd = driver.findElement(By.cssSelector("#nloginpwd"));
		WebElement loginSubmit = driver.findElement(By.cssSelector("#paipaiLoginSubmit"));
		
		loginName.sendKeys("toyaowu@163.com");
		loginPwd.sendKeys("5580730yaowu");

		loginSubmit.click();
		
		/**
		 * 等待登录成功
		 */
		new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				List<WebElement> elems = driver.findElements(By.cssSelector(".index-summary-item"));
				return elems.size() > 0;
			}
		});
	}
	
	private List<TopTaobao> getGrabKeywords(){
//		String sql = "select id, name as keyword from t_catalog where id > 178";
		String sql = "select id, name as keyword from t_catalog";
		List<TopTaobao> list = Lists.newArrayList();
		try {
			 list = dao.superDao().getBeans(sql, TopTaobao.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public void getProductList(){
		List<TopTaobao> list = getGrabKeywords();
		for(TopTaobao tt: list){
			for(int i = 1; i <= 10; i++){
				String keyword = tt.getKeyword();
				if(keyword.contains("、")){
					String[] kws = keyword.split("、");
					for(String kw: kws){
						getProductListByPage(i, kw);
						CommonUtil.wait2(1000, 10000);
					}
				}
				else{
					getProductListByPage(i, tt.getKeyword());
				}
				CommonUtil.wait2(1000, 10000);
			}
			CommonUtil.wait2(1000, 10000);
		}
	}
	
	public void getProductListByPage(int page, String keyword){
		
		String searchUrl = "http://media.jd.com/gotoadv/goods?"
				+ "pageIndex=" + page
				+ "&pageSize=50"
				+ "&property=inOrderComm30Days&sort=desc&adownerType=&pcRate=&wlRate=&category=&category1=0&condition=0&"
				+ "keyword=" + keyword;
		
		driver.get("http://media.jd.com/gotoadv/goods?pageIndex="+ page +"&pageSize=50");
		driver.get(searchUrl);
		
		WebElement notice = driver.findElement(By.cssSelector("body > div.wrapper > div.notice-fixed > div.hd.clearfix > div.pull-right > a"));
		notice.click();

		scrollTop = 600;
		
		List<WebElement> trs = driver.findElements(By.cssSelector("table tbody tr"));
		for(WebElement tr: trs){
			
			scrollTop += 150;
//			LOG.info("ScrollTop: {}", scrollTop);
			driver.executeScript("document.body.scrollTop=" + scrollTop);
			List<WebElement> tds = tr.findElements(By.tagName("td"));
			if(tds.size() < 8) continue;
			
			RecommendProduct product = parseProductInfo(tds);
			
			WebElement spreadTd = tds.get(7);
			String recommendUrl = "";

			try{
				recommendUrl = doGetProductList(spreadTd);
			}catch(Throwable e){
				e.printStackTrace();
				String message = e.getMessage();
				if(		message.contains("introduce.htm")
					||	message.contains("navbar-collapse"))
				{
					LOG.info("Navbar ScrollTop: {}", scrollTop);
					scrollTop -= 300;
					driver.executeScript("document.body.scrollTop=" + scrollTop);
					recommendUrl = doGetProductList(spreadTd);
				}
				if(message.contains("images/bell.png")){
					LOG.info("Bell ScrollTop: {}", scrollTop);
					scrollTop += 300;
					driver.executeScript("document.body.scrollTop=" + scrollTop);
					doGetProductList(spreadTd);
				}
				if(message.contains("getcode")){
					closeModal();
				}
				CommonUtil.wait2(1000, 3000);
			}
			
			product.setRecommendUrl(recommendUrl);
			product.setGrabKeyword(keyword);
			product.setGrabDate(new Date());
			
			getProductDetail(product);
			
			if(!Strings.isNullOrEmpty(recommendUrl)){
				System.out.println(product);
				dao.save(product);
			}
			else{
				
			}
			
		}
	}
	
	private RecommendProduct parseProductInfo(List<WebElement> tds){
		
		RecommendProduct product = new RecommendProduct();
		/**
		 * 商品名和店铺名
		 */
		WebElement td = tds.get(0);
		String selector = "div > p:nth-child(1) > a";
		WebElement nameElement = td.findElement(By.cssSelector(selector));
		
		selector = "div > p.shop-name > a";
		WebElement shopElement = null;
		try{
			shopElement = td.findElement(By.cssSelector(selector));
		}catch(Throwable e){
			selector = "div > p.shop-name > em";
			shopElement = td.findElement(By.cssSelector(selector));
		}
		/**
		 * 价格
		 */
		td = tds.get(1);
		String price = td.getText();
		/**
		 * 佣金比例
		 */
		td = tds.get(2);
		String ratioText = td.getText();
		
		/**
		 * 佣金
		 */
		td = tds.get(3);
		String commission = td.getText();
		/**
		 * 30天订单数
		 */
		td = tds.get(4);
		String orders = td.getText();
		/**
		 * 30天累计支出
		 */
		td = tds.get(5);
		String totalPrice = td.getText();
		/**
		 * 时间范围
		 */
		td = tds.get(6);
		String timeRange = td.getText();
		
		product.setProductName(nameElement.getText());
		product.setUrl(nameElement.getAttribute("href"));
		product.setPrice(price);
		product.setShopName(shopElement.getText());
		product.setPcRatio(ratioText);
		product.setPcCommission(commission);
		product.setOrders(orders);
		product.setTotalPrice(totalPrice);
		product.setTimeRange(timeRange);
		
		return product;
	}
	
	private String doGetProductList(WebElement spreadTd){
		
		WebElement spreadA = spreadTd.findElement(By.cssSelector("a"));
		spreadA.click();
		
		new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				List<WebElement> elems = driver.findElements(By.cssSelector(".modal-dialog"));
				return elems.get(0).isDisplayed();
			}
		});
		
		
		WebElement codeWin = driver.findElement(By.id("unionWebId"));
		Select select = new Select(codeWin);
		select.selectByVisibleText("精通有道");
		
		WebElement staticCodeType2 = driver.findElement(By.id("staticCodeType_2"));
		staticCodeType2.click();
		
		WebElement spaceName = driver.findElement(By.id("spaceName"));
		new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				WebElement spaceName = driver.findElement(By.id("spaceName"));
				List<WebElement> elems = spaceName.findElements(By.tagName("option"));
				return elems.size() > 1;
			}
		});
		Select select1 = new Select(spaceName);
		select1.selectByValue("392612005");
//		List<WebElement> options = select1.getAllSelectedOptions();
//		for(WebElement option: options){
//			option.getText().
//		}
		
		WebElement getcodeBtn = driver.findElement(By.id("getcode-btn"));
		getcodeBtn.click();
		
		WebElement targetCode = driver.findElement(By.id("targetCode"));
		
		new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				WebElement targetCode = driver.findElement(By.id("targetCode"));
				String val = targetCode.getAttribute("value");
				return val.length() > 0;
			}
		});
		
		String link = targetCode.getAttribute("value");
		
		try {
			Files.write(Paths.get("D:/tmp/jd_spread"), link.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		closeModal();
		
		return link;
	}
	
	private void closeModal(){
		try{
			closeModal0();
		}
		catch(Throwable e){
			e.printStackTrace();
		}
	}
	
	private void closeModal0(){
		
		final WebElement dilogCloseBtn = driver.findElement(By.cssSelector("#getcode > div > div > div.modal-header > button > span"));
		new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return dilogCloseBtn.isDisplayed();
			}
		});
		/**
		 * 1.点击旁边关闭
		 * 2.使用post请求数据，不是ui
		 */
		dilogCloseBtn.click();
		
		new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return !dilogCloseBtn.isDisplayed();
			}
		});
	}

	public void getProductDetail(RecommendProduct product){
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "item.jd.com");
		
		String html = http.get(product.getUrl(), headers);
		
		if(html == null){
			System.err.println("No html, location: " + http.getLocation());
			return;
		}
		
		try{
			JDDocParser.parseCategory(html, product);
		}catch(Throwable e){
			System.out.println(product.getUrl());
			e.printStackTrace();
		}
	}
	
	public void test(){}

	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/weixin/";
	}
	
	public static void main(String...strings) throws UnsupportedEncodingException{

		List<TopTaobao> list = new JDBroRobotHelper(null).getGrabKeywords();
		System.out.println(list);
	}
}
