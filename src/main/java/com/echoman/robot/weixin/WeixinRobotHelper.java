package com.echoman.robot.weixin;

import java.util.Map;

import org.apache.http.message.BasicHeader;

import com.echoman.robot.AbstractHelper;

public class WeixinRobotHelper extends AbstractHelper {

	public void getGZHList(){
		Map<String, String> headers = getGeneralHeaders();
		
		String url = URLBuilder.gzhQueryURL("cctv", 1);
		
		String html = http.get(url, headers);
		System.out.println(html);
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
		return "";
	}

}
