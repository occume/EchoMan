package com.echoman.robot.weibo;

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

public class WeiboDocParser {

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

	public static void parseUserOfSearchCN(String html) {
		
		Document doc = Jsoup.parse(html);
		Elements elems = doc.select("table tbody tr td:nth-child(2) a");
		Set<WeiboUser> beans = Sets.newHashSet();
		
		for(Element a: elems){
			System.out.println(a.html());
		}
	}
}
