package com.echoman.robot.jd.model;

import com.echoman.storage.Storable;

public class TopTaobao implements Storable{
	
	private int		id;
	private String 	keyword;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	@Override
	public String toString() {
		return "TopTaobao [id=" + id + ", keywords=" + keyword + "]";
	}
	@Override
	public String getUid() {
		return id + "";
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
