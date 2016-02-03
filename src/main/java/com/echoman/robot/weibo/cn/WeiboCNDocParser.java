package com.echoman.robot.weibo.cn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

	public static Set<WeiboUser> parseFollowsById(String html, WeiboUser user){
		
		Document doc = Jsoup.parse(html);
		Elements elems = doc.select(".c table tbody tr");
		Set<WeiboUser> beans = Sets.newHashSet();
	
		for(Element elem: elems){
			
			Element child1 = elem.child(1);
			Element child1a = child1.child(0);
			String name = child1a.text();
			String href = child1a.attr("href");
			String id = RegexUtil.getGroup1(href, "/u/(\\d+)");
			
			String url = href.substring(15);
			
			WeiboUser user0 = new WeiboUser(id, name);
			user0.setDepth(user.getDepth() + 1);
			user0.setUrl(url);
			user0.setGrabTag(user.getUserId());
			
			beans.add(user0);
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
	
	private static Element getElementByText(Elements elems, String text){
		Element ret = null;
		for(Element elem: elems){
			if(elem.text().contains(text)) ret = elem;
		}
		return ret;
	}
	
	
	public static void parseUserInfo(String html, WeiboUser user){
		
		Document doc = Jsoup.parse(html);
		Elements elems = doc.select("div");
		
		Element preBase = getElementByText(elems, "基本信息");
		
		if(preBase != null){
			
			Element baseInfo = preBase.nextElementSibling();
			
			String baseText = baseInfo.html();
			String[] items = baseText.split("<br />");
			
			for(String item: items){
				String[] terms = RegexUtil.getGroup12(item.replaceAll("\n", ""), "(.{2}):(.*)");
//				String label = terms[0];
				String value = terms[1];

				if(item.contains("性别")) user.setGender(value);
				if(item.contains("地区")) user.setBaseAddress(value);
				if(item.contains("简介")) user.setIntro(value);
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
		}
		
		/**
		 * school
		 */	
		Element preSchool = getElementByText(elems, "学习经历");
		if(preSchool != null){
			Element studyInfo = preSchool.nextElementSibling();
			String studyText = studyInfo.html();
			String[] items1 = studyText.split("<br />");
			String school = items1[0].substring(1)
					.replaceAll("middot;", "").replaceAll("&nbsp;", " ").replaceAll("\n", "");
			user.setSchool(school);
		}

		/**
		 * company
		 */
		Element preCompany = getElementByText(elems, "工作经历");
		if(preCompany != null){
			Element jobInfo = preCompany.nextElementSibling();
			String jobText = jobInfo.html();
			String[] items2 = jobText.split("<br />");
			String company = items2[0].substring(1)
					.replaceAll("middot;", "").replaceAll("&nbsp;", " ").replaceAll("\n", "");
			user.setCompany(company);
		}
		
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
	}
	

	public static String getMsgFormAction(String html) {
		Document doc = Jsoup.parse(html);
		Element form = doc.getElementById("reply");
		String action = form.attr("action");
		return action;
	}
	
	public static void main(String...strings) throws IOException{
		
		byte[] buf = Files.readAllBytes(Paths.get("D:/tmp/weibocnfans.txt"));
		String html = new String(buf, "UTF-8");
		System.out.println(html);
//		parseFollowsById(html);
	}

}
