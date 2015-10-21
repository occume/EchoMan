package com.echoman.robot.baidu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.d3f.Tuling;
import com.echoman.model.Response;
import com.echoman.model.RobotBean;
import com.echoman.robot.AbstractRobot;
import com.echoman.robot.Robot;
import com.echoman.robot.baidu.model.ForumInfo;
import com.echoman.robot.baidu.model.PostInfo;
import com.echoman.robot.baidu.model.UserInfo;
import com.echoman.util.Scheduler;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class BaiduRobot extends AbstractRobot{
	
	private final static Logger LOG = LoggerFactory.getLogger(BaiduRobot.class);
	
	private final static String TYPE = "BAIDU";
	
	private UserInfo userInfo;
	private String userName;
	private String userId;
	private String portrait;
	private String errNo;
	
	private PostInfo lastPostInfo;
	private ConcurrentHashMap<String, PostInfo> echoMap;
	
	public BaiduRobot(){}
	
	public BaiduRobot(RobotBean bean){
		super(bean);
		echoMap = new ConcurrentHashMap<>();
	}
	
	@Override
	public Robot login() {
		try {
			doLogin();
		} catch (IOException e) {
			LOG.error("Login fail, {}", e);
		}
		
		/**
		 * 
		 * 
		 */ 
//		Scheduler.execute(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					replyReplyme();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}, "1", 600);
		return this;
	}
	
	public void replyReplyme() throws Exception{
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		/**
		 * 获取所有回复我的回复
		 */
		List<String[]> replies = getReplies();
	
		if(replies.size() == 0){
			lastReplyReplymeDate = new Date();
			return;
		}
		
		/**
		 * 第一次回复
		 **/
		if(replyReplymeCont == 0){
			replyReplymeCont++;
			
			String[] item = replies.get(0);
			lastReplyReplymeDate = df.parse("2015-" + item[1]);
			replyReplyme0(item);
			return;
		}
		
		for(int i = replies.size() - 1; i >=0; i--){
			String[] item = replies.get(i);
			LOG.debug("Item1: {}; Item2: {}; Item3: {}",
					new Object[]{item[0], item[1], item[2]}); 
			Date replyDate = df.parse("2015-" + item[1]);
			
			if(replyDate.after(lastReplyReplymeDate)){
				lastReplyReplymeDate = replyDate;
				replyReplyme0(item);
				Thread.sleep(random(2000, 10000));
			}
		}
	}
	
	public void replyThread(String tid, String content){
		try {
			interReply(tid, content);
		} catch (Exception e) {
			LOG.error("Reply thread error, {}", e);
		}
	}
	
	@Override
	public Response execute(Map<String, String[]> params) {
		String[] actions = params.get("action");
		Response resp = null;
		if(actions == null || actions.length == 0) return resp;
		
		String action = getParam(params, "action");
		switch(action){
			case "login": 		resp = login(params); break;
			case "posts": 		resp = posts(params); break;
			case "reply": 		resp = reply(params); break;
			case "": break;
		}
		return resp;
	}
	
	@Override
	public UserInfo getUserInfo() {
		return userInfo;
	}
	
	@Override
	public String getName() {
		return this.account + "@" + TYPE;
	}
	
	private Response reply(Map<String, String[]> params) {
		
		String tid = getParam(params, "tid");
		String content = getParam(params, "content");
		String period = getParam(params, "period");
		Response resp = Response.getOk();
		
		if(Scheduler.isRunning(tid)){
			System.out.println("is running...");
			resp.setErrCode(1);
			resp.setErrInfo("Already running");
			return resp;
		}
		
		getThreadInfo("/p/" + tid);
		userInfo.addPost(lastPostInfo);
		echoMap.put(tid, lastPostInfo);
		resp.setPayload(lastPostInfo);
		
		Scheduler.execute(new ReplyTask(tid, content), tid, Long.valueOf(period));
		return resp;
	}

	private Response posts(Map<String, String[]> params) {
		
		Response resp = Response.getOk();
		List<PostInfo> posts = getPosts();
		resp.setPayload(posts);
		return resp;
	}

	private String getParam(Map<String, String[]> params, String name){
		String[] values = params.get(name);
		if(values == null || values.length == 0) return "";
		return values[0];
	}
	
	private Response login(Map<String, String[]> params){
		
		Response resp;
		if(!isLogin()){
			resp = Response.getOk();
		}
		else{
			resp = Response.getOk();
			resp.setErrCode(1);
			resp.setErrInfo("Already signed");
			return resp;
		}
		
		userName = getParam(params, "name");
		password = getParam(params, "password");
		
		try {
			
			doLogin();
			
			if(!isLogin()){
				resp.setErrCode(Integer.valueOf(errNo));
				resp.setErrInfo("Login failure");
				LOG.info("Login failure, err_no: {}", errNo);
				return resp;
			}
			getProfile();
			resp.setPayload(userInfo);
			
		} catch (Exception e) {
			resp.setErrCode(1);
			LOG.error("Login error: {}", e.getStackTrace());
		}
		
		return resp;
	}

	private void genGid() throws FileNotFoundException, ScriptException{
		
		engine.setBindings(bds, 100);
		
		URL url = BaiduRobot.class.getClassLoader().getResource("com/echoman/robot/baidu/gid.js");
		FileReader reader = new FileReader(new File(url.getPath()));
		engine.eval(reader);
		
		LOG.debug("Generate gid: {}", bds.get("guideRandom"));
	}
	
	private void getCookie(){
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "www.baidu.com");
		hds.remove("Referer");
		
		http.get("https://www.baidu.com/", hds);
	}
	
	public void getApi() throws ScriptException{
		
		String gid = bds.get("guideRandom").toString();
		String url = "https://passport.baidu.com/v2/api/?getapi&tpl=mn&apiver=v3&"
				+ "tt="+ System.currentTimeMillis() +"&class=login&"
				+ "gid="+ gid +"&logintype=dialogLogin&"
				+ "callback=getToken";
		String content = http.get(url, getGeneralHeaders());
		
		engine.eval(content);
		
		LOG.debug("Get token: {}", bds.get("token"));
	}
	
	private void getPublicKey() throws FileNotFoundException, ScriptException{
		
		String token = bds.get("token").toString();
		String gid = bds.get("guideRandom").toString();
		
		String url = "https://passport.baidu.com/v2/getpublickey?"
				+ "token="+ token
				+ "&tpl=mn&apiver=v3"
				+ "&tt="+ System.currentTimeMillis()
				+ "&gid=" + gid
				+ "&callback=bd__cbs__fqrp2h";
		
		String content = http.get(url);
		
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
		
		getRSA(funName, argus);
	}
	
	private void getRSA(String fun, String argu) throws FileNotFoundException, ScriptException{
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		
		bds.put("cert", "");
		bds.put("rawPassword", password);
		engine.setBindings(bds, 100);
		
		URL url = BaiduRobot.class.getClassLoader().getResource("com/echoman/robot/baidu/util.js");
		
		FileReader reader = new FileReader(new File(url.getPath()));
		engine.eval("var cert = " + argu);
		engine.eval(reader);
		
		LOG.debug("Get rsakey: {}", bds.get("rsakey"));
		LOG.debug("Get encryptPassword: {}", bds.get("encryptPassword"));
	}
	/**
	 * 用名字登录 不要用邮箱
	 * @throws IOException
	 */
	private void doLogin() throws IOException{
		
		LOG.info(getName() + " request login ...");
		
		getCookie();
		
		try {
			genGid();
			getApi();
			getPublicKey();
		} catch (ScriptException e) {
			LOG.error("Login error, {}", e);
		}
		
		String url = "https://passport.baidu.com/v2/api/?login";
		
		String token = bds.get("token").toString();
		String gid = bds.get("guideRandom").toString();
		String rsakey = bds.get("rsakey").toString();
		String encryptPassword = bds.get("encryptPassword").toString();
		
		Map<String, String> hds = getGeneralHeaders();
		
		Map<String, Object> params = getParamsMap();
		params.put("apiver", "v3");
		params.put("callback", "parent.bd__pcbs__tpnjeu");
		params.put("charset", "UTF-8");
		params.put("codeString", "");
		params.put("crypttype", "12");
		params.put("detect", "1");
		params.put("gid", gid);
		params.put("idc", "");
		params.put("isPhone", "");
		params.put("logLoginType", "pc_loginDialog");
		params.put("loginMerge", "true");
		params.put("logintype", "dialogLogin");
		params.put("mem_pass", "on");
		params.put("ppui_logintime", "8862");
		params.put("quick_user", "0");
		params.put("rsakey", rsakey);
		params.put("safeFlag", "0");
		params.put("splogin", "rate");
		params.put("staticPage", "https://www.baidu.com/cache/user/html/v3Jump.html");
		params.put("subpro", "");
		params.put("token", token);
		params.put("tpl", "mn");
		params.put("tt", System.currentTimeMillis() + "");
		params.put("u", "https://www.baidu.com/");
		params.put("username", account);
		params.put("password", encryptPassword);
		params.put("verifycode", "");
		
		String ctt = http.post(url, params, hds);
//		System.out.println(ctt);
		Pattern p = Pattern.compile("err_no=(\\d+)");
		Matcher m = p.matcher(ctt);
		
		if(m.find()){
			errNo = m.group(1);
		}
		
		if("0".equals(errNo)){
			http.setLogined(true);
			getProfile();
		}
		LOG.info("Login status: {}", errNo);
	}
	
	public void getProfile(){
		String url = "http://tieba.baidu.com/";
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Referer", "https://www.baidu.com/index.php?tn=monline_3_dg");
		
		String html = http.get(url);
		
		Document document = Jsoup.parse(html);
		
		Elements scripts = document.getElementsByTag("script");
		String pageDataText = "";
		
		for(Element script: scripts){
			if(script.html().contains("var PageData")){
				pageDataText = script.html();
				break;
			}
		}
		
		try {
			
			engine.eval(pageDataText);
			URL resource = BaiduRobot.class.getClassLoader().getResource("com/echoman/robot/baidu/parse_user_info.js");
			FileReader reader = new FileReader(new File(resource.getPath()));
			engine.eval(reader);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		userId = bds.get("userId").toString();
		userName = bds.get("userName").toString();
		portrait = bds.get("portrait").toString();
		
		Element userInfoElem = document.getElementById("user_info");
		Elements headImg = userInfoElem.select(".media_left .head_img");
		String src = headImg.first().attr("src");
		
		Elements userNameElem = userInfoElem.select(".media_right .user_name a");
		String uName = userNameElem.text();
		
		Element likeForum = document.getElementById("likeforumwraper");
		
		Elements forums = likeForum.select(".u-f-item");
		
		userInfo = new UserInfo(uName, src);
		
		for(Element elem: forums){
			String fid = elem.attr("data-fid");
			String name = elem.text();
			String level = elem.select("span").first().classNames().toString();
			userInfo.addForum(new ForumInfo(fid, name, level));
		}
		
	}
	
	private int replyReplymeCont = 0;
	private Date lastReplyReplymeDate;
	
	private static class ReplymeInfo{
		static final ReplymeInfo EMPTY = new ReplymeInfo();
		String tid, floorNum, pid, content;
	}
	
	private ReplymeInfo parseReplymeInfo(String queryString) throws JSONException{
		String pid = "", tid = "";
		Pattern p = Pattern.compile("/p/(\\d*).+pid=(\\d*)");
		Matcher m = p.matcher(queryString);
		
		if(m.find()){
			tid = m.group(1);
			pid = m.group(2);
		}
		
		String html = getThreadInfo(queryString);
		
		if(html == null){
			return ReplymeInfo.EMPTY;
		}
		
		Document doc = Jsoup.parse(html);
		Elements elems = doc.select(".l_post_bright");
		System.out.println(elems.size());
		
		String fieldString = "";
		for(Element elem: elems){
			String field = elem.attr("data-field");
			if(field.contains(pid)){
				System.out.println(field);
				fieldString = field;
			}
		}
		
		JSONObject jobj = new JSONObject(fieldString);
		JSONObject contentElem = jobj.getJSONObject("content");
		int floorNum = contentElem.getInt("post_no");
		
		ReplymeInfo ret = new ReplymeInfo();
		ret.tid = tid;
		ret.pid = pid;
		ret.floorNum = floorNum + "";
		
		return ret;
	}
	
	private void replyReplyme0(String[] item) throws Exception{
		
		String queryString = item[0];
		
		String question = item[2];
		String answer = "heihei";
		
		/**
		 * 图灵API
		 **/ 
		String[] terms = Tuling.getAnswer(question);
		if("100000".equals(terms[0])){
			answer = terms[1];
			LOG.info("question: {}; answer: {}", question, answer);
			
			ReplymeInfo info = parseReplymeInfo(queryString);
			
			if(info == ReplymeInfo.EMPTY) return;
			
			info.content = answer;
			doReplyReplyme(info);
		}
	}
	
	private void doReplyReplyme(ReplymeInfo info) throws IOException{
		
		String url = "http://tieba.baidu.com/f/commit/post/add";
			
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "tieba.baidu.com");
		hds.put("Origin", "http://tieba.baidu.com");
		hds.put("Referer", "http://tieba.baidu.com/p/" + info.tid);
		hds.put("X-Requested-With", "XMLHttpRequest");
		
		Map<String, Object> params = new HashMap<>();
		
		params.put("ie", "utf-8");
		params.put("kw", lastPostInfo.getForumName());
		params.put("fid", lastPostInfo.getFid());
		params.put("tid", info.tid);
		params.put("floor_num", info.floorNum);
		params.put("quote_id", info.pid);
		params.put("rich_text", "1");
		params.put("tbs", lastPostInfo.getTbs());
		params.put("content", info.content);
		params.put("lp_type", "0");
		params.put("lp_sub_type", "0");
		params.put("new_vcode", "1");
		params.put("tag", "11");
		params.put("repostid", info.pid);
		params.put("anonymous", "0");
		
		String html = http.post(url, params, hds);
		System.out.println(html);
	}
	
	public List<String[]> getReplies() throws JSONException{
		String url = "http://tieba.baidu.com/i/sys/jump?u=" + portrait + "&type=replyme";
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "tieba.baidu.com");
		hds.put("Referer", "http://tieba.baidu.com/");
		
		String html = http.get(url, hds);
		
		Document doc = Jsoup.parse(html);
		Elements scripts = doc.getElementsByTag("script");
		String pageDataText = "";
		
		for(Element script: scripts){
			if(script.html().contains("feed_item_list")){
				pageDataText = script.html();
				break;
			}
		}
		LOG.debug(pageDataText);
		
		Pattern p = Pattern.compile("\\[\\{.*\\d{2}\"\\}\\]:?");
		Matcher matcher = p.matcher(pageDataText);
		
		String jsonString = "";
		
		if(matcher.find()){
			jsonString = matcher.group();
			LOG.debug(jsonString);
		}
		
		List<String[]> replies = new ArrayList<>();
		JSONArray arr = new JSONArray(jsonString);
		for(int i = 0; i < arr.length(); i++){
			JSONObject obj = arr.getJSONObject(i);
			String time = obj.getString("time");
			JSONArray feed = obj.getJSONArray("feed_item_list");
			JSONObject info = feed.getJSONObject(0);
			String url0 = info.getString("url");
			String content0 = info.getString("content");
			
			replies.add(new String[]{url0, time, content0});
		}
		LOG.info("Get {} replies", replies.size());
		return replies;
	}
	
	public void getUnread(){
		String url = "http://tieba.baidu.com/im/pcmsg/query/getAllUnread?_=1443521085338";
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "tieba.baidu.com");
		hds.put("Referer", "http://tieba.baidu.com/");
		hds.put("X-Requested-With", "XMLHttpRequest");
		
		String html = http.get(url, hds);
		System.out.println(html);
	}
	
	public List<PostInfo> getPosts(){
		
		String un = "";
		try {
			un = URLEncoder.encode(userName, "gbk");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String url = "http://tieba.baidu.com/home/post?un=" + un + "&fr=home";
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "tieba.baidu.com");
		hds.put("Referer", "http://tieba.baidu.com/home/main?id=442ab7e7ceaacbadb8e8b306&fr=itb");
		
		String html = http.get(url, hds);
		
		Document doc = Jsoup.parse(html);
		Elements postsElems = doc.select("#content .simple_block_container ul li");
		
		List<PostInfo> posts = new ArrayList<>();
		
		for(Element elem: postsElems){
			
			Elements postElem = elem.select(".wrap_container table tbody tr td.wrap a");
			String name = postElem.first().text();
			String href = postElem.first().attr("href");
			String id = getPostId(href);
			
			posts.add(new PostInfo(id, name));
		}
		
		return posts;
	}
	
	private String getPostId(String text){
		
		String pid = "";
		
		Pattern pat = Pattern.compile("/p/(\\d*)");
		Matcher mat = pat.matcher(text);
		
		if(mat.find()){
			pid = mat.group(1);
		}
		
		return pid;
	}
	
	public void onekeySign(String formName) throws IOException{
		String url = "http://tieba.baidu.com/sign/add";
		
		Map<String, String> headers = getGeneralHeaders();
		headers.put("Host", "tieba.baidu.com");
		headers.put("Referer", "http://tieba.baidu.com/");
		headers.put("x-requested-with", "XMLHttpRequest");
		headers.remove("Cookie");
		
		Map<String, Object> params = new HashMap<>();
		
		params.put("ie", "utf-8");
		params.put("tbs", "");
		params.put("kw", formName);
		
		String content = http.post(url, params, headers);
//		System.out.println(content);
		String no = "";
		String noMsg = "";
		
		Pattern p = Pattern.compile("no\":(\\d+)");
		Matcher m = p.matcher(content);
		
		if(m.find()){
			no = m.group(1);
		}
		
//		Pattern.compile("error\":(\\d+)");
//		Matcher m = p.matcher(content);
//		
//		if(m.find()){
//			noMsg = m.group(1);
//		}
		
		if("0".equals(no)){
			LOG.info("{} sign {} success", userName, formName);
		}
		else{
			LOG.info("{} sign {} fail; no: {}; msg: {}", 
					new Object[]{userName, formName, no, no});
		}
	}
	
	private String getThreadHtml(String tid){
		
		String url = "http://tieba.baidu.com" + tid;
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "tieba.baidu.com");
		hds.put("Referer", "http://tieba.baidu.com/p/"+ tid +"?pn=1");
		
		String html = http.get(url, hds);
		return html;
	}
	
	private List<String> getForumInfo(String forumName){
		String url = "http://tieba.baidu.com/f?kw="+ forumName +"&ie=utf-8";
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "tieba.baidu.com");
		hds.put("Referer", "http://tieba.baidu.com");
		
		String html = http.get(url);
		Document document = Jsoup.parse(html);
		
		Element threadList = document.getElementById("thread_list");
		
		Elements list = threadList.select("li");
		Pattern pattern = Pattern.compile("id\":(\\d+)");
		List<String> tlist = Lists.newArrayList();
		
		for(Element li: list){
			String dataField = li.attr("data-field");
			if(Strings.isNullOrEmpty(dataField)) continue;
			Matcher matcher = pattern.matcher(dataField);
			if(matcher.find()){
				tlist.add(matcher.group(1));
			}
		}
		
		return tlist;
	}
	
	public void wanderTieba(String forumName) throws Exception{
		List<String> tlist = getForumInfo(forumName);
		Collections.shuffle(tlist);
		int i = 0;
		
		for(String tid: tlist){
			if(i++ >= 3) break;
			interReply(tid, "nice work");
			Thread.sleep(random(2000, 10000));
		}
	}
	
	public String getThreadInfo(String tid){
		
		String html = getThreadHtml(tid);
		
		if(html.contains("贴吧404")){
			LOG.info("This thread is deleted, {}", tid);
			return null;
		}
		Document doc = Jsoup.parse(html);
		Elements scripts = doc.getElementsByTag("script");
		Iterator<Element> ite = scripts.iterator();
		
		String scriptText = "";
		
		int i = 0;
		while(ite.hasNext()){
			Element elem = ite.next();
			if(i == 3) break;
			if(elem.html().contains("var PageData")){
				scriptText += elem.html() + ";";
				i++;
			}
			if(elem.html().contains("var commonPageData")){
				scriptText += elem.html() + ";";
				i++;
			}
		}

		try {
			
			engine.eval(scriptText);
//			engine.eval(commonPageData.html());
			
			URL resource = BaiduRobot.class.getClassLoader().getResource("com/echoman/robot/baidu/parse_thread_info.js");
			FileReader reader = new FileReader(new File(resource.getPath()));
			engine.eval(reader);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//fid, tbs, forumName, threadTitle;
		String fid = bds.get("fid").toString();
		String tbs = bds.get("tbs").toString();
		String forumName = bds.get("forumName").toString();
		String threadTitle = bds.get("threadTitle").toString();
		String totalPage = bds.get("totalPage").toString();
		String replyNum = bds.get("replyNum").toString();
		
		lastPostInfo = new PostInfo(fid, forumName, tbs, tid, 
				threadTitle, totalPage, replyNum);

		System.out.println(lastPostInfo);
		return html;
	}
	
	public void interReply(String tid, String content) throws IOException, JSONException{
		getThreadInfo("/p/" + tid);
		echoMap.put(tid, lastPostInfo);
		reply(tid, content);
	}
	
	private void reply(String tid, String content) throws IOException, JSONException{
		
		LOG.info("Reply {}", tid);
//		getThread(threadId);
//		getThreadInfo("/p/" + tid);
//		echoMap.put(tid, lastPostInfo);
		
		String replyContent = content;
		String question = null;
		PostInfo postInfo = echoMap.get(tid);
		
		if(postInfo.getCurrFloor() == 0){
			postInfo.setCurrFloor(1);
			question = postInfo.getTitle();
		}
		else{
			String totalPage = postInfo.getTotalPage();
			int pn = Integer.valueOf(totalPage);
			Random random = new Random();
			
			String html = getThreadInfo("/p/" + tid + "?pn=" + random.nextInt(pn));
			Document doc = Jsoup.parse(html);
			Elements elems = doc.select(".l_post_bright");
			
			List<JSONObject> floors = new ArrayList<>();
			
			for(Element elem: elems){
				String field = elem.attr("data-field");
				JSONObject jobj = new JSONObject(field);
				JSONObject contentElem = jobj.getJSONObject("content");
				
				floors.add(contentElem);
			}
			
			JSONObject jobj = floors.get(random.nextInt(floors.size()));
			question = jobj.getString("content");
			int floorNum = jobj.getInt("post_no");
			System.out.println("Use question of floor: " + floorNum + "; " + URLDecoder.decode(question, "UTF-8"));
		}
		
		question = question.replaceAll("【.*】", "");
		String[] terms = Tuling.getAnswer(question);
		if("100000".equals(terms[0])){
			replyContent = terms[1];
			LOG.info("Answer: {}", replyContent);
		}
		else{
			return;
		}
		
		String url = "http://tieba.baidu.com/f/commit/post/add";
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Accept", "application/json, text/javascript, */*; q=0.01");
		hds.put("Cache-Control", "no-cache");
		hds.put("Host", "tieba.baidu.com");
		hds.put("Referer", "http://tieba.baidu.com/p/"+ tid +"?pn=1");
		hds.put("X-Requested-With", "XMLHttpRequest");
		hds.remove("Cookie");
	
		Map<String, Object> params = new HashMap<>();
		
		params.put("__type__", "reply");
		params.put("content", replyContent);
		params.put("fid", lastPostInfo.getFid());
		params.put("files", "[]");
		params.put("floor_num", "2");
		params.put("ie", "utf-8");
		params.put("kw", lastPostInfo.getForumName());
		params.put("mouse_pwd", "61,62,48,36,57,62,59,58,1,57,36,56,36,57,36,56,36,57,36,56,36,57,36,56,36,57,36,56,1,57,56,63,61,56,1,57,62,48,56,36,57,56,61,56,14411909192080");
		params.put("mouse_pwd_isclick", "0");
		params.put("mouse_pwd_t", "" + System.currentTimeMillis());
		params.put("rich_text", "0");
		params.put("tbs", lastPostInfo.getTbs());
		params.put("tid", tid);
		params.put("vcode_md5", "");
	
		String text = http.post(url, params, hds);
		
		Pattern p = Pattern.compile("no\":(\\d+)");
		Matcher m = p.matcher(text);
		String no = "";
		
		if(m.find()){
			no = m.group(1);
		}
		
		if("0".equals(no)){
			LOG.info("Reply success, {}", lastPostInfo.getTid());
		}
		else{
			LOG.info("Reply fail, {}; code: {}", tid, no);
		}
	}
	
	public Map<String, String> getGeneralHeaders(){
		
		Map<String, String> hds = super.getGeneralHeaders();

		hds.put("Host", "passport.baidu.com");
		hds.put("Referer", "http://www.baidu.com/");
		
		return hds;
	}
	
	public void parseUserInfo() throws FileNotFoundException, ScriptException{

		URL url = BaiduRobot.class.getClassLoader().getResource("com/echoman/robot/baidu/test.js");
		engine.setBindings(bds, 100);
		FileReader reader = new FileReader(new File(url.getPath()));
		engine.eval(reader);
		
		System.out.println(bds.get("po"));
	}
	
	private class ReplyTask implements Runnable{

		private String tid;
		private String content;
		
		public ReplyTask(String tid, String content){
			this.tid = tid;
			this.content = content;
		}
		
		@Override
		public void run(){
			try {
				
				interReply(tid, content);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void backgroundSign() {
		sign();
	}
	
	@Override
	public void backgroundProcess() {
		if(!isLogin()){
			return;
		}
		try {
			wanderTieba("");
		} catch (Exception e) {
			LOG.error("Error wander, {}", e.getStackTrace());
		}
	}
	
	private static int random(int m, int n){
		return (int) (Math.random() * (n - m) + m);
	}

	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/baidu/";
	}

	@Override
	public void sign() {
		if(!isLogin()){
			login();
		}
		for(ForumInfo finfo: userInfo.getForums()){
			try {
				onekeySign(finfo.getName());
				Thread.sleep(1000);
			} catch (Exception e) {
				LOG.error("Sign error, {}", e.getStackTrace());
			}
		}
	}
}

