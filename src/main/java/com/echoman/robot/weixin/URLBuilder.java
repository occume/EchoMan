package com.echoman.robot.weixin;

public class URLBuilder {
	
	

	public static String gzhQueryURL(String keyWord, int type){
		String url = baseQueryURL();
		url += "&query=" + keyWord + "&type=" + type;
		return url;
	}
	
	public static String baseQueryURL(){
		String baseURL = "http://weixin.sogou.com/weixin?"
				+ "ie=utf8&"
				+ "_ast=1420441472&"
				+ "_asf=null&w=01019900&"
				+ "p=40040100&dp=1&"
				+ "cid=null&"
				+ "sut=5372&"
				+ "sst0=1420441488584&"
				+ "lkt=0%2C0%2C0";
		return baseURL;
	}
}
