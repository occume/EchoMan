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

	/**
	 * get follows by user id
	 * @param id
	 */
	public void getFollows(WeiboUser user){
		
		for(int i = 1; i <= 20; i++){
			Set<WeiboUser> follows = helper.getFollows(user, i);
			LOG.info("GetFollows of {}, page {}", user.getUserId(), i);
			LOG.info("Get {} fans: {}", follows.size(), follows.toString());
			if(follows.size() == 0) break;
			WeiboCNBiz.instance().addAllUser(follows);
			CommonUtil.wait2(20000, 30000);
		}
		
	}
	/**
	 * search user on specify page
	 * @param keyword
	 * @param page
	 */
	public void searchUser(String keyword, int page){
		
		Set<WeiboUser> users = helper.doSearchUserCN(keyword, page);
		WeiboCNBiz.instance().addAllUser(users);
		
	}
	
	private static int[] sortTypes = {0, 108};
	private static String[] filterTypes = {"stag", "isv", "all"};
	
	/**
	 * search user of 1st page
	 * @param keyword
	 */
	public void searchUser(String keyword){
		
		for(String filter: filterTypes){
			for(int sort: sortTypes){
				for(int i = 1; i <= 50; i++){
					Set<WeiboUser> users = helper.doSearchUserCN(keyword, sort, filter, i);
					LOG.info("Page {}, Search {} users: {}", new Object[]{i, users.size(), users});
					LOG.info("FillUserInfo.Queue.size: {}", WeiboCNBiz.instance().taskSize());
//					LOG.info("SuperDao.Queue.size: {}", WeiboCNScheduler.instance().dbTaskSize());
//					LOG.info(WeiboCNScheduler.instance().stats());
					if(users.size() == 0) break;
					
					WeiboCNBiz.instance().addAllUser(users);
					CommonUtil.wait2(30000, 50000);
				}
			}
		}
		
//		for(int i = 1; i <= 50; i++){
//			Set<WeiboUser> users = helper.doSearchUserCN(keyword, i);
//			LOG.info("Page {}, Search {} users: {}", new Object[]{i, users.size(), users});
//			LOG.info("FillUserInfo.Queue.size: {}", WeiboCNBiz.instance().taskSize());
////			LOG.info("SuperDao.Queue.size: {}", WeiboCNScheduler.instance().dbTaskSize());
////			LOG.info(WeiboCNScheduler.instance().stats());
//			if(users.size() == 0) break;
//			
//			WeiboCNBiz.instance().addAllUser(users);
//			CommonUtil.wait2(30000, 50000);
//		}

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
	
	public boolean chatUser(String uid, String content){
		
		return helper.doChatUserWithAttach(uid, content);
		
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
	
	public static void main(String...strings){
		for(int m = 0; m < 5; m++){
			for(int n = 0; n < 5; n++){
				System.out.println(m + " " + n);
				if(n == 3) break;
			}
		}
	}
}
