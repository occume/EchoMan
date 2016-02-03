package com.echoman.robot.qq.model;

import java.util.List;

public class GroupBox {
	
	public static final GroupBox EMPTY = new GroupBox();

	private QqGroup group;
	private List<QqGroupMember> members;
	
	public GroupBox(){}
	
	public GroupBox(QqGroup group, List<QqGroupMember> members) {
		this.group = group;
		this.members = members;
	}
	
	public GroupBox(List<QqGroupMember> members) {
		this.members = members;
	}
	
	public QqGroup getGroup() {
		return group;
	}
	public void setGroup(QqGroup group) {
		this.group = group;
	}
	public List<QqGroupMember> getMembers() {
		return members;
	}
	public void setMembers(List<QqGroupMember> members) {
		this.members = members;
	}
	
}
