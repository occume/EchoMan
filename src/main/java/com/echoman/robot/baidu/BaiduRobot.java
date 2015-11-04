package com.echoman.robot.baidu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.model.RobotBean;
import com.echoman.robot.AbstractRobot;
import com.echoman.robot.Robot;
import com.echoman.robot.baidu.model.BaiduUser;
import com.echoman.robot.baidu.model.BaiduForum;
import com.echoman.robot.baidu.model.PostInfo;
import com.echoman.robot.baidu.model.ReplyInfo;
import com.echoman.util.CommonUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class BaiduRobot extends AbstractRobot{
	
	private final static Logger LOG = LoggerFactory.getLogger(BaiduRobot.class);
	
	private final static String TYPE = "BAIDU";
	
	private BaiduRobotHelper helper = new BaiduRobotHelper(this);
	private BaiduUser user;
	
	private int replyReplymeCont = 0;
	private Date lastReplyReplymeDate;
	
	public BaiduRobot(){}
	
	public BaiduRobot(RobotBean bean){
		super(bean);
	}
	
	@Override
	public Robot login() {
		try {
			helper.doLogin();
		} catch (IOException e) {
			LOG.error("Login fail, {}", e);
		}
		/**
		 * if success, get profile
		 */
		getProfile();
		return this;
	}
	
	public void replyReplyme() throws Exception{
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		/**
		 * 获取所有回复我的回复
		 */
		List<ReplyInfo> replies = getReplies();
	
		if(replies.size() == 0){
			lastReplyReplymeDate = new Date();
			return;
		}
		
		/**
		 * 第一次回复
		 **/
//		if(replyReplymeCont == 0){
//			replyReplymeCont++;
//			
//			ReplyInfo item = replies.get(0);
//			lastReplyReplymeDate = df.parse("2015-" + item.getTime());
//			helper.replyReplyme(item);
//			return;
//		}
		
		for(int i = replies.size() - 1; i >=0; i--){
			ReplyInfo reply = replies.get(i);
			LOG.debug("reply: {}", reply); 
			Date replyDate = df.parse("2015-" + reply.getTime());
			
//			if(replyDate.after(lastReplyReplymeDate)){
				lastReplyReplymeDate = replyDate;
				helper.replyReplyme(reply);
				Thread.sleep(CommonUtil.random(2000, 10000));
//			}
		}
	}
	
	public void replyThread(String tid, String content){
		try {
			doReply(tid, content);
		} catch (Exception e) {
			LOG.error("Reply thread error, {}", e);
		}
	}
	
	
	@Override
	public String getName() {
		return this.account + "@" + TYPE;
	}
	
	public BaiduUser getUserInfo(){
		return user;
	}

	public void getProfile(){
		
		user = helper.getProfile();
		
	}
	/**
	 *  厘清百度贴吧的
	 */
	public void getUserByName(String userName){
		BaiduUser user = helper.getUserByName(userName);
		System.out.println(user.getForums());
		BaiduScheduler.instance().addAllUser(user.getFollows());
		BaiduScheduler.instance().addAllUser(user.getVisitors());
		BaiduScheduler.instance().addAllForums(user.getForums());
	}
	
	
	/**
	 * Get all replies of this account
	 * @return
	 * @throws JSONException
	 */
	public List<ReplyInfo> getReplies() throws JSONException{
		
		return helper.getReplies();

	}
	
	public void getPicsOfPost(){
		
		helper.getPicsOfPost("3086699379");
		
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
			un = URLEncoder.encode(user.getUserName(), "gbk");
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
	/**
	 * sign a forum once
	 * @param formName
	 * @throws IOException
	 */
	public void onekeySign(String formName) throws IOException{
		
		helper.onekeySign(formName);
		
	}
	/**
	 * fill the forum
	 * @param forum
	 */
	public void getForumInfo(BaiduForum forum){
		
		helper.getForumInfo(forum);
		
	}
	
	public List<String> getPostsOfForum(String forumName){
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
	
	
	public String doReply(String tid, String content) throws IOException, JSONException{
		
		return helper.doReply(tid, content);

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
	
	@Override
	public void backgroundSign() {
		sign();
	}
	
	@Override
	public void backgroundProcess() {
		
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
		for(BaiduForum finfo: user.getForums()){
			try {
				onekeySign(finfo.getName());
				Thread.sleep(1000);
			} catch (Exception e) {
				LOG.error("Sign error, {}", e.getStackTrace());
			}
		}
	}

	@Override
	public boolean isLogin() {
		return helper.isLogin();
	}
}

