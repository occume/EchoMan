package com.echoman.robot.baidu;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.d3f.Tuling;
import com.echoman.robot.AbstractHelper;
import com.echoman.robot.baidu.model.BaiduForum;
import com.echoman.robot.baidu.model.BaiduUser;
import com.echoman.robot.baidu.model.PostInfo;
import com.echoman.robot.baidu.model.ReplyInfo;
import com.echoman.util.CommonUtil;
import com.echoman.util.DocUtil;
import com.echoman.util.RegexUtil;
import com.google.common.collect.Maps;

public class BaiduRobotHelper extends AbstractHelper{
	
	private final static Logger LOG = LoggerFactory.getLogger(BaiduRobotHelper.class);
	
	private BaiduRobot robot;
	private PostInfo lastPostInfo;
	
	private ConcurrentHashMap<String, PostInfo> echoMap;
	
	public BaiduRobotHelper(BaiduRobot robot){
		this.robot = robot;
		echoMap = new ConcurrentHashMap<>();
	}

	public BaiduUser getUserByName(String un){
		
		String url = "http://tieba.baidu.com/home/main?un="+ un +"&fr=pb&ie=utf-8";
		
		Map<String, String> headers = robot.getGeneralHeaders();
		headers.put("Host", "tieba.baidu.com");
		headers.put("Referer", "http://tieba.baidu.com/");
		
		String html = http.get(url, headers);

		BaiduUser user = BaiduDocParser.parseMainOfUser(html);
		return user;
	}
	
	private String token;
	private String gid;
	private String rsakey;
	private String encryptPassword;
	/**
	 * 用名字登录 不要用邮箱
	 * @throws IOException
	 */
	public void doLogin() throws IOException{
		
		LOG.info(robot.getName() + " request login ...");
		firstRequest4Cookie();
		
		try {
			prepareParam();
		} catch (ScriptException e) {
			LOG.error("Login error, {}", e);
		}
		
		String url = "https://passport.baidu.com/v2/api/?login";
		
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
		params.put("username", robot.getAccount());
		params.put("password", encryptPassword);
		params.put("verifycode", "");
		
		String ctt = http.post(url, params, hds);

		String errNo = RegexUtil.getGroup1(ctt, "err_no=(\\d+)");
		
		if("0".equals(errNo)){
			http.setLogined(true);
		}
		LOG.info("Login status: {}", errNo);
	}
	
	private void firstRequest4Cookie(){
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "www.baidu.com");
		hds.remove("Referer");
		
