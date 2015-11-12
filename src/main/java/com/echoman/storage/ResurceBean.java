package com.echoman.storage;

public class ResurceBean {
	
	public static ResurceBean baidu(){
		return new ResurceBean("baidu");
	}

	private String source;
	private String userName;
	private String userId;
	private String url;
	
	public ResurceBean(){}
	
	public ResurceBean(String source){
		this.source = source;
	}
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		return "ResurceBean [source=" + source + ", userName=" + userName
				+ ", userId=" + userId + ", url=" + url + "]";
	}
}
