package com.echoman.robot.jd;

import java.util.Map;

import com.echoman.robot.AbstractHelper;

public class JDRobotHelper extends AbstractHelper{

	@Override
	public String getJSFileDirectory() {
		return "com/echoman/robot/jd/";
	}

	public void test() {
		String url = "http://item.jd.com/1397588146.html";
		String html = http.get(url);
		System.out.println(html);
	}

	public void login() {
		
		prepareCookie();
		
		String url = "https://passport.jd.com/common/loginService?nr=1&uuid=7e199a96-7528-4e1c-808e-003a53d71da6&from=media&ReturnUrl=http%3A%2F%2Fmedia.jd.com%2Findex%2Foverview&r=0.15197113544602692";
	
		Map<String, Object> 
		params = getParamsMap();
		params.put("authcode", "");
		params.put("chkRememberMe", "on");
		params.put("eid", "2518eb8a6bcb4589937d300c4de80f30243789447");
		params.put("fp", "969827afe69d53766a2a109b4550d9df");
		params.put("loginname", "toyaowu@163.com");
		params.put("loginpwd", "5580730yaowu");
		params.put("machineCpu", "");
		params.put("machineDisk", "");
		params.put("machineNet", "");
		params.put("nloginpwd", "5580730yaowu");
		params.put("uuid", "7e199a96-7528-4e1c-808e-003a53d71da6");
		params.put("xVQmLKIade", "sVCCf");
		
		Map<String, String> 
		headers = getGeneralHeaders();
		headers.put("Host", "passport.jd.com");
		headers.put("Cookie", getCookie());
		headers.put("Referer", "https://passport.jd.com/common/loginPage?from=media&ReturnUrl=http%3A%2F%2Fmedia.jd.com%2Findex%2Foverview");
		headers.put("X-Requested-With", "XMLHttpRequest");
		
		String html = http.post(url, params, headers);
		System.out.println(html);
	}
	
	private void prepareCookie(){
		String url = "http://media.jd.com/";
		Map<String, String>
		headers = getGeneralHeaders();
		headers.put("Host", "media.jd.com");
		http.get(url, headers);
	
		url = "https://passport.jd.com/common/loginPage?from=media&ReturnUrl=http%3A%2F%2Fmedia.jd.com%2Findex%2Foverview";
		headers = getGeneralHeaders();
		headers.put("Host", "passport.jd.com");
		headers.put("Referer", "http://media.jd.com/");
		http.get(url, headers);

		url = "http://media.jd.com/";
		headers = getGeneralHeaders();
		headers.put("Host", "media.jd.com");
		http.get(url, headers);
	}

	private String getCookie(){
		return "__jdu=96918450; e_etag" +
"=2518eb8a6bcb4589937d300c4de80f30243789447; __jda=95931165.96918450.1454138522.1454138522.1454138522" +
".1; __jdb=95931165.1.96918450|1.1454138522; __jdc=95931165; __jdv=95931165|direct|-|none|-; e_png=2518eb8a6bcb4589937d300c4de80f30243789447" +
"; 3AB9D23F7A4B3C9B=2518eb8a6bcb4589937d300c4de80f30243789447";
	}
}
