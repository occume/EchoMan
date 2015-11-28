package com.echoman.robot.hujiang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.model.RobotBean;
import com.echoman.robot.AbstractRobot;
import com.echoman.robot.Robot;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;

public class HujiangRobot extends  AbstractRobot{
	
	private final static Logger LOG = LoggerFactory.getLogger(HujiangRobot.class);
	
	public HujiangRobot(RobotBean bean) {
		super(bean);
	}

	public void doLogin() throws IOException, ScriptException, URISyntaxException{
		
		String url = "http://pass.hujiang.com/quick/account/?"
				+ "callback=jQuery18302554508973073093_1438860565247&"
				+ "account="+ account +"&"
				+ "password="+ Hashing.md5().hashBytes(password.getBytes()).toString() +"&"
				+ "code=facial&"
				+ "act=loginverify&"
				+ "source=nbulo&"
				+ "captchatoken=a2c65b01c1162a8ab1627892a99fd9fc&"
				+ "_=1438860609238";

		Map<String, String> hds = getGeneralHeaders();

		String ctt = http.get(url, hds);
		System.out.println(ctt);
		Pattern pat = Pattern.compile("([^()]+)");
		Matcher mat = pat.matcher(ctt);
		String funName = "";
		String argus = "";

		if(mat.find()){
			funName = mat.group();
		}
		
		if(mat.find()){
			argus = mat.group();
		}
		executeScript(funName, argus);
	}
	
	private static String ssoToken;
	private static String clubAuth;
	
	public  void getClubAuth() throws IOException{
		String url = "http://pass.hujiang.com/quick/synclogin.aspx?"
				+ "token="+ ssoToken +"&"
				+ "remeberdays=14&"
				+ "callback=jQuery1830443667704975646_1440390523935&_=1440390592722";
		
		Map<String, String> hds = getGeneralHeaders();
		
		String content = http.get(url, hds);
		System.out.println(content);
		
		clubAuth = http.getCookie("ClubAuth");
		if(!Strings.isNullOrEmpty(clubAuth)){
			System.out.println("get clubAuth: " + clubAuth);
			takeCard();
		}
	}
	
	public static void executeScript(String funName, String argus) throws FileNotFoundException, ScriptException, URISyntaxException{
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		
		Bindings bds = engine.createBindings();
		bds.put("code", "");
		bds.put("message", "");
		engine.setBindings(bds, 100);
		
		URL url = HujiangRobot.class.getClassLoader().getResource("com/echoman/robot/hujiang/callback.js");
		
		FileReader reader = new FileReader(new File(url.getPath()));
		engine.eval(reader);
		engine.eval("parseLoginResult("+ argus +");");
		
		ssoToken = bds.get("ssotoken").toString();
		System.out.println("get ssotoken: " + ssoToken);
	}
	
	public void takeCard() throws IOException{
		String url = "http://bulo.hujiang.com/app/api/ajax_take_card.ashx?0.14495444658471535";
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "bulo.hujiang.com");
		
		String ctt = http.get(url, hds);
		System.out.println(ctt);
	}
	
	public Map<String, String> getGeneralHeaders(){
		
		Map<String, String> hds = new HashMap<>();

		hds.put("Accept", "application/json,text/javascript,*/*; q=0.01");
		hds.put("Accept-Encoding", "gzip, deflate");
		hds.put("Accept-Language", "zh-CN,zh:q=0.8,en-US:q=0.5,en:q=0.3");
		hds.put("Connection", "keep-alive");
		hds.put("Cache-Control", "max-age=0");
		hds.put("Host", "pass.hujiang.com");
		hds.put("Referer", "http://bulo.hujiang.com/");
		hds.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0");
		hds.put("X-Requested-With", "XMLHttpRequest");
		
		return hds;
	}

	@Override
	public void backgroundSign() {
		sign();
	}

	@Override
	public void backgroundProcess() {
		
	}
	@Override
	public void sign(){
		LOG.info("Hujiang sign");
		try {
			login();
			getClubAuth();
		} catch (Exception e) {
			LOG.error("Sign fail, {}", e);
		}
	}

	@Override
	public Robot login() {
		try {
			doLogin();
		} catch (Exception e) {
			LOG.error("Login fail, {}", e);
		}
		return this;
	}

	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/hujiang/";
	}

	@Override
	public boolean isLogin() {
		return false;
	}
}
