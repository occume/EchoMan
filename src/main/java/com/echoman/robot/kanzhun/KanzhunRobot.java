package com.echoman.robot.kanzhun;

import java.util.Map;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.echoman.robot.AbstractRobot;
import com.echoman.robot.Robot;
import com.google.common.collect.Lists;

public class KanzhunRobot extends AbstractRobot {

	@Override
	public Robot login() {
		return null;
	}

	@Override
	public void sign() {
		
	}

	@Override
	public String getJSFileDirectory() {
		return null;
	}

	Queue<String> cityHrefs = Lists.newLinkedList();

	public void getCityList(){
		
		String url = "http://www.kanzhun.com/companyl/search/?ka=banner-com";
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "www.kanzhun.com");
		hds.put("Referer", "http://www.kanzhun.com/plc52p1.html?ka=com-more");
//		hds.put("Cookie", "__c=1445520551; W_CITY_S_V=1; __g=-; LAST_V_C=|11514|; __l=r=&l=%2F; __a=76075338.1445520551..1445520551.7.1.7.7; AB_T=abva");
		
		String html = http.get(url, hds);
		
		Document doc = Jsoup.parse(html);
		
		Elements citys = doc.select(".host_city a");
		
		for(Element e: citys){
			String name = e.text();
			String href = e.attr("href");
			System.out.println(name);
			cityHrefs.offer(href);
		}
		
	}
	
	public void getFieldList(){
		for(String href: cityHrefs){
			parseField(href);
		}
	}
	
	private void parseField(String cityHref){
		
		String url = "http://www.kanzhun.com/";
		Queue<String> fieldHrefs = Lists.newLinkedList();
		
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "www.kanzhun.com");
		hds.put("Referer", "http://www.kanzhun.com/plc52p1.html?ka=com-more");
		
		String html = intervalHttp(url + cityHref, hds);
		Document doc = Jsoup.parse(html);
		
		Elements fields = doc.select(".industry_field .more_list span a");
	
		for(Element e: fields){
			String title = e.attr("title");
			String href = e.attr("href");
			System.out.println(title + " >> " + href);
			fieldHrefs.offer(href);
		}
		
		getCompanys(fieldHrefs);
	}
	
	public void getCompanys(Queue<String> urls){
		
		for(String url0: urls){
			parseCompany(url0);
		}
	}
	
	private void parseCompany(String path){
		
		String url0 = "http://www.kanzhun.com/";
		String url = url0 + path;
		
		String html = intervalHttp(url, getHeaders());
		Document doc = Jsoup.parse(html);
		
		Elements companys = doc.select(".company_result li");
		
		for(Element e: companys){
			String name = e.child(1).children().first().text();
			System.out.println(name);
		}
	}
	
	private String intervalHttp(String url, Map<String, String> headers){
		try {
			Thread.sleep(random(2000, 5000));
		} catch (InterruptedException e) {
		}
		return http.get(url, headers);
	}
	
	private static int random(int m, int n){
		return (int) (Math.random() * (n - m) + m);
	}
	
	private Map<String, String> getHeaders(){
		Map<String, String> hds = getGeneralHeaders();
		hds.put("Host", "www.kanzhun.com");
		hds.put("Referer", "http://www.kanzhun.com/plc52p1.html?ka=com-more");
		return hds;
	}
	
	public void test(){
		String url = "http://m.bosszhipin.com/job/6c680714ecfa245f0XN42tU~?sid=wxs-144556402512476427&ka=wap_boss_to_job";
		System.out.println(http.get(url));
	}
	
	public static void main(String...strings){
		KanzhunRobot robot = new KanzhunRobot();
		robot.test();
//		robot.getCityList();
//		robot.getFieldList();
//		robot.getCompanys(urls);
		
	}
}
