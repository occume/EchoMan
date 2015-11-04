package com.echoman.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocUtil {
	
	private final static Logger LOG = LoggerFactory.getLogger(DocUtil.class);

	public static String getScriptText1(String html, String regex){
		
		Document doc = Jsoup.parse(html);
		Elements scripts = doc.select("script");
		
		LOG.debug("Scripts.size: {}", scripts.size());
		
		String result = null;
		
		for(Element e: scripts){
			String html0 = e.html();
			if(html0.matches(makeMatchRegex(regex))){
				result = html0;
			}
		}
		
		return result;
	}
	
	private static String makeMatchRegex(String regex){
		return ".*" + regex + ".*";
	}
	
	public static String getScriptText2(String html, String regex){
		
		Document doc = Jsoup.parse(html);
		Elements scripts = doc.select("script");
		
		LOG.debug("Scripts.size: {}", scripts.size());
		
		String result = "";
		int i = 0;
		
		for(Element e: scripts){
			
			String html0 = e.html();
			if(i == 2) break;
			
			if(html0.matches(makeMatchRegex(regex))){
				result += html0;
				i++;
			}
		}
		
		return result;
	}
}
