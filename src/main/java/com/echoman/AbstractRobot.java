package com.echoman;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.echoman.baidu.model.UserInfo;
import com.echoman.model.Response;
import com.echoman.model.RobotBean;
import com.echoman.util.LoginedHttpClient;

public abstract class AbstractRobot implements Robot {
	
	protected ScriptEngineManager manager;
	protected ScriptEngine engine;
	protected Bindings bds;
	protected LoginedHttpClient http;
	
	protected String account;
	protected String password;
	
	public AbstractRobot(){
		manager = new ScriptEngineManager();
		engine = manager.getEngineByName("js");
		bds = engine.createBindings();
		engine.setBindings(bds, 100);
		http = LoginedHttpClient.newHttp();
	}
	
	public AbstractRobot(RobotBean bean){
		this();
		this.account = bean.getAccount();
		this.password = bean.getPassword();
	}

	@Override
	public Response execute(Map<String, String[]> map) {
		return null;
	}

	@Override
	public UserInfo getUserInfo() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void backgroundSign() {
		
	}

	@Override
	public void backgroundProcess() {
		
	}

	public LoginedHttpClient getHttpClient(){
		return http;
	}
	
	@Override
	public boolean isLogin() {
		return http.isLogined();
	}
}
