package com.echoman.robot;

import java.util.Map;

import com.echoman.model.Response;
import com.echoman.model.RobotBean;
import com.echoman.util.LoginedHttpClient;

public interface Robot {
	
	public Response execute(Map<String, String[]> map);
	
	public RobotBean getRobotBean();
	
	public String getName();
	
	public void backgroundSign();
	
	public void backgroundProcess();
	
	public LoginedHttpClient getHttpClient();
	
	public Robot login();
	
	public boolean isLogin();
	
	public Map<String, String> getGeneralHeaders();
	
	public Robot setAccount(String account);
	
	public Robot setPassword(String password);
	
	public void sign();
}
