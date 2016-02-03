package com.echoman.model;

import java.util.Date;

import com.echoman.storage.Storable;

public class SendTasksLog implements Storable{

	private String		tasksId;
	private String 		articleId;
	private String 		userId;
	private String 		userName;
	private Date		createDate = new Date();
	
	public SendTasksLog(){}
	
	public SendTasksLog(String tasksId, String articleId, String userId,
			String userName) {
		this.tasksId = tasksId;
		this.articleId = articleId;
		this.userId = userId;
		this.userName = userName;
	}

	@Override
	public Object[] toArray() {
		return new Object[]{tasksId, articleId, userId, userName, createDate};
	}

	@Override
	public Object[] equalValues() {
		return null;
	}
	
	
	public String getTasksId() {
		return tasksId;
	}
	public void setTasksId(String tasksId) {
		this.tasksId = tasksId;
	}
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	@Override
	public String toString() {
		return "SendTasksLog [tasksId=" + tasksId + ", articleId=" + articleId
				+ ", userId=" + userId + ", userName=" + userName + "]";
	}

	@Override
	public String getUid() {
		return tasksId;
	}

}
