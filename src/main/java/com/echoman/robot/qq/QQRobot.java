package com.echoman.robot.qq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import com.echoman.model.RobotBean;
import com.echoman.robot.AbstractRobot;
import com.echoman.robot.Robot;
import com.echoman.robot.qq.model.QqGroupMsg;
import com.echoman.storage.AsyncSuperDao;

public class QQRobot extends AbstractRobot{
	
	private final static Logger LOG = LoggerFactory.getLogger(QQRobot.class);
	private static final String TYPE = "QQ";
	
	private QQRobotHelper helper = new QQRobotHelper(this);
	
	public QQRobot(){}
	
	public QQRobot(RobotBean bean){
		super(bean);
	}
	
	public Robot login(){
		
		helper.login();
		
		return this;
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
				+ "uin="+ account +"&"
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
	
	private Map<Long, String> pareseGroupList(String script) throws Exception{
		
		URL underscore = QQRobot.class.getClassLoader().getResource("com/echoman/robot/qq/underscore.js");
		URL getGroupList = QQRobot.class.getClassLoader().getResource("com/echoman/robot/qq/getGroupList.js");
		
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
	
	private String bkn;
	private AsyncSuperDao dao = new AsyncSuperDao("jtyd_", 3);
	
	public void showRoamMessage(String groupId) throws Exception{
		
		List<QqGroupMsg> msgList = helper.showRoamMessage(groupId);
		
		for(QqGroupMsg msg: msgList){
			dao.save(msg);
		}
		
	}
	
	private void getBkn(String skey) throws FileNotFoundException, ScriptException, URISyntaxException{
		
		URL url = QQRobot.class.getClassLoader().getResource("com/echoman/robot/qq/getBkn.js");
		
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
	public String getName() {
		return account + "@" + TYPE;
	}

	@Override
	public void backgroundSign() {
		LOG.info("****** Start qq group sign ******");
		batchGroupSign();
	}

	@Override
	public void backgroundProcess() {
		
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
