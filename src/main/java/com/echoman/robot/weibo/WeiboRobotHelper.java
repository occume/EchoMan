package com.echoman.robot.weibo;

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
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;

public class WeiboRobotHelper extends AbstractHelper {
	
	private final static Logger LOG = LoggerFactory.getLogger(WeiboRobotHelper.class);
	
	private WeiboRobot robot;
	
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
	
	public WeiboRobotHelper(WeiboRobot robot){
		this.robot = robot;
	}
	
	/** login start */
	 
	protected void login(){
		prelogin();
		doLogin();
		ssoLogin();
		ajaxLogin();
	}
	
	protected void prelogin(){
		
		String url = "http://login.sina.com.cn/sso/prelogin.php?entry=weibo&callback=sinaSSOController.preloginCallBack&su=MTM1ODU5MDgyMzU%3D&rsakt=mod&checkpin=1&client=ssologin.js(v1.4.18)&_="+ System.currentTimeMillis();
		
		Map<String, String> headers = getGeneralHeaders();
		String html = http.get(url, headers);
		
		if(LOG.isDebugEnabled()) LOG.debug(html);
		
		loadAndRunFunction(null, "function", html);
		
		retcode 	= getValueOfVar("retcode");
		servertime 	= getValueOfVar("servertime");
		pcid 		= getValueOfVar("pcid");
		nonce 		= getValueOfVar("nonce");
		pubkey 		= getValueOfVar("pubkey");
		rsakv 		= getValueOfVar("rsakv");
		
		LOG.info("Get rsakv: {}", rsakv);
	}
	
	public void doLogin(){
		
		String url = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)";
		
		Map<String, String> headers = getGeneralHeaders();
		Map<String, Object> params = Maps.newHashMap();

		String su = BaseEncoding.base64().encode(robot.getAccount().getBytes());
		String sp = getEncryptedP(robot.getPassword());
		
		params.put("encoding", 		Constant.Charset.UTF8);
		params.put("entry", 		"weibo");
		params.put("from", 			"");
		params.put("gateway", 		"1");
		params.put("nonce", 		nonce);
		params.put("pagerefer", 	"http://login.sina.com.cn/sso/logout.php?entry=miniblog&r=http%3A%2F%2Fweibo.com%2Flogout.php%3Fbackurl%3D%252F");
		params.put("prelt", 		"105");
		params.put("pwencode", 		"rsa2");
		params.put("returntype", 	"META");
		params.put("rsakv", 		rsakv);
		params.put("savestate", 	"7");
		params.put("servertime", 	servertime);
		params.put("service", 		"miniblog");
		params.put("sp", 			sp);
		params.put("sr", 			"1680*1050");
		params.put("su", 			su);
		params.put("url", 			"http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack");
		params.put("useticket", 	"1");
		params.put("vsnf", 			"1");
			
		String html = http.post(url, params, headers);
		LOG.debug(html);
		
		String retcode = RegexUtil.getGroup1(html, "retcode\":(\\d+)");
		if("0".equals(retcode)){
			http.setLogined(true);
			LOG.info("Login success, {}", robot.getAccount());
		}
		else{
			LOG.info("Login fail, {}, {}", robot.getAccount(), retcode);
			return;
		}
		
		ssoUrl = RegexUtil.getGroup1(html, "location.replace\\('([^']+)");
	}

	private void ssoLogin(){
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "passport.weibo.com");
		headers.put("Referer", "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)");
		
		String html = http.get(ssoUrl, headers);
		LOG.debug(html);
	}
	
	private void ajaxLogin(){
		
		String url = "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack&sudaref=weibo.com";
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.com");
		headers.put("Referer", "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)");
		
		String html = http.get(url, headers);
		LOG.debug(html);
		
		String fun = RegexUtil.getGroup1(html, "<script[^>]*>(.*)</script>");
		LOG.info(fun);
		
		runFunction(fun);
		uniqueid = getValueOfVar("uniqueid");
		
		LOG.info("Get uniqueid: {}", uniqueid);
	}
	
	private String getEncryptedP(String password){
		
		setValueOfVar("rawPassword", password);
		setValueOfVar("rsaPubkey", pubkey);
		setValueOfVar("servertime", servertime);
		setValueOfVar("nonce", nonce);
		
		loadAndRunFunction("encrypt");
		
		return getValueOfVar("b");
	}
	
	/** login end */
	
	public Set<WeiboUser> getFollows(String id){

		String url = "http://weibo.com/p/100505"+ id +"/follow?page=1&from=page_100505&wvr=6&mod=headfollow#place";
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "weibo.com");
		headers.put("Referer", "http://weibo.com/u/"+ uniqueid +"/home");
		
		String html = http.get(url, headers);
		String text = DocUtil.getScriptText1(html, "domid\":\"Pl_Official_HisRelation_");

		if(LOG.isDebugEnabled())
			LOG.debug("Text: {}", text);
		
		runFunction(text);
		
		relationMyfollowHtml = getValueOfVar("relationMyfollowHtml");
		
		Set<WeiboUser> follows = WeiboDocParser.parseFollowsById(relationMyfollowHtml);
	
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
		return WeiboDocParser.parseUserOfSearch(searchHtml);
	}

	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/weibo/";
	}

	public static void main(String...strings){
		String text = "try{sinaSSOController.setCrossDomainUrlList({\"retcode\":32650,\"arrURL\":"
				+ "[\"http://crosdom.weicaifu.com/sso/crosdom?action=login&savestate=1477813101"
				+ "http://passport.97973.com/sso/crossdomain?action=login&savestate=1477813101"
				+ "http://passport.weibo.cn/sso/crossdomain?action=login&savestate=1"
				+ "]});}catch(e){}try{sinaSSOController.crossDomainAction('login',function(){location."
				+ "replace('http://passport.weibo.com/wbsso/login?ssosavestate="
				+ "1477813101&url=http%3A%2F%2Fweibo.com%2Fajaxlogin.php%3Fframelogin%3D1%26callback%3D"
				+ "parent.sinaSSOController.feedBackUrlCallBack&"
				+ "ticket=ST-MTczMDgxMTM3MA==-1446277101-gz-76808654F087F726EE4374D13FFC4F11&retcode=0');});}catch(e){}";
		String retcode = RegexUtil.getGroup1(text, "retcode\":(\\d+)");
		System.out.println(retcode);
	}
}
