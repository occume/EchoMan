package com.echoman.d3f;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.echoman.util.LoginedHttpClient;

public class Tuling {
	
	public static String[] getAnswer(String question){
		
		String errorCode = "", answer = "";
		
		String key = "1ebb9c07b832c7cceee94b01e8a6b7b6";
		String url = "http://www.tuling123.com/openapi/api?";
		
		String getUrl = "";
		try {
			getUrl = url + "key=" + key + "&info=" + URLEncoder.encode(question, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String json = LoginedHttpClient.newHttp().get(getUrl);
		
		Pattern pat = Pattern.compile("code.+:(\\d+).+text\":\"(.+)\"");
		Matcher mat = pat.matcher(json);
		
		if(mat.find()){
			errorCode = mat.group(1);
			answer = mat.group(2);
		}
		
		return new String[]{errorCode, answer};
	}
}
