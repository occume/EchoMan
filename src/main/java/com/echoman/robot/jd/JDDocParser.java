package com.echoman.robot.jd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.echoman.robot.jd.model.RecommendProduct;

public class JDDocParser {

	public static void parseCategory(String html, RecommendProduct product){
		Document doc = Jsoup.parse(html);
		String selector = "#root-nav div.w div.breadcrumb";
		Element navElem = doc.select(selector).first();
		String type1 = navElem.select("strong a").first().text();
		
		String type2 = navElem.child(1).select("a").get(0).text();
		String type3 = navElem.child(1).select("a").get(1).text();
		
		String type4 = navElem.child(2).select("a").get(0).text();
		
		product.setItemType1(type1);
		product.setItemType2(type2);
		product.setItemType3(type3);
		product.setItemType4(type4);
		
		selector = "#preview div#spec-n1.jqzoom img";
		Element imgElem = doc.select(selector).first();
		String imageUrl = imgElem.attr("src");
		
		product.setImageUrl(imageUrl);
	}
}
