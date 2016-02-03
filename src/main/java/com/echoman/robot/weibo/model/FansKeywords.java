package com.echoman.robot.weibo.model;

import com.echoman.storage.Storable;

public class FansKeywords implements Storable{

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
	@Override
	public String getUid() {
		return id;
	}
	@Override
	public Object[] toArray() {
		return null;
	}
	@Override
	public Object[] equalValues() {
		return null;
	}
}
