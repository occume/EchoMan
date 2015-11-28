package com.echoman.robot.qq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jodd.util.HtmlDecoder;

import com.echoman.robot.AbstractHelper;
import com.echoman.robot.qq.model.QqGroupMsg;
import com.google.common.collect.Lists;

public class QQRobotHelper extends AbstractHelper{
	
	private final static Logger LOG = LoggerFactory.getLogger(QQRobotHelper.class);
	
	private QQRobot robot;
	
	public QQRobotHelper(){}
	
	public QQRobotHelper(QQRobot robot){
		this.robot = robot;
	}
	
	public void login(){
		
		xlogin();
		
		try {
			check();
			doLogin();
		} catch (Exception e) {
			LOG.error("Login fail, {}", e);
		}
	}
	
	public void sign(){
		String url = "http://snsapp.qzone.qq.com/cgi-bin/signin/checkin_cgi_read?"
				+ "version=1&"
				+ "more_info_length=10&"
				+ "is_need_rank=1&"
				+ "plattype=1&"
				+ "r=0.14843519152725493&"
				+ "g_tk=2116751054&"
				+ "group_id=1";
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "snsapp.qzone.qq.com");
		hds.put("Referer", "http://ctc.qzs.qq.com/qzone/app/checkin_v4/html/checkin.html");
		
		String content = http.get(url, hds);
//		System.out.println(content);
	}
	
	public void qqGroupSign(String groupId) throws Exception{
		String url = "http://qiandao.qun.qq.com/cgi-bin/sign";
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "qiandao.qun.qq.com");
		hds.put("Origin", "http://qiandao.qun.qq.com");
		hds.put("Referer", "http://qiandao.qun.qq.com/index.html?groupUin="+ groupId +"&appID=100729587");
		
		getBkn(http.getCookie("skey"));
		
		Map<String, Object> params = new HashMap<>();
		params.put("gc", groupId);
		params.put("is_sign", "0");
		params.put("bkn", bkn);
		
		String html = http.post(url, params, hds);
