package com.echoman.robot.weibo.cn;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.robot.AbstractHelper;
import com.echoman.robot.weibo.model.WeiboUser;
import com.echoman.util.DocUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class WeiboCNRobotHelper extends AbstractHelper {
	
	private final static Logger LOG = LoggerFactory.getLogger(WeiboCNRobotHelper.class);
	
	private static int[] sortTypes = {0, 108};
	private static String[] filterTypes = {"stag", "isv", "all"};
	
	private WeiboCNRobot robot;
	
	private String searchHtml;
	
	public WeiboCNRobotHelper(WeiboCNRobot robot){
		this.robot = robot;
	}
	
	public Set<WeiboUser> getFollows(WeiboUser user, int page){

		String url = "http://weibo.cn/"+ user.getUserId() + "/fans?page=" + page;
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.cn");
		headers.put("Referer", "http://weibo.cn/u/"+ user.getUserId() );
		
		String html = http.get(url, headers);
		
		if(Strings.isNullOrEmpty(html)){
			return Collections.emptySet();
		}
//		System.out.println(html);
		Set<WeiboUser> follows = WeiboCNDocParser.parseFollowsById(html, user);
		return follows;
	}
	
	protected Set<WeiboUser> searchUser(String keyword, int page){
		
		String url = "http://s.weibo.com/user/"+ keyword +"&page=" + page;
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "s.weibo.com");
		headers.put("Referer", "http://s.weibo.com/user/" + keyword);
		
		String html = http.get(url, headers);
		
		String text = DocUtil.getScriptText1(html, "pid\":\"pl_user_feedList");
		System.out.println(text);
		
		runFunction(text);
		
		searchHtml = getValueOfVar("searchHtml");
		return WeiboCNDocParser.parseUserOfSearch(searchHtml);
	}
	
	/**
	 *  weibo.cn begin
	 */
	
	private void prepareLoginCN(){
		
		String url = "http://weibo.cn";
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.cn");
		
		String html = http.get(url, headers, true);
//		System.out.println(html);
	}
	
	public Map<String, Object> getLoginCNParamsMap(){
		String url = "http://login.weibo.cn/login/?ns=1&revalid=2&backURL=http%3A%2F%2Fweibo.cn%2F&backTitle=%CE%A2%B2%A9&vt=4";
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "login.weibo.cn");
		headers.put("Referer", "http://weibo.cn/pub/?vt=4");
		
		String html = http.get(url, headers);
		
		return WeiboCNDocParser.parseVK(html);
	}
	
	public void doLoginCN(){
		
		prepareLoginCN();
		
		String url = "http://login.weibo.cn/login/?rand=57876072&backURL=http%3A%2F%2Fweibo.cn%2F&backTitle=%E5%BE%AE%E5%8D%9A&vt=4&revalid=2&ns=1)";
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "login.weibo.cn");
		headers.put("Referer", "http://login.weibo.cn/login/?ns=1&revalid=2&backURL=http%3A%2F%2Fweibo.cn%2F&backTitle=%CE%A2%B2%A9&vt=");
		
		Map<String, Object> 
		params = getLoginCNParamsMap();
		params.put("remember", "on");
		params.put("submit", "登录");
		
		String vk = params.get("vk").toString();
		String passSuffix = vk.substring(0, 4);
		
		params.put("mobile", robot.getAccount().replace("%40", "@"));
		params.put(("password_" + passSuffix), robot.getPassword());
		System.out.println(params);
		
		String html = http.post(url, params, headers);
		
		String location = http.getHeaderVal("Location");
		LOG.info("Location1: {}", location);
		newLogin(location);
		
	}
	
	private void newLogin(String url){
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "newlogin.sina.cn");
		headers.put("Referer", "http://login.weibo.cn/login/?rand=1242709468&backURL=http%3A%2F%2Fweibo.cn%2F&backTitle=%E5%BE%AE%E5%8D%9A&vt=4&revalid=2&ns=1");
		
		http.get(url, headers);
		String location = http.getHeaderVal("Location");
		LOG.info("Location2: {}", location);
		
		crossLogin(location);
	}
	
	private void crossLogin(String url){
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "passport.weibo.com");
		headers.put("Referer", "http://login.weibo.cn/login/?rand=1242709468&backURL=http%3A%2F%2Fweibo.cn%2F&backTitle=%E5%BE%AE%E5%8D%9A&vt=4&revalid=2&ns=1");
		
		http.get(url, headers);
		String location = http.getHeaderVal("Location");
		LOG.info("Location3: {}", location);
		
		if(location.contains("gsid")){
			LOG.info("Login success");
		}
		else{
			LOG.info(location);
		}
	}
	
	public Set<WeiboUser> doSearchUserCN(String keyword, int page){
		return doSearchUserCN(keyword, 0, "", page);
	}
	
	public Set<WeiboUser> doSearchUserCN(String keyword, int sort, String filter, int page){
		
		Set<WeiboUser> users = Sets.newHashSet();
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.cn");
		headers.put("Referer", "http://weibo.cn/find/user");
		
		
		String url = "http://weibo.cn/search/user/?keyword="+ keyword +"&sort="+ sort +"&filter="+ filter +"&page=" + page;
		System.out.println(url);
		String html = http.get(url, headers);
		
		users.addAll(WeiboCNDocParser.parseUserOfSearchCN(html, keyword));
		
		return users;

//		String url = "http://weibo.cn/find/user?keyword="+ keyword +"&suser=2&page=" + page;
		
//		Map<String, String> 
//		headers = getGeneralHeaders();
//		headers.put("Host", "weibo.cn");
//		headers.put("Referer", "http://weibo.cn/find/user");
//		try {
//			url = URLEncoder.encode(url, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		String html = http.get(url, headers);
//		return WeiboCNDocParser.parseUserOfSearchCN(html, keyword);
	}
	

	public void doFillUserInfo(WeiboUser user) {
		
		String url = "http://weibo.cn/"+ user.getUserId() +"/info";
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.cn");
		headers.put("Referer", "http://weibo.cn/u/" + user.getUserId());
		
		String html = http.get(url, headers);
		WeiboCNDocParser.parseUserInfo(html, user);
	}
	
	public void doFillUserInfo1(WeiboUser user) {
		
		String url = "http://weibo.cn" + user.getUrl();
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.cn");
		headers.put("Referer", "http://weibo.cn/find/user");
		
		String html = http.get(url, headers);
		WeiboCNDocParser.parseUserInfo1(html, user);
	}

	
	private String prepaerChat(String uid){
//		String url = "http://weibo.cn/im/chat?uid=1864100610&rl=0&rand=718260";
		String url = "http://weibo.cn/im/chat?uid=" + uid + "&rl=0";
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.cn");
		headers.put("Referer", "http://weibo.cn/im/chat?uid=1864100610&rl=0");
		String html = http.get(url, headers);
		
		return WeiboCNDocParser.getMsgFormAction(html);
	}

	public void doChatUser(String uid, String content) {
		
		String action = prepaerChat(uid);
		
//		String url = "http://weibo.cn/msg/do/post?vt=4&st=9c38a1";
		String url = "http://weibo.cn" + action;
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.cn");
		headers.put("Referer", "http://weibo.cn/im/chat?uid="+ uid +"&rl=0");
	
		Map<String, Object>
		params = Maps.newHashMap();
		params.put("content", content);
		params.put("rl", "1");
		params.put("send", "发送");
		params.put("uid", uid);
		
		System.out.println(" --> " + uid + " " + content);
		String html = http.post(url, params, headers);
		String location = http.getLocation();
		System.out.println("---> " + location);
	}
	
	public boolean doChatUserWithAttach(String uid, String content) {
		
		String action = prepaerChat(uid);
		
		String url = "http://weibo.cn" + action;
		LOG.info("Post msg url: {}", url);
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.cn");
		headers.put("Referer", "http://weibo.cn/im/chat?uid="+ uid +"&rl=0");
	
		Map<String, Object>
		params = Maps.newHashMap();
		params.put("content", content);
		params.put("rl", "1");
		params.put("act", "send");
		params.put("uid", uid);
		
		LOG.info(" --> {}", (uid + " " + content));
		String html = http.post(url, params, headers, "conf/aks1.jpg");
		String location = http.getLocation();
		
		if(!Strings.isNullOrEmpty(location)){
			return true;
		}
		else{
			System.out.println("html---> " + html);
			return false;
		}
	}

	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/weibo/";
	}

	public static void main(String...strings) throws IOException{
//		String text = "try{sinaSSOController.setCrossDomainUrlList({\"retcode\":32650,\"arrURL\":"
//				+ "[\"http://crosdom.weicaifu.com/sso/crosdom?action=login&savestate=1477813101"
//				+ "http://passport.97973.com/sso/crossdomain?action=login&savestate=1477813101"
//				+ "http://passport.weibo.cn/sso/crossdomain?action=login&savestate=1"
//				+ "]});}catch(e){}try{sinaSSOController.crossDomainAction('login',function(){location."
//				+ "replace('http://passport.weibo.com/wbsso/login?ssosavestate="
//				+ "1477813101&url=http%3A%2F%2Fweibo.com%2Fajaxlogin.php%3Fframelogin%3D1%26callback%3D"
//				+ "parent.sinaSSOController.feedBackUrlCallBack&"
//				+ "ticket=ST-MTczMDgxMTM3MA==-1446277101-gz-76808654F087F726EE4374D13FFC4F11&retcode=0');});}catch(e){}";
//		String retcode = RegexUtil.getGroup1(text, "retcode\":(\\d+)");
//		System.out.println(retcode);
//		Files.readAllLines(Paths.get("D:/tmp/weibo/page1.txt"), Charset.forName("UTF-8"));
//		WeiboCNRobotHelper helper = new WeiboCNRobotHelper(null);
//		byte[] b1 = Files.readAllBytes(Paths.get("D:/tmp/weibo/page2.txt"));
//		String html = new String(b1, "UTF-8");
//		String text = DocUtil.getScriptText1(html, "domid\":\"Pl_Official_HisRelation_");
//		
//		helper.loadAndRunFunction("function", text);
//		String html1 = helper.getValueOfVar("relationMyfollowHtml");
		File f = new File("");
		File f1 = new File("conf/aks1.jpg");
		System.out.println(f.getAbsolutePath());
		System.out.println(f1.getAbsolutePath());
		System.out.println(f1.exists());
//		Set<WeiboUser> follows = WeiboCNDocParser.parseFollowsById(html1);
//		System.out.println(follows);
	}
}