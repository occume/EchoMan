package com.echoman.model;

public class SendTasks {

	private String		id;
	private String 		articleId;
	private int 		fromPlatformId;
	private int 		toPlatformId;
	private String 		userName;
	private String 		userPassword;
	private String		fansKeywords;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	
	public int getFromPlatformId() {
		return fromPlatformId;
	}
	public void setFromPlatformId(int fromPlatformId) {
		this.fromPlatformId = fromPlatformId;
	}
	public int getToPlatformId() {
		return toPlatformId;
	}
	public void setToPlatformId(int toPlatformId) {
		this.toPlatformId = toPlatformId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getFansKeywords() {
		return fansKeywords;
	}
	public void setFansKeywords(String fansKeywords) {
		this.fansKeywords = fansKeywords;
	}
	@Override
	public String toString() {
		return "SendTasks [articleId=" + articleId + ", fromPlatformId="
				+ fromPlatformId + ", userName=" + userName + ", userPassword="
				+ userPassword + ", fansKeywords=" + fansKeywords + "]";
	}
}
