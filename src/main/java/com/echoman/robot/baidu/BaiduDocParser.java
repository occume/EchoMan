package com.echoman.robot.baidu;

import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.robot.baidu.model.BaiduForum;
import com.echoman.robot.baidu.model.BaiduUser;
import com.echoman.robot.baidu.model.ReplyInfo;
import com.echoman.util.RegexUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class BaiduDocParser {
	
	private final static Logger LOG = LoggerFactory.getLogger(BaiduDocParser.class);
	/**
	 * parse forums, follows, visitors from main page of user
	 * @param html
	 * @return
	 */
	public static BaiduUser parseMainOfUser(String html){
		
		Document doc = Jsoup.parse(html);
		BaiduUser ret = new BaiduUser();
		
		/**
		 *  follows
		 */
		Elements 
		
		elems = doc.select("#concern_wrap_concern .concern_item a:first-child");
		for(Element elem: elems){
			String href = elem.attr("href");
			parseUN(ret.getFollows(), href);
		}
		/**
		 * visitors
		 */
		elems = doc.select("#visitor_card_wrap .visitor_card a");
		for(Element elem: elems){
			String href = elem.attr("href");
			parseUN(ret.getVisitors(), href);
		}
		/**
		 * Forums
		 */
		elems = doc.select("#forum_group_wrap a span:first-child");
		for(Element elem: elems){
			String fid = elem.parent().attr("data-fid");
			String name = elem.text();
			String level = elem.select("span").first().classNames().toString();
			ret.addForum(new BaiduForum(fid, name, level));
		}
		
		return ret;
	}
	
	private static void parseUN(Set<BaiduUser> users, String input){
		String un = RegexUtil.getGroup1(input, "un=(.*)&");
		if(!Strings.isNullOrEmpty(un)){
			users.add(new BaiduUser(un));
		}
	}
	
	public static BaiduUser parseProfile(String html){
		
		Document document = Jsoup.parse(html);
		
		Element userInfoElem = document.getElementById("user_info");
		Elements headImg = userInfoElem.select(".media_left .head_img");
		String src = headImg.first().attr("src");
		
		Elements userNameElem = userInfoElem.select(".media_right .user_name a");
		String uName = userNameElem.text();
		
		Element likeForum = document.getElementById("likeforumwraper");
		
		Elements forums = likeForum.select(".u-f-item");
		
		BaiduUser user = new BaiduUser(uName, src);
		
		for(Element elem: forums){
			String fid = elem.attr("data-fid");
			String name = elem.text();
			String level = elem.select("span").first().classNames().toString();
			user.addForum(new BaiduForum(fid, name, level));
		}
		
		return user;
	}
	
	public static List<ReplyInfo> parseReplies(String input) throws JSONException{
		
		List<ReplyInfo> replies = Lists.newArrayList();
		JSONArray arr = new JSONArray(input);
		
		for(int i = 0; i < arr.length(); i++){
			JSONObject obj = arr.getJSONObject(i);
			String time = obj.getString("time");
			JSONArray feed = obj.getJSONArray("feed_item_list");
			JSONObject info = feed.getJSONObject(0);
			String url0 = info.getString("url");
			String content0 = info.getString("content");
			
			replies.add(new ReplyInfo(url0, time, content0));
		}
		
		LOG.info("Get {} replies", replies.size());
		
		return replies;
	}
	
	public static int parseReplyFloorNum(String html, String pid) throws JSONException{
		
		Document doc = Jsoup.parse(html);
		
		Elements elems = doc.select(".l_post_bright");
		
		String fieldString = "";
		for(Element elem: elems){
			String field = elem.attr("data-field");
			if(field.contains(pid)){
				fieldString = field;
			}
		}
		
		JSONObject jobj = new JSONObject(fieldString);
		JSONObject contentElem = jobj.getJSONObject("content");
		int floorNum = contentElem.getInt("post_no");
		
		return floorNum;
	}
	
	public static void parseForumInfo(String html, BaiduForum forum) {
		
		Document doc = Jsoup.parse(html);
		
		Elements cardNumElems = doc.select(".card_num");
		if(!cardNumElems.isEmpty()){
			Element cardMenNum = cardNumElems.select(".card_menNum").first();
			Element cardInfoNum = cardNumElems.select(".card_infoNum").first();
			
			Element sloganElem = doc.select(".card_slogan").first();
			
			if(cardMenNum != null){
				forum.setMemberNum(parseInt(cardMenNum.html()));
				forum.setPostNum(parseInt(cardInfoNum.html()));
				forum.setSlogan(sloganElem.html());
			}
		}
		else{
			Elements numBox = doc.select(".fans_numbox");
			Element visitNum = numBox.select(".j_visit_num").first();
			Element postNum = numBox.select(".j_post_num").first();
			if(visitNum != null){
				forum.setMemberNum(parseInt(visitNum.html()));
				forum.setPostNum(parseInt(postNum.html()));
			}
		}
	}

	private static int parseInt(String input){
		String intString = input.replaceAll(",",  "");
		return Integer.valueOf(intString);
	}

	public static void traversePost(String html) {
		Document doc = Jsoup.parse(html);
		Elements elems = doc.select("#j_p_postlist .l_post .d_post_content_main cc .d_post_content");
		for(Element elem: elems){
			System.out.println("--------------------------------------");
			System.out.println(elem.html());
		}
	}
	public static void main(String...strings){

	}
}
