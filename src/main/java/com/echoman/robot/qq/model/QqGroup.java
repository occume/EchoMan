package com.echoman.robot.qq.model;

import java.util.Date;

import com.echoman.storage.EqualColumn;
import com.echoman.storage.Storable;

public class QqGroup implements Storable{
	
	@EqualColumn
	private long 		gid;
	private long 		klass;
	private String 		groupName;
	private String 		fingerMemo;
	private String 		groupMemo;
	private Date		createTime;
	
	public QqGroup(){}

	public QqGroup(int klass, long gid, String groupName, String fingerMemo,
			String groupMemo, Date createTime) {
		this.klass = klass;
		this.gid = gid;
		this.groupName = groupName;
		this.fingerMemo = fingerMemo;
		this.groupMemo = groupMemo;
		this.createTime = createTime;
	}

	@Override
	public String getUid() {
		return String.valueOf(gid);
	}

	@Override
	public Object[] toArray() {
		return new Object[]{gid, klass, groupName, fingerMemo, groupName, createTime};
	}

	@Override
	public Object[] equalValues() {
		return new Object[]{gid};
	}

	public long getKlass() {
		return klass;
	}

	public void setKlass(long klass) {
		this.klass = klass;
	}

	public long getGid() {
		return gid;
	}

	public void setId(long gid) {
		this.gid = gid;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getFingerMemo() {
		return fingerMemo;
	}

	public void setFingerMemo(String fingerMemo) {
		this.fingerMemo = fingerMemo;
	}

	public String getGroupMemo() {
		return groupMemo;
	}

	public void setGroupMemo(String groupMemo) {
		this.groupMemo = groupMemo;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "Group [klass=" + klass + ", gid=" + gid + ", groupName="
				+ groupName + ", fingerMemo=" + fingerMemo + ", groupMemo="
				+ groupMemo + ", createTime=" + createTime + "]";
	}

}
