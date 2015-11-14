package com.echoman.robot.weixin;

import java.util.Map;

import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.echoman.robot.AbstractHelper;

public class WeixinRobotHelper extends AbstractHelper {

	public void getGZHList(){
		Map<String, String> headers = getGeneralHeaders();
		
		String url = URLBuilder.gzhQueryURL("cctv", 1);
		
		String html = http.get(url, headers);
		System.out.println(html);
	}
	
	public void getArticleList(){
//		String url = "http://weixin.sogou.com/gzh?openid=oIWsFt6HGMaRoWYyRbYCb5or9GTg&ext=_L45N5QlA_U6HdbgEze4FkbEVUwEPUH4eZULYOu8AjqHRaYZVC7OweuAwnKm5fv0";
//		String url = "http://weixin.sogou.com/gzhjs?cb=sogou.weixin.gzhcb&openid=oIWsFt6HGMaRoWYyRbYCb5or9GTg&ext=_L45N5QlA_U6HdbgEze4FkbEVUwEPUH4eZULYOu8AjqHRaYZVC7OweuAwnKm5fv0&gzhArtKeyWord=&page=1&t=1447504451230";
		String url = "http://weixin.sogou.com/gzhjs?openid=oIWsFt6HGMaRoWYyRbYCb5or9GTg&ext=_L45N5QlA_U6HdbgEze4FkbEVUwEPUH4eZULYOu8AjqHRaYZVC7OweuAwnKm5fv0";
		Map<String, String> headers = getGeneralHeaders();
		
		String html = http.get(url, headers);
//		System.out.println(html);
		
		if(html.contains("gzh(")){
			html = html.substring(4, html.indexOf("})") + 1);
		}
		System.out.println(html);
		try {
			JSONObject jobj = new JSONObject(html);
			JSONArray items = jobj.getJSONArray("items");
			
			int len = items.length();
			for(int i = 0; i < len; i++){
				String item = items.getString(i);
				Document doc = Jsoup.parse(item);
				Elements elems = doc.getElementsByTag("display");
				Element display = elems.first();
				
				String[] fields = {"docid", "title1", "url", "imglink", "sourcename", 
						"openid", "content", "date"};
				
//				Article article = new Article();
				
				for(String filed: fields){
					Elements fieldElment = display.getElementsByTag(filed);
					Element first = fieldElment.first();
					if(first != null){
						String fieldValue = fieldElment.first().text();
						System.out.println(fieldValue);
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
			}
		} catch (Exception e) {
//			currStep = Step.BREAK;
			e.printStackTrace();
			return;
		}
	}
	
	public Map<String, String> getGeneralHeaders(){
		
		Map<String, String> hds = super.getGeneralHeaders();

		hds.put("Host", "weixin.sogou.com");
//		hds.add(new BasicHeader("Referer", "http://weixin.sogou.com/weixin?type=2&query=%E5%85%B0%E5%B7%9E&fr=sgsearch&ie=utf8&_ast=1421146752&_asf=null&w=01029901&p=40040100&dp=1&cid=null"));
		hds.put("Content-Type", "application/x-www-form-urlencoded");
		
		return hds;
	}

	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/weixin/";
	}
}