//		System.out.println(html);
	}
	
	public Map<Long, String> qqGroupList() throws Exception{
		
		getBkn(http.getCookie("skey"));
		
		String url = "http://qun.qzone.qq.com/cgi-bin/get_group_list?"
				+ "groupcount=4&count=4&callbackFun=_GetGroupPortal&"
				+ "uin="+ robot.getAccount() +"&"
				+ "g_tk="+ bkn +"&"
				+ "ua=Mozilla%2F5.0%20(Windows%20NT%206.1%3B%20WOW64)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Chrome%2F45.0.2454.85%20Safari%2F537.36";
		
		String html = http.get(url);
		System.out.println(html);
		
		return pareseGroupList(html);
	}
	
	public void batchGroupSign(){
		try {
			doBatchGroupSign();
		} catch (Exception e) {
			LOG.error("Group sign error, {}", e);
		}
	}
	
	private void xlogin(){
		String url = "http://xui.ptlogin2.qq.com/cgi-bin/xlogin?"
				+ "proxy_url=http%3A//qzs.qq.com/qzone/v6/portal/proxy.html&"
				+ "daid=5&"
				+ "pt_qzone_sig=1&"
				+ "hide_title_bar=1&"
				+ "low_login=0&"
				+ "qlogin_auto_login=1&"
				+ "no_verifyimg=1&"
				+ "link_target=blank&"
				+ "appid=549000912&"
				+ "style=22&"
				+ "target=self&"
				+ "s_url=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&"
				+ "pt_qr_app=%E6%89%8B%E6%9C%BAQQ%E7%A9%BA%E9%97%B4&"
				+ "pt_qr_link=http%3A//z.qzone.com/download.html&self_regurl=http%3A//qzs.qq.com/qzone/v6/reg/index.html&"
				+ "pt_qr_help_link=http%3A//z.qzone.com/download.html";
		
		http.get(url);
	}
	
	private void check() throws FileNotFoundException, ScriptException, URISyntaxException{

		String loginSig = http.getCookie("pt_login_sig");
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "check.ptlogin2.qq.com");
		
		String url = "http://check.ptlogin2.qq.com/check?"
				+ "regmaster=&"
				+ "pt_tea=1&"
				+ "pt_vcode=1&"
				+ "uin="+ robot.getAccount() +"&"
				+ "appid=549000912&"
				+ "js_ver=10133&"
				+ "js_type=1&"
				+ "login_sig="+ loginSig +"&"
				+ "u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&"
				+ "r=0.970304226894594";
		
		String content = http.get(url, hds);
		
		Pattern pat = Pattern.compile("([^()]+)");
		Matcher mat = pat.matcher(content);
		String funName = "";
		String argus = "";

		if(mat.find()){
			funName = mat.group();
		}
		
		if(mat.find()){
			argus = mat.group();
		}
		
		runCallback("ptui_checkVC("+ argus +");");
		verifyCode = bds.get("verifyCode").toString();
		salt = bds.get("salt").toString();
		LOG.info("Get verifyCode: {}", verifyCode);
	}
	
	private void doLogin() throws IOException, ScriptException, URISyntaxException{
		
		String loginSig = http.getCookie("pt_login_sig");
		String verifySession = http.getCookie("ptvfsession");
		
		encriptPassword(robot.getPassword());
		
		long now = System.currentTimeMillis();
		
		String url = "http://ptlogin2.qq.com/login?"
				+ "u="+ robot.getAccount() +"&"
				+ "verifycode="+ verifyCode +"&"
				+ "pt_vcode_v1=0&"
				+ "pt_verifysession_v1="+ verifySession +"&"
				+ "p="+ encrptedPassword +"&"
				+ "pt_randsalt=0&"
				+ "u1=http://qzs.qq.com/qzone/v5/loginsucc.html?para=izone&"
				+ "ptredirect=0&"
				+ "h=1&t=1&g=1&from_ui=1&ptlang=2052&"
				+ "action=3-8-"+ now +"&"
				+ "js_ver=10133&js_type=1&"
				+ "login_sig="+ loginSig +"&"
				+ "pt_uistyle=32&"
				+ "aid=549000912&"
				+ "daid=5&pt_qzone_sig=1&";

		Map<String, String> hds = getGeneralHeaders();
		
		String ret = http.get(url, hds);
		
		runCallback(HtmlDecoder.decode(ret));
		loginResultCode = bds.get("loginResultCode").toString();
		loginResultMsg = bds.get("loginResultMsg").toString();
		userName = bds.get("userName").toString();

		if("0".equals(loginResultCode)){ 
			http.setLogined(true);
			LOG.info("Login success, {}", userName);
		}
		else{
			LOG.info("Login fail, {}", loginResultMsg);
		}
	}
	
	private Map<Long, String> pareseGroupList(String script) throws Exception{
		
		URL underscore = QQRobotHelper.class.getClassLoader().getResource("com/echoman/robot/qq/underscore.js");
		URL getGroupList = QQRobotHelper.class.getClassLoader().getResource("com/echoman/robot/qq/getGroupList.js");
		
		FileReader reader1 = new FileReader(new File(underscore.getPath()));
		FileReader reader2 = new FileReader(new File(getGroupList.getPath()));
		
		Map<Long, String> groupMap = new HashMap<>();
		bds.put("groupMap", groupMap);
		
		engine.eval(reader1);
		engine.eval(reader2);
		engine.eval(script);
		
		LOG.info("Get groupMap, num: " + groupMap.size());
		return groupMap;
	}
	
	private void doBatchGroupSign() throws Exception{
		
		if(!isLogin()){
			login();
		}
		
		Map<Long, String> groupList = qqGroupList();

		for(long groupId: groupList.keySet()){
			qqGroupSign(String.valueOf(groupId));
			Thread.sleep(2000);
			LOG.info("Sign ok...{}", groupList.get(groupId));
		}
	}
	
	private String verifyCode;
	private String salt;
	private String encrptedPassword;
	private String bkn;
	private int seq;
	private int es;
	private int pullLen;
	private String loginResultCode;
	private String loginResultMsg;
	private String userName;
	
	private void preShowMessage(){
		String url0 = "http://msgwall.qun.qq.com/?groupUin=89304269&appID=100730554";		
		Map<String, String> hds0 = getGeneralHeaders();
		hds0.put("Host", "msgwall.qun.qq.com");		
		String html = http.get(url0, hds0);
		
		Document doc = Jsoup.parse(html);
		Elements scripts = doc.select("script");
		
		/**
		 *  Get script url
		 */
		String indexJsUrl = "";
		for(Element script: scripts){
			String attr = script.attr("src");
			if(attr.contains("s.url.cn/qqun/qun/msgwall/js")){
				indexJsUrl = attr;
			}
		}
		
		Pattern pattern = Pattern.compile("pullLen:(\\d+)");
		html = http.get(indexJsUrl);
		Matcher matcher = pattern.matcher(html);
		
		if(matcher.find()){
			String pullen = matcher.group(1);
			pullLen = Integer.valueOf(pullen);
		}
	}
	public List<QqGroupMsg> showRoamMessage(String groupId) throws Exception{
		
		String url = "http://msgwall.qun.qq.com/cgi-bin/get_qun_roam_msg";
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Accept-Language:", "en-us,en");
		hds.put("Host", "msgwall.qun.qq.com");
		hds.put("Origin", "http://msgwall.qun.qq.com");
		hds.put("Referer", "http://msgwall.qun.qq.com/?groupUin="+ groupId +"&appID=100730554");

		getBkn(http.getCookie("skey"));
		
		Map<String, Object> params = new HashMap<>();
		if(seq == 0){
			preShowMessage();
			params.put("ps", "60");
			params.put("bs", "0");
			params.put("es", "0");
			params.put("mode", "1");
		}
		else{
			params.put("ps", pullLen + "");
			params.put("bs", (es + 1) + "");
			params.put("es", (es + pullLen) + "");
		}
		params.put("gid", groupId);
		params.put("seq", seq++ + "");
		params.put("bkn", bkn);
		params.put("mode", "1");
		
		String result = http.post(url, params, hds);
		System.out.println(result);
		return parseMessage(result, groupId);
	}
	
	private List<QqGroupMsg> parseMessage(String html, String groupId) throws Exception{
		
		JSONObject json = new JSONObject(html);
		
		JSONObject map = json.getJSONObject("list");
		
		JSONObject result = json.getJSONObject("result");
		int bs = result.getInt("bs");
		if(es == 0)
			es = result.getInt("es");
		
		JSONArray cl = result.getJSONArray("cl");
		List<QqGroupMsg> list = Lists.newArrayList();
		
		for(int i = 0; i < cl.length();i++){
			JSONObject item = cl.getJSONObject(i);
			Object obj = item.get("il");
			if(obj instanceof String) continue;
			JSONArray il = (JSONArray) obj;
			
			String content = "";
			for(int k = 0; k < il.length(); k++){
				
				if(il.get(k) instanceof String) continue;
				JSONObject il0 = il.getJSONObject(k);
				if(il0.has("v")){
					content = il0.getString("v");
					
				}
				else if(il0.has("i")){
					content = il0.getString("i");
				}
			}
			
			long t = item.getLong("t");
			long u = item.getLong("u");
			long tm = Long.valueOf(t + "000");
			String nickName = map.getString(String.valueOf(u));
			QqGroupMsg msg = new QqGroupMsg(groupId, u, nickName, content, new Date(tm));
			list.add(msg);
			System.out.println(msg);
		}
		
		LOG.info("Get bs: {}", bs);
		return list;
	}
	
	private void runCallback(String funString) throws FileNotFoundException, ScriptException, URISyntaxException{
		
		URL url = QQRobotHelper.class.getClassLoader().getResource("com/echoman/robot/qq/callback.js");
		
		FileReader reader = new FileReader(new File(url.getPath()));
		engine.eval(reader);
		engine.eval(funString);
	}
	
	private void encriptPassword(String password) throws FileNotFoundException, ScriptException{
		URL url = QQRobotHelper.class.getClassLoader().getResource("com/echoman/robot/qq/ecryption.js");
		
		FileReader reader = new FileReader(new File(url.getPath()));
		engine.eval(reader);
		engine.eval("var encrptedPassword = "
				+ "$.Encryption.getEncryption('"+ password +"', '"+ salt +"', '"+ verifyCode +"', false);");
		
		encrptedPassword = bds.get("encrptedPassword").toString();
		
		LOG.info("Encrpt password: {}", bds.get("encrptedPassword"));
	}
	
	private void getBkn(String skey) throws FileNotFoundException, ScriptException, URISyntaxException{
		
		URL url = QQRobotHelper.class.getClassLoader().getResource("com/echoman/robot/qq/getBkn.js");
		
		bds.put("skey", skey);
		FileReader reader = new FileReader(new File(url.getPath()));
		engine.eval(reader);
		engine.eval("getBkn();");
		
		bkn = bds.get("bkn").toString();
		
		LOG.debug("Geg bkn: {}", bds.get("bkn"));
	}
	
	public Map<String, String> getGeneralHeaders(){
		
		Map<String, String> hds = new HashMap<>();

		hds.put("Accept", "*/*");
		hds.put("Accept-Encoding", "gzip, deflate");
		hds.put("Accept-Language", "zh-CN,zh:q=0.8,en-US:q=0.5,en:q=0.3");
		hds.put("Connection", "keep-alive");
		hds.put("Cache-Control", "max-age=0");
		hds.put("Host", "ptlogin2.qq.com");
		hds.put("Referer", getRefer());
		hds.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/40.0");
		
		return hds;
	}
	
	private static String getRefer(){
		return
		"http://xui.ptlogin2.qq.com/cgi-bin/xlogin?proxy_url=http%3A//qzs.qq.com/qzone/v6/portal/proxy.html&daid" +
		"=5&pt_qzone_sig=1&hide_title_bar=1&low_login=0&qlogin_auto_login=1&no_verifyimg=1&link_target=blank&appid" +
		"=549000912&style=22&target=self&s_url=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone" +
		"&pt_qr_app=%E6%89%8B%E6%9C%BAQQ%E7%A9%BA%E9%97%B4&pt_qr_link=http%3A//z.qzone.com/download.html&self_regurl" +
		"=http%3A//qzs.qq.com/qzone/v6/reg/index.html&pt_qr_help_link=http%3A//z.qzone.com/download.html";
	}

	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/qq/";
	}

	@Override
	public boolean isLogin() {
		return false;
	}

}
