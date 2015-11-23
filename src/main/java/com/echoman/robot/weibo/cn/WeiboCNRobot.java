package com.echoman.robot.weibo.cn;

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

public class WeiboCNRobot extends AbstractRobot {
	
	private final static Logger LOG = LoggerFactory.getLogger(WeiboCNRobot.class);
	
	private String uniqueid;
	private String relationMyfollowHtml;
	private String searchHtml;
	
	private WeiboCNRobotHelper helper = new WeiboCNRobotHelper(this);
	
	public WeiboCNRobot(RobotBean bean){
		super(bean);
	}
	
	public WeiboCNRobot() {}

	@Override
	public Robot login() {
		helper.doLoginCN();
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
			WeiboCNScheduler.instance().addAllUser(follows);
		}
		
	}
	/**
	 * search user on specify page
	 * @param keyword
	 * @param page
	 */
	public void searchUser(String keyword, int page){
		
		Set<WeiboUser> users = helper.doSearchUserCN(keyword, page);
		WeiboCNScheduler.instance().addAllUser(users);
		
	}
	/**
	 * search user of 1st page
	 * @param keyword
	 */
	public void searchUser(String keyword){
		
		for(int i = 1; i <= 50; i++){
			Set<WeiboUser> users = helper.doSearchUserCN(keyword, i);
			LOG.info("Search {} users", users.size());
			LOG.info("FillUserInfo.Queue.size: {}", WeiboCNScheduler.instance().taskSize());
			LOG.info("SuperDao.Queue.size: {}", WeiboCNScheduler.instance().dbTaskSize());
			if(users.size() == 0) break;
			
			WeiboCNScheduler.instance().addAllUser(users);
			CommonUtil.wait2(10000, 30000);
//			for(WeiboUser user: users){
//				fillUserInfo(user);
//				System.out.println(user);
//			}
//			break;
		}

	}
	
	public void fillUserInfo(WeiboUser user){
		
		helper.doFillUserInfo(user);
		CommonUtil.wait2(1000, 2000);
		helper.doFillUserInfo1(user);
		LOG.info(user.toString());
	}
	
	public void loginCN(){
		
		helper.doLoginCN();
		
	}
	
	public void chatUser(String uid, String content){
		
		helper.doChatUser(uid, content);
		
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
