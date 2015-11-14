package com.echoman.robot.weibo.cn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.echoman.robot.weibo.model.WeiboUser;
import com.echoman.util.RegexUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class WeiboCNDocParser {

	public static Set<WeiboUser> parseFollowsById(String html){
		
		Document doc = Jsoup.parse(html);
		Elements elems = doc.select(".follow_list .follow_item .mod_info");
		Set<WeiboUser> beans = Sets.newHashSet();
		
		for(Element e: elems){
			
			Element a = e.children().first().children().first();
			String name = a.text();
//			String href = a.attr("href");
//			String id0 = RegexUtil.getGroup1(href, "/u/(\\d+)");
			String usercard = a.attr("usercard");
			String id0 = RegexUtil.getGroup1(usercard, "id=(\\d+)");
			
			beans.add(new WeiboUser(id0, name));
		}
		
		return beans;
	}
	
	public static Set<WeiboUser> parseUserOfSearch(String html){
		
		Document doc = Jsoup.parse(html);
		Elements elems = doc.select(".pl_personlist .list_person .person_detail .person_name .W_texta");
		Set<WeiboUser> beans = Sets.newHashSet();
		
		for(Element a: elems){

			String name = a.attr("title");
			String href = a.attr("href");
			String id0 = RegexUtil.getGroup1(href, "/u/(\\d+)");
			
			if(Strings.isNullOrEmpty(id0)){
				id0 = a.attr("uid");
			}
			
			System.out.println(name + " " + id0);
			beans.add(new WeiboUser(id0, name));
		}
		
		return beans;
	}

	public static Map<String, Object> parseVK(String html) {
		Document doc = Jsoup.parse(html);
		Elements elems = doc.select("form div input");
		
		Map<String, Object> map = Maps.newHashMap();
		
		for(Element elem: elems){
			String type = elem.attr("type");
			if(isUI(type)) continue;
			String name = elem.attr("name");
			String value = elem.attr("value");
			map.put(name, value);
		}
		
		return map;
	}
	
	private static boolean isUI(String type){
		return "checkbox".equalsIgnoreCase(type) || "submit".equalsIgnoreCase(type);
	}

	public static Set<WeiboUser> parseUserOfSearchCN(String html, String keyword) {
		
		Document doc = Jsoup.parse(html);
		Elements elems = doc.select("table tbody tr td:nth-child(2)");
		Set<WeiboUser> beans = Sets.newHashSet();
		
		for(Element elem: elems){
			
			Elements a = elem.select("a");
			Element a0 = a.get(0);
			String name = a0.html();
			String url = a0.attr("href");
			
			Element a1 = a.get(1);
			String attention = a1.attr("href");
			String id = RegexUtil.getGroup1(attention, "uid=(\\d+)");
			
			WeiboUser user = new WeiboUser(id, name);
			user.setUrl(url);
			user.setGrabTag(keyword);
			beans.add(user);
		}
		
		return beans;
	}
	
	public static void parseUserInfo(String html, WeiboUser user){
		Document doc = Jsoup.parse(html);
		
		Elements elems = doc.select("div");
		Element baseInfo = elems.get(4);
		
		String baseText = baseInfo.html();
		String[] items = baseText.split("<br />");
		for(String item: items){
			String[] terms = RegexUtil.getGroup12(item.replaceAll("\n", ""), "(.{2}):(.*)");
//			String label = terms[0];
			String value = terms[1];
//			System.out.println(value);
			if(item.contains("性别")){
				user.setGender(value);
			}
			if(item.contains("地区")){
				user.setBaseAddress(value);
			}
			if(item.contains("简介")){
				user.setIntro(value);
			}
			if(item.contains("生日")){
				
				if(value.length() == 10){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					try {
						user.setBirthday(sdf.parse(value));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				
			}
			if(item.contains("标签")){
				String tagString = "";
				String[] tags = value.split("&nbsp;");
				for(String tg: tags){
					String tagName = RegexUtil.getGroup1(tg, "<a[^>]*>(.*)</a>");
					if(!tagName.contains("更多")){
						tagString += (tagName + ",");
					}
				}
				user.setTag(tagString);
			}
		}
		/**
		 * school
		 */	
		Element studyInfo = elems.get(6);
		String studyText = studyInfo.html();
		String[] items1 = studyText.split("<br />");
		user.setSchool(items1[0].substring(1).replaceAll("middot;", "").replaceAll("&nbsp;", " "));
//		System.out.println(user);
		/**
		 * company
		 */
		Element jobInfo = elems.get(8);
		String jobText = jobInfo.html();
		String[] items2 = jobText.split("<br />");
		user.setCompany(items2[0].substring(1).replaceAll("middot;", "").replaceAll("&nbsp;", " "));
		
//		System.out.println("2>>> " + user);
	}
	
	public static void parseUserInfo1(String html, WeiboUser user){
		Document doc = Jsoup.parse(html);
		
		Elements elems = doc.select(".u .tip2");
		Element elem = elems.get(0);
		
		String infoText = elem.html();
		String[] terms = infoText.split("&nbsp;");
		
		for(String term: terms){
			String value = RegexUtil.getGroup1(term, "\\[(\\d+)\\]");
			if(term.contains("微博")){
				user.setSendCount(Integer.valueOf(value));
			}
			if(term.contains("关注")){
				user.setAttentions(Integer.valueOf(value));
			}
			if(term.contains("粉丝")){
				user.setFans(Integer.valueOf(value));
			}
		}
//		System.out.println("3>>> " + user);
	}
	
	public static void main(String...strings){
		String html = "<span class=\"tc\">微博[23915]</span>&nbsp;" +
"<a href=\"/2104483152/follow\">关注[1429]</a>&nbsp;" +
"<a href=\"/2104483152/fans\">粉丝[12109]</a>&nbsp;" +
"<a href=\"/attgroup/opening?uid=2104483152\">分组[2]</a>&nbsp;" +
"<a href=\"/at/weibo?uid=2104483152\">@他的</a>;";
		
		String[] tags = html.split("&nbsp;");
		
		for(String lb: tags){
			String tagName = RegexUtil.getGroup1(lb, "\\[(\\d+)\\]");
			System.out.println(tagName);
		}
		
	}
}
