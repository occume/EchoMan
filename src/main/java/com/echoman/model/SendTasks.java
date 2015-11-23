package com.echoman.model;

public class SendTasks {

	private String 		articleId;
	private String 		platformId;
	private String 		userName;
	private String 		userPassword;
	private String		fansKeywords;
	
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public String getPlatformId() {
		return platformId;
	}
	public void setPlatformId(String platformId) {
		this.platformId = platformId;
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
		return "SendTasks [articleId=" + articleId + ", platformId="
				+ platformId + ", userName=" + userName + ", userPassword="
				+ userPassword + ", fansKeywords=" + fansKeywords + "]";
	}
}
