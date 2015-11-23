package com.echoman.robot.weixin;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.echoman.robot.AbstractHelper;
import com.echoman.robot.weixin.model.WeixinArticle;
import com.echoman.robot.weixin.model.WeixinGZH;
import com.echoman.util.CommonUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hankcs.hanlp.HanLP;

public class WeixinRobotHelper extends AbstractHelper {

	public Set<WeixinGZH> getGZHList(String keyword){
		
		Map<String, String> headers = getGeneralHeaders();
//		headers.put("Cookie", "ABTEST=7|1447664575|v1; SNUID=484F41E4D9DDFD7D7415053ED92DFB16; IPLOC=CN3100; SUID=9196983D6F1C920A0000000056499BBF; SUID=9196983D1508990A0000000056499BC0; SUV=1447664575581686; weixinIndexVisited=1; sct=1; wapsogou_qq_nickname=");
		
		String url = URLBuilder.gzhQueryURL(keyword, 1);
//		String url = "http://weixin.sogou.com/weixin?type=1&query=a&ie=utf8";
		/**
		 * Get cookie
		 */ 
//		http.get("http://weixin.sogou.com", headers);
		
		Set<WeixinGZH> gzhs = Sets.newHashSet();
		String html = http.get(url, headers);
		
		if(html.contains("302")){
			System.out.println(url);
			System.out.println(html);
		}
		
		Document doc = Jsoup.parse(html);
        
        Elements elems = null;
        int type = 1;
        String detailUrl = "";
        
        if(type == 1){
        	elems = doc.select(".wx-rb_v1");
        }
        else if(type == 2){
        	elems = doc.select("a[id=weixin_account]");
        }
        
        int size = elems.size();
        
        for(int i = 0; i < size; i++){
        	Element elem = elems.get(i);
        	Element nameElem = elem.select(".txt-box h3").first();
        	Element enameElem = elem.select(".txt-box h4").first();
        	detailUrl = elem.attr("href");
//        	String gotoUrl = elem.attr("onclick");
//        	System.out.println("detailUrl: " + detailUrl);
//        	System.out.println(gotoUrl);
        	
        	String weixinName = nameElem.text();
        	String weixinCode = enameElem.text().substring(4);
        	
        	WeixinGZH gzh = new WeixinGZH(weixinName, weixinCode, detailUrl, "");
//        	System.out.println(gzh);
        	
//        	getArticleList(gzh);
        	System.out.println(gzh);
        	gzhs.add(gzh);
        	
        }
        return gzhs;
	}
	
