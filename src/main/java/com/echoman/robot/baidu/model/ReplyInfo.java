package com.echoman.robot.baidu.model;

public class ReplyInfo {

	private String url;
	private String time;
	private String content;
	
	public ReplyInfo(){}
	
	public ReplyInfo(String url, String time, String content) {
		this.url = url;
		this.time = time;
		this.content = content;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "ReplyInfo [url=" + url + ", time=" + time + ", content="
				+ content + "]";
	}
}
