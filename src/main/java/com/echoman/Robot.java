package com.echoman;

import java.util.Map;

import com.echoman.baidu.model.UserInfo;
import com.echoman.model.Response;
import com.echoman.util.LoginedHttpClient;

public interface Robot {
	
	public Response execute(Map<String, String[]> map);
	
	public UserInfo getUserInfo();
	
	public String getName();
	
	public void backgroundSign();
	
	public void backgroundProcess();
	
	public LoginedHttpClient getHttpClient();
	
	public void login();
	
	public boolean isLogin();
}
