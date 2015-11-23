package com.echoman.robot.weixin.bro;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.robot.AbstractHelper;
import com.echoman.robot.weixin.URLBuilder;
import com.echoman.robot.weixin.model.WeixinArticle;
import com.echoman.robot.weixin.model.WeixinGZH;
import com.echoman.util.CommonUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.hankcs.hanlp.HanLP;

public class WeixinBroRobotHelper extends AbstractHelper {
	
	private final static Logger LOG = LoggerFactory.getLogger(WeixinBroRobotHelper.class);
	
	private WeixinBroRobot robot;
	private ChromeDriver driver;
	
	public WeixinBroRobotHelper(WeixinBroRobot robot){
		System.setProperty("webdriver.chrome.driver", "D:\\lib\\Java\\NLP\\chromedriver.exe");
		driver = new ChromeDriver();
		this.robot = robot;
	}

	public Set<WeixinGZH> getGZHList(String keyword){
		
		String url = URLBuilder.gzhQueryURL(keyword, 1);
		LOG.info("Search with keyword: {}", keyword);
		
		driver.get(url);
		
		Set<WeixinGZH> gzhs = Sets.newHashSet();
		
		List<WebElement> elems = null;
        int type = 1;
        
        if(type == 1){
        	 elems = driver.findElements(By.cssSelector(".wx-rb_v1"));
        }
        LOG.info("Have search {} gzg", elems.size());
        
        for(WebElement elem: elems){
        	WebElement nameElem = elem.findElement(By.cssSelector(".txt-box h3"));
        	String weixinName = nameElem.getText();
        	
        	WebElement enameElem = elem.findElement(By.cssSelector(".txt-box h4"));
        	String weixinCode = enameElem.getText().substring(4);
        	
        	String detailUrl = elem.getAttribute("href");
        	
        	WeixinGZH gzh = new WeixinGZH(weixinName, weixinCode, detailUrl, "");
        	System.out.println(gzh);
        	/**
        	 * Here we only get the exactly matched gzh
        	 */
        	if(weixinName.equalsIgnoreCase(keyword)
        		|| weixinCode.equalsIgnoreCase(keyword))
        	gzhs.add(gzh);
        }
        
        return gzhs;
	}
	
