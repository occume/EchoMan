package com.echoman.robot.qq.model;

import com.echoman.storage.Storable;

public class QqGroupMember implements Storable{
	
	private long 		qqNum;
	private String 		nickName;
	private long		gid;
	
	public QqGroupMember(){}
	
	public QqGroupMember(long qqNum, String nickName, long gid) {
		this.qqNum = qqNum;
		this.nickName = nickName;
		this.gid = gid;
	}

	@Override
	public String getUid() {
		return String.valueOf(qqNum);
	}

	@Override
	public Object[] toArray() {
		return new Object[]{qqNum, nickName, gid};
	}

	@Override
	public Object[] equalValues() {
		return null;
	}

	public long getQqNum() {
		return qqNum;
	}

	public void setQqNum(long qqNum) {
		this.qqNum = qqNum;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public long getGid() {
		return gid;
	}

	public void setGid(long gid) {
		this.gid = gid;
	}

	@Override
	public String toString() {
		return "QqGroupMember [qqNum=" + qqNum + ", nickName=" + nickName
				+ ", gid=" + gid + "]";
	}
}
