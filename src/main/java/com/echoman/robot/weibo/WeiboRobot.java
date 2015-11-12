package com.echoman.robot.weibo;

import java.util.Map;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.echoman.model.RobotBean;
import com.echoman.robot.AbstractRobot;
import com.echoman.robot.Robot;
import com.echoman.robot.weibo.model.WeiboUser;
import com.echoman.util.CommonUtil;
import com.echoman.util.DocUtil;

public class WeiboRobot extends AbstractRobot {
	
	private final static Logger LOG = LoggerFactory.getLogger(WeiboRobot.class);
	
	private String uniqueid;
	private String relationMyfollowHtml;
	private String searchHtml;
	
	private WeiboRobotHelper helper = new WeiboRobotHelper(this);
	
	public WeiboRobot(RobotBean bean){
		super(bean);
	}
	
	public WeiboRobot() {}

	@Override
	public Robot login() {
		helper.login();
		return this;
	}
	
	public void getMyFollows(){
		String url = "http://weibo.com/"+ uniqueid +"/follow?rightmod=1&wvr=6";
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.com");
		headers.put("Referer", "http://weibo.com/u/"+ uniqueid +"/home");
		
		String html = http.get(url, headers);
		String text = DocUtil.getScriptText1(html, "domid\":\"Pl_Official_RelationMyfollow");
		
		if(LOG.isDebugEnabled())
			LOG.debug("Text: {}", text);
		
		runCallback(new JsInvoker() {
			@Override
			public void invoke() {
				relationMyfollowHtml = bds.get("relationMyfollowHtml").toString();
			}
		}, "callback", text);
		
		Document doc = Jsoup.parse(relationMyfollowHtml);

		Elements elems = doc.select(".member_box .member_li .mod_info");
		
		for(Element e: elems){
			Element a = e.children().first().children().first();
			String name = a.text();
			String href = a.attr("href");
			System.out.println(name + " " + href);
		}
	}
	/**
	 * get follows by user id
	 * @param id
	 */
	public void getFollows(String id){
		for(int i = 1; i <= 5; i++){
			Set<WeiboUser> follows = helper.getFollows(id, i);
			System.out.println(i + "   ----------------");
			System.out.println(follows);
			if(follows.size() == 0) break;
			CommonUtil.wait2(1000, 3000);
			WeiboScheduler.instance().addAllUser(follows);
		}
		
	}
	/**
	 * search user on specify page
	 * @param keyword
	 * @param page
	 */
	public void searchUser(String keyword, int page){
		
		Set<WeiboUser> users = helper.searchUser(keyword, page);
		WeiboScheduler.instance().addAllUser(users);

	}
	/**
	 * search user of 1st page
	 * @param keyword
	 */
	public void searchUser(String keyword){
		
		searchUser(keyword, 1);

	}
	
	public void getUserInfo(){
		String url = "http://weibo.com/p/1005053057179881/info?mod=pedit_more";
		Map<String, String> headers = getGeneralHeaders();
		
		headers.put("Host", "weibo.com");
		headers.put("Referer", "http://weibo.com/u/"+ uniqueid +"/home");
		
		String html = http.get(url, headers);
	}
	
	public void home(){
		String url = "http://d.weibo.com/?from=signin";
		
		Map<String, String> headers = getGeneralHeaders();
		headers.put("Host", "d.weibo.com");
		headers.put("Referer", "http://weibo.com/");
		
		String html = http.get(url, headers);
		System.out.println(html);
	}
	
	public Map<String, String> getGeneralHeaders(){
		
		Map<String, String> hds = super.getGeneralHeaders();

		hds.put("Host", "login.sina.com.cn");
		hds.put("Referer", "http://weibo.com/");
		
		return hds;
	}

	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/weibo/";
	}

	@Override
	public void sign() {
		
	}
	
	public int getRequestCount(){
		return helper.getHttpClient().getRequestCount();
	}

	@Override
	public boolean isLogin() {
		return helper.isLogin();
	}
}
