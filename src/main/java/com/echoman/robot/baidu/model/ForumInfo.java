package com.echoman.robot.baidu.model;

public class ForumInfo {

	private String fid;
	private String name;
	private String level;
	
	public ForumInfo(String fid, String name, String level) {
		this.fid = fid;
		this.name = name;
		this.level = level;
	}
	
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "ForumInfo [fid=" + fid + ", name=" + name + ", level=" + level
				+ "]";
	}
}
