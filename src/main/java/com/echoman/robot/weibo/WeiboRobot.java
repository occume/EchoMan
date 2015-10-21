package com.echoman.robot.weibo;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.model.RobotBean;
import com.echoman.robot.AbstractRobot;
import com.echoman.robot.Robot;
import com.echoman.robot.baidu.BaiduRobot;
import com.echoman.util.Constant;
import com.google.common.collect.Maps;
import com.google.common.io.BaseEncoding;

public class WeiboRobot extends AbstractRobot {
	
	private final static Logger LOG = LoggerFactory.getLogger(WeiboRobot.class);
	
	private String retcode;
	private String servertime;
	private String pcid;
	private String nonce;
	private String pubkey;
	private String rsakv;
	private String ssoUrl;
	
	public WeiboRobot(RobotBean bean){
		super(bean);
	}
	
	private void prelogin(){
		String url = "http://login.sina.com.cn/sso/prelogin.php?entry=weibo&callback=sinaSSOController.preloginCallBack&su=MTM1ODU5MDgyMzU%3D&rsakt=mod&checkpin=1&client=ssologin.js(v1.4.18)&_="+ System.currentTimeMillis();
		
		Map<String, String> headers = getGeneralHeaders();
		
		String html = http.get(url, headers);
		
		runCallback(new JsInvoker() {
			@Override
			public void invoke() {
				retcode = bds.get("retcode").toString();
				servertime = bds.get("servertime").toString();
				pcid = bds.get("pcid").toString();
				nonce = bds.get("nonce").toString();
				pubkey = bds.get("pubkey").toString();
				rsakv = bds.get("rsakv").toString();
			}
		}, "callback", html);
		
		LOG.info("Get rsakv: {}", rsakv);
	}

	@Override
	public Robot login() {
		
		prelogin();
		
		String url = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)";
		
		Map<String, String> headers = getGeneralHeaders();
		Map<String, Object> params = Maps.newHashMap();
		
		String su = BaseEncoding.base64().encode("13585908235".getBytes());
		String sp = getEncryptedP();
		
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
		
		Pattern p = Pattern.compile("location.replace\\('([^']+)");
		Matcher m = p.matcher(html);
		
		if(m.find()){
			ssoUrl = m.group(1);
			LOG.info("Get ssoURL: {}", ssoUrl);
		}
		return this;
	}
	
	private void ssoLogin(){
		
		Map<String, String> headers = getGeneralHeaders();
		
		headers.put("Host", "passport.weibo.com");
		headers.put("Referer", "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)");
		
		String html = http.get(ssoUrl, headers);
		System.out.println(html);
	}
	
	private String getEncryptedP(){
		try {
			return runJS();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
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
	
	public String runJS() throws Exception{
		
		bds.put("rsaPubkey", pubkey);
		bds.put("servertime", servertime);
		bds.put("nonce", nonce);
		URL url = getClass().getClassLoader().getResource("com/echoman/robot/weibo/encrypt.js");
		
		FileReader reader = new FileReader(new File(url.getPath()));
		engine.eval(reader);
		return bds.get("b").toString();
	}

	public static void main(String...strings) throws Exception{
//		WeiboRobot robot = new WeiboRobot();
//		robot.login();
//		robot.ssoLogin();
//		robot.home();
//		robot.prelogin();
//		String su = BaseEncoding.base64().encode("13585908235".getBytes());
//		System.out.println(su);
//		robot.runJS();
//		String src = "try{sinaSSOController.crossDomainAction('login',function(){location.replace('http://passport.weibo" +
//				".com/wbsso/login?ssosavestate=1476873443&url=http%3A%2F%2Fweibo.com%2Fajaxlogin.php%3Fframelogin%3D1" +
//				"%26callback%3Dparent.sinaSSOController.feedBackUrlCallBack%26sudaref%3Dweibo.com&ticket=ST-MTczMDgxMTM3MA" +
//				"==-1445337443-gz-6F7CB6ED0365774CBE063F8940C188CB&retcode=0');});}catch(e){}";
//		//Pattern pat = Pattern.compile("([^()]+)");
//		Pattern p = Pattern.compile("location.replace\\('([^']+)");
//		Matcher m = p.matcher(src);
//		
//		if(m.find()){
//			System.out.println(m.group(1));
//		}
	}

	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/weibo/";
	}

	@Override
	public void sign() {
		
	}
}