	public Set<String> getArticleList(WeixinGZH gzh, int page){
//		String url = "http://weixin.sogou.com/gzh?openid=oIWsFt6HGMaRoWYyRbYCb5or9GTg&ext=_L45N5QlA_U6HdbgEze4FkbEVUwEPUH4eZULYOu8AjqHRaYZVC7OweuAwnKm5fv0";
//		String url = "http://weixin.sogou.com/gzhjs?openid=oIWsFt6HGMaRoWYyRbYCb5or9GTg&ext=_L45N5QlA_U6HdbgEze4FkbEVUwEPUH4eZULYOu8AjqHRaYZVC7OweuAwnKm5fv0&gzhArtKeyWord=&page=1&t=1447504451230";
//		String url = "http://weixin.sogou.com/gzhjs?openid=oIWsFt6HGMaRoWYyRbYCb5or9GTg&ext=_L45N5QlA_U6HdbgEze4FkbEVUwEPUH4eZULYOu8AjqHRaYZVC7OweuAwnKm5fv0t=" + System.currentTimeMillis();
//		String url = "http://weixin.sogou.com/gzh?openid=oIWsFt1NwGzPhdrAEQdcfEKbwHsk&ext=rgljaGmlw7BezHRzQ4H0dtTSvs2KARj3UAxxupOjDhIdKo61WJNuEzgt0oR4wOmH";
		String url = "http://weixin.sogou.com" + gzh.getUrl() + "&page=" + page + "&t=" + System.currentTimeMillis();
//		System.out.println(gzh.getUrl());
		Set<String> urls = Sets.newHashSet();
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		headers.put("Accept-Encoding", "gzip, deflate");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		headers.put("Cache-Control", "max-age=0");
		headers.put("Connection", "keep-alive");
		headers.put("Host", "weixin.sogou.com");
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0");
//		headers.put("Referer", "http://weixin.sogou.com/gzh?openid=oIWsFt__ri8LG3IniUySdMV13M-s&ext=h3pzFjLZCZ0pPLfBVPvz6UoFiGTAMGJkPoZjAUAsSBfN8E0HfM5d28p2NtGNLQqs");
//		headers.put("Cookie", "SNUID=5C5B54F0CDC8E96E233D92B9CD20DF8F;");
//		headers.put("Cookie", "SNUID=5901FC300B0E2FAF6AE875E10BD14F7E;");
//		headers.put("Cookie", "SNUID=3E39288DB0AA9414DD61F54CB0F03BEF;");
//		headers.put("Cookie", "SNUID=F2A15D96ABA98808D2467674AC541763;");
//		headers.put("Cookie", "SNUID=1043BC70494C6DED70B2A6AE4A1730DA; SUID=590AF63A6B20900A0000000056499439; SUID=590AF63A6B20900A0000000056499439;");
//		headers.put("Cookie", "SUV=1447640551632404;");
//		headers.put("Cookie", "SUV=1447661407155335;");
		
//		http.get(url, headers);
		
		headers = getGeneralHeaders();
		headers.put("Accept", "*/*");
		headers.put("Accept-Encoding", "gzip, deflate");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		headers.put("Connection", "keep-alive");
		headers.put("Host", "weixin.sogou.com");
//		headers.put("Cookie", "SNUID=7B7D72D7EBEECF4F567BF30FEBFC4F5A");
//		headers.put("Referer", "http://weixin.sogou.com/gzh?openid=oIWsFt__ri8LG3IniUySdMV13M-s&ext=h3pzFjLZCZ0pPLfBVPvz6UoFiGTAMGJkPoZjAUAsSBfN8E0HfM5d28p2NtGNLQqs");
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0");
		
		url = url.replace("gzh", "gzhjs");
		String html = http.get(url, headers);
		
		if (!html.contains("gzh(")) {
			System.out.println("302: " + http.getLocation());
			System.out.println(html);
//			return;
		}
		else{
			
			html = html.substring(4, html.indexOf("})") + 1);
			
			try {
				JSONObject jobj = new JSONObject(html);
				JSONArray items = jobj.getJSONArray("items");
				
				int totalPage = jobj.getInt("totalPages");
				gzh.setTotalPage(totalPage);
				
				int len = items.length();
				 System.out.println(len + " articles...");
				 
				for(int i = 0; i < len; i++){
					String item = items.getString(i);
					Document doc = Jsoup.parse(item);
					Elements elems = doc.getElementsByTag("display");
					Element display = elems.first();
					
					String[] fields = {"docid", "title1", "url", "imglink", "sourcename", 
							"openid", "content", "date"};
					
					for(String filed: fields){
						Elements fieldElment = display.getElementsByTag(filed);
						Element first = fieldElment.first();
						if(first != null){
							String fieldValue = fieldElment.first().text();
	//						System.out.println(fieldValue);
	//						fillArticle(filed, fieldValue, article);
							if(filed.equals("url")){
								String articleUrl = "http://weixin.sogou.com" + fieldValue;
								String articleHtml = http.get(articleUrl, headers);
								System.out.println(articleUrl);
	//							System.out.println(articleHtml);
//								CommonUtil.wait2(1000, 2000);
//								http.get(articleUrl, headers);
								String location = http.getLocation();
								System.out.println("location: " + location);
	//							String articleHtml = http.get(location);
	//							System.out.println(articleHtml);
								urls.add(location);
								
//								getArticle(location, gzh);
	//							break;
							}
							
						}
						
					}
					
					Elements epageSize = display.getElementsByTag("pagesize");
					String pageSize = epageSize.first().text();
					Elements elastModified = display.getElementsByTag("lastmodified");
					String lastModified = elastModified.first().text();
					long lm = Long.valueOf(lastModified + "000");
					
	//				article.setPagesize(Integer.valueOf(pageSize.substring(0, pageSize.length() - 1)));
	//				article.setLastmodified(lm);
					
	//				System.out.println(article);
	//				if(isToday(lm)){
	//					article.setGzh(account.getEnName());
	//					article.setCommunity("WX");
	//					storage.writeArticle(article);
	//				}
//					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return urls;
	}
	
	private void fillArticle(String filed, Object fieldValue, WeixinArticle article){
		Method[] methods = WeixinArticle.class.getMethods();
		for(Method m: methods){
			if(m.getName().startsWith("set") && 
					m.getName().toLowerCase().contains(filed)){
				try {
					m.invoke(article, fieldValue);
				} catch (Exception e) {
					System.out.println(m.getName());
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	public WeixinArticle getArticle(String url, WeixinGZH gzh){
//		System.out.println(url);
		Map<String, String> 
		
		headers = getGeneralHeaders();
		headers.put("Host", "mp.weixin.qq.com");
		headers.put("Cache-Control", "no-cache");
		headers.put("Pragma", "no-cache");
		
		String html = http.get(url, headers);
		
		http.get("http://mp.weixin.qq.com/mp/report?action=pcclick&__biz=MzI4NzAyODE0Mg==&uin=&scene=10000001&r=0.7182390960764692", headers
				);
		
		Document doc = Jsoup.parse(html);
		
		Element titleElem = doc.getElementById("activity-name");
		String title = titleElem.text();
		
		Elements elems = doc.select("#js_content p span");
		String text = "";
		
		for(Element elem: elems){
//			elem.h
//			System.out.println(elem);
//			System.out.println(elem.tagName());
//			System.out.println(elem.html());
			if(elem.hasText()){
				text += elem.text();
			}
		}
		
//		System.out.println(text);
		
		List<String> keywordList = HanLP.extractKeyword(text, 5);
//		System.out.println(keywordList);
		
		List<String> sentenceList = HanLP.extractSummary(text, 3);
//		System.out.println(sentenceList);
		
		WeixinArticle article = new WeixinArticle();
		article.setArticleContent(text);
		article.setArticleKeywords(Joiner.on(",").join(keywordList));
		article.setArticleDesc(Joiner.on(",").join(sentenceList));
		article.setArticleName(title);
		article.setArticleUrl(url);
		article.setWeixinName(gzh.getWeixinName());
		article.setWeixinCode(gzh.getWeixinCode());
		
		System.out.println(article);
		
		return article;
	}
	
	public Map<String, String> getGeneralHeaders(){
		
		Map<String, String> hds = super.getGeneralHeaders();

		hds.put("Host", "weixin.sogou.com");
//		hds.add(new BasicHeader("Referer", "http://weixin.sogou.com/weixin?type=2&query=%E5%85%B0%E5%B7%9E&fr=sgsearch&ie=utf8&_ast=1421146752&_asf=null&w=01029901&p=40040100&dp=1&cid=null"));
//		hds.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0");
		hds.put("Cookie", "ABTEST=1|1447899525|v1; IPLOC=CN3100; SUID=9196983D6F1C920A00000000564D3185; PHPSESSID=dkrc9rs38er2hk4enodu6gq920; SUIR=1447899525; SUV=00BF25C73D989691564D3185EEA49974; SNUID=F8FFF054686D4CB7B0897BCC697BD138; SUID=9196983D1508990A00000000564D6BB7;");
		return hds;
	}

	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/weixin/";
	}
}
