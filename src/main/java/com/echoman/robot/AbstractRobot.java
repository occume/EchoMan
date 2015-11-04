package com.echoman.robot;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.echoman.model.Response;
import com.echoman.model.RobotBean;
import com.echoman.util.LoginedHttpClient;
import com.google.common.collect.Maps;

public abstract class AbstractRobot implements Robot {
	
	protected ScriptEngineManager manager;
	protected ScriptEngine engine;
	protected Bindings bds;
	protected LoginedHttpClient http;
	
	protected RobotBean robotBean;
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
		this.robotBean = bean;
	}
	
	public Robot setAccount(String account){
		this.account = account;
		return this;
	}
	
	public Robot setPassword(String password){
		this.password = password;
		return this;
	}
	
	public String getAccount(){
		return this.account;
	}
	
	public String getPassword(){
		return this.password;
	}

	@Override
	public Response execute(Map<String, String[]> map) {
		return null;
	}

	@Override
	public RobotBean getRobotBean() {
		return this.robotBean;
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
	
	public void runCallback0(JsInvoker invoker, String jsFile, String fun) throws Exception{
		
		String jsFilePath = getJSFileDirectory() + jsFile + ".js";
		URL url = getClass().getClassLoader().getResource(jsFilePath);
		
		FileReader reader = new FileReader(new File(url.getPath()));
		engine.eval(reader);
		engine.eval(fun);
		
		invoker.invoke();
	}
	
	public void runCallback(JsInvoker invoker, String jsFile, String fun){
		try {
			runCallback0(invoker, jsFile, fun);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public abstract String getJSFileDirectory();
	
	public Map<String, Object> getParamsMap(){
		return Maps.newHashMap();
	}
	
	public Map<String, String> getGeneralHeaders(){
		
		Map<String, String> hds = new HashMap<>();

		hds.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		hds.put("Accept-Encoding", "gzip, deflate");
		hds.put("Accept-Language", "zh-CN,zh:q=0.8,en-US:q=0.5,en:q=0.3");
		hds.put("Connection", "keep-alive");
		hds.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/40.0");
		
		return hds;
	}
	
	protected static interface JsInvoker{
		public void invoke();
	}
}
