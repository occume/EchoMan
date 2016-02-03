package com.echoman.robot.weidian;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.robot.AbstractHelper;
import com.google.common.collect.Maps;

public class WeidianHelper extends AbstractHelper{
	
	private final static Logger LOG = LoggerFactory.getLogger(WeidianHelper.class);

	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/weidian/";
	}

	public void login() {
		
		String url = "http://cps.weidian.com/login.do";
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "cps.weidian.com");
		headers.put("Referer", "http://cps.weidian.com/");
		
		Map<String, Object> 
		params = Maps.newHashMap();
		params.put("countryCode", 86);
		params.put("phoneNum", "13910227615");
		params.put("password", "5580730yw");
		
		http.post(url, params, headers, true);
		String location = http.getLocation();
		System.out.println(location);
		
		http.get(location, headers);
		location = http.getLocation();
		System.out.println(location);
		
		String html = http.get(location, headers);
//		System.out.println(html);
	}

	public void getListByKeyWord() {
		
		String url = "http://cps.weidian.com/listCpsItem.do?keyword=%E8%BF%9E%E8%A1%A3%E8%A3%99";
		
		Map<String, String>
		headers = getGeneralHeaders();
		headers.put("Host", "cps.weidian.com");
		headers.put("Referer", "http://cps.weidian.com/listCpsItem.do");
		
		String html = http.get(url, headers);
		System.out.println(html);
	}
}