	public Set<String> getArticleList(WeixinGZH gzh, int page){
		
		String url = "http://weixin.sogou.com" + gzh.getUrl() + "&page=" + page + "&t=" + System.currentTimeMillis();
		Set<String> urls = Sets.newHashSet();
		
		System.out.println("Reqeust: " + url);

		driver.get(url);
		mainWindow = driver.getWindowHandle();
		
		new WebDriverWait(driver, 20).until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				List<WebElement> elems = driver.findElements(By.cssSelector("#gzh_arts_1 .wx-rb .news_lst_tab"));
				return elems.size() > 0;
			}
		});
		
		driver.findElement(By.cssSelector("#sogou_webhelp")).click();
		new WebDriverWait(driver, 20).until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				Set<String> handles = driver.getWindowHandles();
				return handles.size() > 1;
			}
		});
		
		int pg = 1;
		do{
			openArticle(driver, pg++, gzh);
			driver.switchTo().window(mainWindow);
			WebElement more = driver.findElement(By.cssSelector("#wxmore a"));
			if(more != null && more.isDisplayed()){
				more.click();
				CommonUtil.wait2(2000, 3000);
			}
			else break;
		}
		while(true);
		
		return urls;
	}
	
	String mainWindow;
	
	private void openArticle(WebDriver driver, int page, WeixinGZH gzh){
		
		
		List<WebElement> elems = driver.findElements(By.cssSelector("#gzh_arts_"+ page +" .wx-rb .news_lst_tab"));
		Set<String> urls = Sets.newHashSet();
		
		for(WebElement elem0: elems){
			String url0 = elem0.getAttribute("href");
			urls.add(url0);
		}
		
		System.out.println("elems.size:		" + elems.size());
		System.out.println("urls.size:		" + urls.size());
		
		Set<String> windows = driver.getWindowHandles();
		WebDriver newWindow = null;
		
		for(String window: windows){
			if(!window.equals(mainWindow)){
				newWindow = driver.switchTo().window(window);
			}
		}
		
		for(String url0: urls){
			
			newWindow.get(url0);
			
			String url = newWindow.getCurrentUrl();
			
			for(;;){
				if(url.contains("antispider")){
					CommonUtil.wait2(1000, 2000);
					url = newWindow.getCurrentUrl();
					System.out.println("Waiting yzm...");
				}
				else break;
			}
			
			System.out.println(url);

			WebElement elem1 = newWindow.findElement(By.cssSelector("#activity-name"));
			String title = elem1.getText();
			System.out.println(title);
			
			List<WebElement> elems0 = newWindow.findElements(By.cssSelector("#js_content p span"));
			String text = "";
			for(WebElement elem0: elems0){
				
				text += elem0.getText();
				
			}
			
			List<String> keywordList = HanLP.extractKeyword(text, 5);	
			List<String> sentenceList = HanLP.extractSummary(text, 3);
			
			if(text.length() > 5000){
				text = text.substring(0, 5000);
			}
			
			WeixinArticle article = new WeixinArticle();
			article.setArticleContent(text);
			article.setArticleKeywords(Joiner.on(",").join(keywordList));
			article.setArticleDesc(Joiner.on(",").join(sentenceList));
			article.setArticleName(title);
			article.setArticleUrl(url);
			article.setWeixinName(gzh.getWeixinName());
			article.setWeixinCode(gzh.getWeixinCode());
			
			System.out.println(article);
			robot.saveArticle(article);
			
			CommonUtil.wait2(5000, 25000);
		}
	}
	
	private void fillArticle(String filed, Object fieldValue, WeixinArticle article){
		Method[] methods = WeixinArticle.class.getMethods();
		for(Method m: methods){
			if(m.getName().startsWith("set") && 
					m.getName().toLowerCase().contains(filed)){
				try {
					m.invoke(article, fieldValue);
				} catch (Exception e) {
					System.out.println(m.getName());
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	public WeixinArticle getArticle(String url, WeixinGZH gzh){
//		System.out.println(url);
		Map<String, String> 
		
		headers = getGeneralHeaders();
		headers.put("Host", "mp.weixin.qq.com");
		headers.put("Cache-Control", "no-cache");
		headers.put("Pragma", "no-cache");
		
		String html = http.get(url, headers);
		
		http.get("http://mp.weixin.qq.com/mp/report?action=pcclick&__biz=MzI4NzAyODE0Mg==&uin=&scene=10000001&r=0.7182390960764692", headers
				);
		
		Document doc = Jsoup.parse(html);
		
		Element titleElem = doc.getElementById("activity-name");
		String title = titleElem.text();
		
		Elements elems = doc.select("#js_content p span");
		String text = "";
		
		for(Element elem: elems){
//			elem.h
//			System.out.println(elem);
//			System.out.println(elem.tagName());
//			System.out.println(elem.html());
			if(elem.hasText()){
				text += elem.text();
			}
		}
		
//		System.out.println(text);
		
		List<String> keywordList = HanLP.extractKeyword(text, 10);
//		System.out.println(keywordList);
		
		List<String> sentenceList = HanLP.extractSummary(text, 10);
//		System.out.println(sentenceList);
		
		WeixinArticle article = new WeixinArticle();
		article.setArticleContent(text);
		article.setArticleKeywords(Joiner.on(",").join(keywordList));
		article.setArticleDesc(Joiner.on(",").join(sentenceList));
		article.setArticleName(title);
		article.setArticleUrl(url);
		article.setWeixinName(gzh.getWeixinName());
		article.setWeixinCode(gzh.getWeixinCode());
		
		System.out.println(article);
		
		return article;
	}
	
	public Map<String, String> getGeneralHeaders(){
		
		Map<String, String> hds = super.getGeneralHeaders();

		hds.put("Host", "weixin.sogou.com");
//		hds.add(new BasicHeader("Referer", "http://weixin.sogou.com/weixin?type=2&query=%E5%85%B0%E5%B7%9E&fr=sgsearch&ie=utf8&_ast=1421146752&_asf=null&w=01029901&p=40040100&dp=1&cid=null"));
//		hds.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0");
		hds.put("Cookie", "ABTEST=1|1447899525|v1; IPLOC=CN3100; SUID=9196983D6F1C920A00000000564D3185; PHPSESSID=dkrc9rs38er2hk4enodu6gq920; SUIR=1447899525; SUV=00BF25C73D989691564D3185EEA49974; SNUID=F8FFF054686D4CB7B0897BCC697BD138; SUID=9196983D1508990A00000000564D6BB7;");
		return hds;
	}

	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/weixin/";
	}
}
