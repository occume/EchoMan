package com.echoman.robot.qq.model;

import java.util.Date;

import com.echoman.storage.Storable;

public class QqGroupMsg implements Storable{

	private String 		groupId;
	private long 		qq;
	private String 		nickName;
	private String		content;
	private Date		sendTime;
	
	public QqGroupMsg(){}
	
	public QqGroupMsg(String groupId, long qq, String nickName, String content,
			Date sendTime) {
		this.groupId = groupId;
		this.qq = qq;
		this.nickName = nickName;
		this.content = content;
		this.sendTime = sendTime;
	}
	
	@Override
	public Object[] toArray() {
		return new Object[]{groupId, qq, nickName, content, sendTime};
	}

	@Override
	public Object[] equalValues() {
		return null;
	}
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public long getQq() {
		return qq;
	}
	public void setQq(long qq) {
		this.qq = qq;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	@Override
	public String toString() {
		return "GroupMsg [groupId=" + groupId + ", qq=" + qq + ", nickName="
				+ nickName + ", content=" + content + "]";
	}

	@Override
	public String getUid() {
		return null;
	}
}
