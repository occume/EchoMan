package com.echoman.robot.baidu.model;

import com.echoman.storage.NonColumn;
import com.echoman.storage.Storable;
import com.echoman.storage.EqualColumn;

public class BaiduForum implements Storable{

	@EqualColumn
	private String fid;
	private String name;
	@NonColumn
	private String level;
	private String slogan;
	
	private int	memberNum;
	private int postNum;
	
	public BaiduForum(){}
	
	public BaiduForum(String name) {
		this.name = name;
	}
	
	public BaiduForum(String fid, String name, String level) {
		this.fid = fid;
		this.name = name;
		this.level = level;
	}
	
	public Object[] toArray(){
		return new Object[]{fid, name, slogan, memberNum, postNum};
	}

	@Override
	public Object[] equalValues() {
		return new Object[]{fid};
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

	public int getMemberNum() {
		return memberNum;
	}

	public void setMemberNum(int memberNum) {
		this.memberNum = memberNum;
	}

	public int getPostNum() {
		return postNum;
	}

	public void setPostNum(int postNum) {
		this.postNum = postNum;
	}

	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	@Override
	public String toString() {
		return "BaiduForum [fid=" + fid + ", name=" + name + ", slogan="
				+ slogan + ", memberNum=" + memberNum + ", postNum=" + postNum
				+ "]";
	}

	@Override
	public String getUid() {
		return fid;
	}
}
