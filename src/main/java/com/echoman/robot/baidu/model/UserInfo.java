package com.echoman.robot.baidu.model;

import java.util.HashSet;
import java.util.Set;

public class UserInfo {

	private String userName;
	private String headImg;
	private Set<ForumInfo> forums;
	private Set<PostInfo> posts;
	
	public UserInfo(){}
	
	public UserInfo(String userName, String headImg) {
		this.userName = userName;
		this.headImg = headImg;
		forums = new HashSet<>();
		posts = new HashSet<>();
	}

	public void addForum(ForumInfo forum){
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
	public Set<ForumInfo> getForums() {
		return forums;
	}
	public void setForums(Set<ForumInfo> forums) {
		this.forums = forums;
	}

	public Set<PostInfo> getPosts() {
		return posts;
	}

	public void setPosts(Set<PostInfo> posts) {
		this.posts = posts;
	}

	@Override
	public String toString() {
		return "UserInfo [userName=" + userName + ", headImg=" + headImg
				+ ", forums=" + forums + "]";
	}
}
