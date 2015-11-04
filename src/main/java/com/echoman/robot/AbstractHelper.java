package com.echoman.robot;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.echoman.util.LoginedHttpClient;
import com.google.common.collect.Maps;

public abstract class AbstractHelper{
	
	private final static Logger LOG = LoggerFactory.getLogger(AbstractHelper.class);
	
	protected ScriptEngineManager manager;
	protected ScriptEngine engine;
	protected Bindings bds;
	protected LoginedHttpClient http;
	
	public AbstractHelper(){
		manager = new ScriptEngineManager();
		engine = manager.getEngineByName("js");
		bds = engine.createBindings();
		engine.setBindings(bds, 100);
		http = LoginedHttpClient.newHttp();
	}

	public LoginedHttpClient getHttpClient(){
		return http;
	}
	
	public boolean isLogin() {
		return http.isLogined();
	}
	
	private void runCallback0(JsInvoker invoker, String jsFileName, String funName) throws Exception{
		
		if(jsFileName != null){
			String jsFilePath = getJSFileDirectory() + jsFileName + ".js";
			URL url = getClass().getClassLoader().getResource(jsFilePath);
		
			FileReader reader = new FileReader(new File(url.getPath()));
			engine.eval(reader);
		}
		
		if(funName != null){
			engine.eval(funName);
		}
		
		if(invoker != null){
			invoker.invoke();
		}
	}
	
	public void runCallback(JsInvoker invoker, String jsFile, String fun){
		try {
			runCallback0(invoker, jsFile, fun);
		} catch (Exception e) {
			LOG.error("Run js error, ", e);
		}
	}
	
	public void loadAndRunFunction(JsInvoker invoker, String jsFileName, String funName){
		runCallback(invoker, jsFileName, funName);
	}
	
	public void loadAndRunFunction(String jsFileName, String funName){
		loadAndRunFunction(null, jsFileName, funName);
	}
	
	public void loadAndRunFunction(String jsFileName){
		loadAndRunFunction(null, jsFileName, null);
	}
	
	public void runFunction(String funName){
		runCallback(null, null, funName);
	}
	
	public void runStatement(String statement){
		runFunction(statement);
	}
	
	public String getValueOfVar(String name){
		return bds.get(name).toString();
	}
	
	public void setValueOfVar(String name, String value){
		bds.put(name, value);
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
