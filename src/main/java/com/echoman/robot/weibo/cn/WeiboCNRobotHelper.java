package com.echoman.robot.weibo.cn;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.robot.AbstractHelper;
import com.echoman.robot.weibo.model.WeiboUser;
import com.echoman.util.Constant;
import com.echoman.util.DocUtil;
import com.echoman.util.RegexUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;

public class WeiboCNRobotHelper extends AbstractHelper {
	
	private final static Logger LOG = LoggerFactory.getLogger(WeiboCNRobotHelper.class);
	
	private WeiboCNRobot robot;
	
	private String retcode;
	private String servertime;
	private String pcid;
	private String nonce;
	private String pubkey;
	private String rsakv;
	private String ssoUrl;
	private String uniqueid;
	private String relationMyfollowHtml;
	private String searchHtml;
	
	public WeiboCNRobotHelper(WeiboCNRobot robot){
		this.robot = robot;
	}
	
	
	public Set<WeiboUser> getFollows(String id, int page){

		String url = "http://weibo.com/p/100505"+ id +"/follow?page="+ page +"#Pl_Official_HisRelation__62";
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.com");
		headers.put("Referer", "http://weibo.com/u/"+ uniqueid +"/home");
		
		String html = http.get(url, headers);
		System.out.println(url);
		System.out.println(html);
		/**
		 * you yi xie zhang hao bu neng cha kan follows
		 */
		if(Strings.isNullOrEmpty(html)){
			return Collections.emptySet();
		}
		
		String text = DocUtil.getScriptText1(html, "domid\":\"Pl_Official_HisRelation_");

		if(LOG.isDebugEnabled())
			LOG.debug("Text: {}", text);
		
		runFunction(text);
		
		relationMyfollowHtml = getValueOfVar("relationMyfollowHtml");
		
		Set<WeiboUser> follows = WeiboCNDocParser.parseFollowsById(relationMyfollowHtml);
		relationMyfollowHtml = "";
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
		System.out.println(html);
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
		
		params.put("mobile", "13585908235");
		params.put(("password_" + passSuffix), "5651403");
		System.out.println(params);
		
		String html = http.post(url, params, headers);
		
		String location = http.getHeaderVal("Location");
		System.out.println(location);
		newLogin(location);
		
	}
	
	private void newLogin(String url){
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "newlogin.sina.cn");
		headers.put("Referer", "http://login.weibo.cn/login/?rand=1242709468&backURL=http%3A%2F%2Fweibo.cn%2F&backTitle=%E5%BE%AE%E5%8D%9A&vt=4&revalid=2&ns=1");
		
		http.get(url, headers);
		String location = http.getHeaderVal("Location");
		System.out.println(location);
		
		crossLogin(location);
	}
	
	private void crossLogin(String url){
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "passport.weibo.com");
		headers.put("Referer", "http://login.weibo.cn/login/?rand=1242709468&backURL=http%3A%2F%2Fweibo.cn%2F&backTitle=%E5%BE%AE%E5%8D%9A&vt=4&revalid=2&ns=1");
		
		http.get(url, headers);
		String location = http.getHeaderVal("Location");
		System.out.println(location);
		
//		doChatUser();
	}
	
	public Set<WeiboUser> doSearchUserCN(String keyword, int page){

		String url = "http://weibo.cn/find/user?keyword="+ keyword +"&suser=2&page=" + page;
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.cn");
		headers.put("Referer", "http://weibo.cn/find/user");
		
		String html = http.get(url, headers);
		return WeiboCNDocParser.parseUserOfSearchCN(html);
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

	
	private void prepaerChat(){
		String url = "http://weibo.cn/im/chat?uid=1864100610&rl=0&rand=718260";
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.cn");
		headers.put("Referer", "http://weibo.cn/im/chat?uid=1864100610&rl=0");
		http.get(url, headers);
	}

	public void doChatUser() {
		prepaerChat();
		String url = "http://weibo.cn/msg/do/post?vt=4&st=9c38a1";
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.cn");
		headers.put("Referer", "http://weibo.cn/im/chat?uid=1864100610&rl=0");
	
		Map<String, Object>
		params = Maps.newHashMap();
		params.put("content", "恭喜发财");
		params.put("rl", "1");
		params.put("send", "发送");
		params.put("uid", "1864100610");
		System.out.println("---> before chat");
		String html = http.post(url, params, headers);
		System.out.println("---> " + html);
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
		WeiboCNRobotHelper helper = new WeiboCNRobotHelper(null);
		byte[] b1 = Files.readAllBytes(Paths.get("D:/tmp/weibo/page2.txt"));
		String html = new String(b1, "UTF-8");
		String text = DocUtil.getScriptText1(html, "domid\":\"Pl_Official_HisRelation_");
		
		helper.loadAndRunFunction("function", text);
		String html1 = helper.getValueOfVar("relationMyfollowHtml");
	
		Set<WeiboUser> follows = WeiboCNDocParser.parseFollowsById(html1);
		System.out.println(follows);
	}
}