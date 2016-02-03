package com.echoman.robot.qq;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QQDocParser {
	
	private final static Logger LOG = LoggerFactory.getLogger(QQDocParser.class);
	
	public static void parseGroupInfo(String html){
		Document doc = Jsoup.parse(html);
		Elements elems = doc.select(".group_widget_id .id_content");
		
		for(Element elem: elems){
			System.out.println(elem.html());
		}
	}
}
