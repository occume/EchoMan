package com.echoman.robot.weibo.model;

public class FansKeywords {

	private String		id;
	private String 	keywords;
	private String	delFlag;
	
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "FansKeywords [keywords=" + keywords + ", delFlag=" + delFlag
				+ "]";
	}
}