		http.get("https://www.baidu.com/", hds);
	}
	
	private void prepareParam() throws FileNotFoundException, ScriptException{
		
		loadAndRunFunction(null, "function", null);
		
		String gid = getValueOfVar("guideRandom");
		
		String url = "https://passport.baidu.com/v2/api/?getapi&tpl=mn&apiver=v3&"
				+ "tt="+ System.currentTimeMillis() +"&class=login&"
				+ "gid="+ gid +"&logintype=dialogLogin&callback=getToken";
		
		String content = http.get(url, getGeneralHeaders());
		runFunction(content);
		getPublicKey();
	}
	
	private void getPublicKey() throws FileNotFoundException, ScriptException{
		
		token 	= getValueOfVar("token");
		gid 	= getValueOfVar("guideRandom");
		
		String url = "https://passport.baidu.com/v2/getpublickey?"
				+ "token="+ token + "&tpl=mn&apiver=v3"+ "&tt="+ System.currentTimeMillis()
				+ "&gid=" + gid + "&callback=bd__cbs__fqrp2h";
		
		String content = http.get(url);

		Pattern pat = Pattern.compile("([^()]+)");
		Matcher mat = pat.matcher(content);

		String params = "";
		
		//skip function name
		mat.find();
		if(mat.find()) params = mat.group();
		
		setValueOfVar("cert", "");
		setValueOfVar("rawPassword", robot.getPassword());
		
		runFunction("var cert = " + params + ";");
		loadAndRunFunction(null, "util", null);
		
		rsakey = getValueOfVar("rsakey");
		encryptPassword = getValueOfVar("encryptPassword");
		
		LOG.info("Get token: {}", token);
		LOG.info("Generate gid: {}", gid);
		LOG.info("Get rsakey: {}", rsakey);
		LOG.info("Get encryptPassword: {}", encryptPassword);
	}
	/**
	 * login end
	 */
	public void onekeySign(String formName) throws IOException{
		
		String url = "http://tieba.baidu.com/sign/add";
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "tieba.baidu.com");
		headers.put("Referer", "http://tieba.baidu.com/");
		headers.put("x-requested-with", "XMLHttpRequest");
		headers.remove("Cookie");
		
		Map<String, Object> 
		params = new HashMap<>();
		params.put("ie", "utf-8");
		params.put("tbs", "");
		params.put("kw", formName);
		
		String content = http.post(url, params, headers);
		String no = RegexUtil.getGroup1(content, "no\":(\\d+)");
		
		printResult(no);
	}
	
	/**
	 * get profile of user from main of tieba
	 * @return
	 */
	public BaiduUser getProfile(){
		
		String url = "http://tieba.baidu.com/";
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Referer", "https://www.baidu.com/index.php?tn=monline_3_dg");
		
		String html = http.get(url);
		
		String pageDataText = DocUtil.getScriptText1(html, "var PageData");
		
		runStatement(pageDataText);
		runFunction("fillModel();");
		
		String userId = getValueOfVar("userId");
		String userName = getValueOfVar("userName");
		String portrait = getValueOfVar("portrait");
		
		BaiduUser user = BaiduDocParser.parseProfile(html);
		user.setUserId(userId);
		user.setPortrait(portrait);
		user.setUserName(userName);

		return user;
	}
	
	public List<ReplyInfo> getReplies() throws JSONException{
		
		String url = "http://tieba.baidu.com/i/sys/jump?u=" + robot.getUserInfo().getPortrait() + "&type=replyme";
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "tieba.baidu.com");
		hds.put("Referer", "http://tieba.baidu.com/");
		
		String html = http.get(url, hds, true);
		
		LOG.debug(html);
		
		String pageDataText = DocUtil.getScriptText1(html, "feed_item_list");;

		LOG.debug(pageDataText);
		
		String jsonString = RegexUtil.getGroup(pageDataText, "\\[\\{.*\\d{2}\"\\}\\]:?");
		List<ReplyInfo> replies = BaiduDocParser.parseReplies(jsonString);
		
		return replies;
	}
	
	public void replyReplyme(ReplyInfo reply) throws Exception{

		String answer = "heihei";
		
		/**
		 * tuling robot
		 **/ 
		String[] terms = Tuling.getAnswer(reply.getContent());
		if("100000".equals(terms[0])){
			answer = terms[1];
			LOG.info("question: {}; answer: {}", reply.getContent(), answer);
			
			String[] arr = RegexUtil.getGroup12(reply.getUrl(), "/p/(\\d*).+pid=(\\d*)");
			String tid = arr[0], pid = arr[1];

			LOG.info("tid: {}", tid);
			
			parseLastPostInfo(tid);
			
			if(currPostHtml == null) return;
			
			ReplymeInfo info = new ReplymeInfo();
			info.tid = tid;
			info.pid = pid;
			info.floorNum = BaiduDocParser.parseReplyFloorNum(currPostHtml, pid) + "";
			
			info.content = answer;
			doReplyReplyme(info);
		}
	}
	
	private void doReplyReplyme(ReplymeInfo info) throws IOException{
		
		String url = "http://tieba.baidu.com/f/commit/post/add";
			
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "tieba.baidu.com");
		headers.put("Origin", "http://tieba.baidu.com");
		headers.put("Referer", "http://tieba.baidu.com/p/" + info.tid);
		headers.put("X-Requested-With", "XMLHttpRequest");
		
		Map<String, Object> 
		params = Maps.newHashMap();
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
		
		String html = http.post(url, params, headers);
		String no = RegexUtil.getGroup1(html, "no\":(\\d+)");;
		
		printResult(no);
	}
	
	protected static class ReplymeInfo{
		static final ReplymeInfo EMPTY = new ReplymeInfo();
		String tid, floorNum, pid, content;
	}
	
	private String currPostHtml;
	
	public void parseLastPostInfo(String tid){
		
		getCurrThreadHtml(tid);
		
		if(currPostHtml.contains("贴吧404")){
			LOG.info("This thread is deleted, {}", tid);
			currPostHtml = null;
			return;
		}
		
		String scriptText = DocUtil.getScriptText2(currPostHtml, "var PageData");

		runStatement(scriptText);
		runFunction("fillThreadInfo();");
		
		lastPostInfo = new PostInfo(
				getValueOfVar("fid"), 
				getValueOfVar("forumName"), 
				getValueOfVar("tbs"), 
				tid, 
				getValueOfVar("threadTitle"), 
				getValueOfVar("totalPage"), 
				getValueOfVar("replyNum"));

//		System.out.println(lastPostInfo);
	}
	
	/**
	 * tid should like '/p/2323452345'
	 * @param tid
	 */
	private void getCurrThreadHtml(String tid){
		
		String url = "http://tieba.baidu.com/p/" + tid;
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "tieba.baidu.com");
		hds.put("Referer", "http://tieba.baidu.com/p/"+ tid +"?pn=1");
		
		currPostHtml = http.get(url, hds);
	}
	
	protected String doReply(String tid, String content) throws IOException, JSONException{
		
		LOG.info("Reply {}", tid);
		
		parseLastPostInfo(tid);
		echoMap.put(tid, lastPostInfo);
		
		String replyContent = content;
		String question = null;
		PostInfo postInfo = lastPostInfo;
		
		if(postInfo.getCurrFloor() == 0){
			postInfo.setCurrFloor(1);
			question = postInfo.getTitle();
		}
		else{
			String totalPage = postInfo.getTotalPage();
			int pn = Integer.valueOf(totalPage);
			Random random = new Random();
			
			parseLastPostInfo(tid + "?pn=" + random.nextInt(pn));
			Document doc = Jsoup.parse(currPostHtml);
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
			return "";
		}
		
		String url = "http://tieba.baidu.com/f/commit/post/add";
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
		headers.put("Cache-Control", "no-cache");
		headers.put("Host", "tieba.baidu.com");
		headers.put("Referer", "http://tieba.baidu.com/p/"+ tid +"?pn=1");
		headers.put("X-Requested-With", "XMLHttpRequest");
		headers.remove("Cookie");
	
		Map<String, Object> 
		params = new HashMap<>();
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
	
		String text = http.post(url, params, headers);
		String no = RegexUtil.getGroup1(text, "no\":(\\d+)");;
		printResult(no);

		return no;
	}
	
	public void getForumInfo(BaiduForum forum){
		
		String url = "http://tieba.baidu.com/f?kw="+ forum.getName() +"&ie=utf-8";
		
		Map<String, String> 
		hds = getGeneralHeaders();
		hds.put("Host", "tieba.baidu.com");
		hds.put("Referer", "http://tieba.baidu.com");
		
		String html = http.get(url);
		BaiduDocParser.parseForumInfo(html, forum);
	}

	public void getPicsOfPost(String tid) {
		
		parseLastPostInfo(tid + "?see_lz=1");
		BaiduDocParser.traversePost(currPostHtml);
		int totalPage = Integer.valueOf(lastPostInfo.getTotalPage());
		
		for(int i = 2; i <= totalPage; i++){
			CommonUtil.wait2(2000, 10000);
			System.out.println("=====================");
			parseLastPostInfo(tid + "?see_lz=1&pn=" + i);
			BaiduDocParser.traversePost(currPostHtml);
		}
	}
	
	private void printResult(String errCode){
		if("0".equals(errCode)){
			LOG.info("Perform ok, {}", (lastPostInfo != null ? lastPostInfo.getTid() : "..."));
		}
		else{
			LOG.info("Reply fail, {}; code: {}", (lastPostInfo != null ? lastPostInfo.getTid() : "..."), errCode);
		}
	}
	
	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/baidu/";
	}
}
