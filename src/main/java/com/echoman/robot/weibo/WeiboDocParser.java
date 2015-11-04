package com.echoman.robot.weibo;

import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.echoman.robot.weibo.model.WeiboUser;
import com.echoman.util.RegexUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class WeiboDocParser {

	public static Set<WeiboUser> parseFollowsById(String html){
		
		Document doc = Jsoup.parse(html);
		Elements elems = doc.select(".follow_list .follow_item .mod_info");
		Set<WeiboUser> beans = Sets.newHashSet();
		
		for(Element e: elems){
			
			Element a = e.children().first().children().first();
			String name = a.text();
			String href = a.attr("href");
			String id0 = RegexUtil.getGroup1(href, "/u/(\\d+)");
			
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
}
