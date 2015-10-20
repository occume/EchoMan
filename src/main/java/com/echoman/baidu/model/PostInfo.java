package com.echoman.baidu.model;

public class PostInfo {

	private String id;
	private String fid;
	private String forumName;
	private String tbs;
	private String tid;
	private String title;
	private String totalPage;
	private String replyNum;
	private int    currFloor;
	private int	   currPage;
	
	public PostInfo(){}
	
	public PostInfo(String id, String title) {
		this.id = id;
		this.title = title;
	}

	public PostInfo(String id, String tid, String title) {
		this.id = id;
		this.tid = tid;
		this.title = title;
	}

	public PostInfo(String fid, String forumName, String tbs, String tid,
			String title, String totalPage, String replyNum) {
		this.fid = fid;
		this.forumName = forumName;
		this.tbs = tbs;
		this.tid = tid;
		this.title = title;
		this.totalPage = totalPage;
		this.replyNum = replyNum;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getForumName() {
		return forumName;
	}

	public void setForumName(String forumName) {
		this.forumName = forumName;
	}

	public String getTbs() {
		return tbs;
	}

	public void setTbs(String tbs) {
		this.tbs = tbs;
	}

	public String getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(String totalPage) {
		this.totalPage = totalPage;
	}

	public String getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(String replyNum) {
		this.replyNum = replyNum;
	}

	public int getCurrFloor() {
		return currFloor;
	}

	public void setCurrFloor(int currFloor) {
		this.currFloor = currFloor;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	@Override
	public String toString() {
		return "PostInfo [fid=" + fid + ", forumName=" + forumName + ", tbs="
				+ tbs + ", tid=" + tid + ", title=" + title + "]";
	}

}
