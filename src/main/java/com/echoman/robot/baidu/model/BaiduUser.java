package com.echoman.robot.baidu.model;

import java.util.Set;

import com.google.common.collect.Sets;

public class BaiduUser {

	private String userName;
	private String headImg;
	private String portrait;
	private String userId;
	private Set<BaiduForum> forums;
	private Set<PostInfo> posts;
	private Set<BaiduUser> follows;
	private Set<BaiduUser> visitors;
	
	public BaiduUser(){
		forums 		= Sets.newHashSet();
		posts 		= Sets.newHashSet();
		follows 	= Sets.newHashSet();
		visitors 	= Sets.newHashSet();
	}
	
	public BaiduUser(String userName){
		this();
		this.userName = userName;
	}
	
	public BaiduUser(String userName, String headImg) {
		this(userName);
		this.headImg = headImg;
	}

	public void addForum(BaiduForum forum){
		forums.add(forum);
	}
	
	public void addPost(PostInfo post){
		posts.add(post);
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getHeadImg() {
		return headImg;
	}
	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}
	public String getPortrait() {
		return portrait;
	}

	public void setPortrait(String portrait) {
		this.portrait = portrait;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Set<BaiduForum> getForums() {
		return forums;
	}
	public void setForums(Set<BaiduForum> forums) {
		this.forums = forums;
	}

	public Set<PostInfo> getPosts() {
		return posts;
	}

	public void setPosts(Set<PostInfo> posts) {
		this.posts = posts;
	}

	public Set<BaiduUser> getFollows() {
		return follows;
	}

	public void setFollows(Set<BaiduUser> follows) {
		this.follows = follows;
	}

	public Set<BaiduUser> getVisitors() {
		return visitors;
	}

	public void setVisitors(Set<BaiduUser> visitors) {
		this.visitors = visitors;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaiduUser other = (BaiduUser) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BaiduUser [userName=" + userName + ", headImg=" + headImg
				+ ", portrait=" + portrait + ", userId=" + userId + ", forums="
				+ forums + "]";
	}
}
