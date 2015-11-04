package com.echoman.robot.weixin.model;

import java.util.Date;

public class Article {
	
	private int id;
	private String gzh;
	private String docid;
	private String url;
	private String title1;
	private String imglink;
	private String sourcename;
	private String openid;
	private String content;
	private String date;
	private long lastmodified;
	private int pagesize;
	private Date postTime;
	private Date happenTime = new Date();
	private String community;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getGzh() {
		return gzh;
	}
	public void setGzh(String gzh) {
		this.gzh = gzh;
	}
	public String getDocid() {
		return docid;
	}
	public void setDocid(String docid) {
		this.docid = docid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getTitle1() {
		return title1;
	}
	public void setTitle1(String title1) {
		this.title1 = title1;
	}
	public String getImglink() {
		return imglink;
	}
	public void setImglink(String imglink) {
		this.imglink = imglink;
	}
	public String getSourcename() {
		return sourcename;
	}
	public void setSourcename(String sourcename) {
		this.sourcename = sourcename;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public long getLastmodified() {
		return lastmodified;
	}
	public void setLastmodified(long lastmodified) {
		this.lastmodified = lastmodified;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public Date getPostTime() {
		return postTime;
	}
	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}
	public Date getHappenTime() {
		return happenTime;
	}
	public void setHappenTime(Date happenTime) {
		this.happenTime = happenTime;
	}
	
	public String getCommunity() {
		return community;
	}
	public void setCommunity(String community) {
		this.community = community;
	}
	@Override
	public String toString() {
		return "Article [gzh=" + gzh + ", docid=" + docid + ", url=" + url
				+ ", title1=" + title1 + ", imglink=" + imglink
				+ ", sourcename=" + sourcename + ", openid=" + openid
				+ ", content=" + content + ", date=" + date + ", lastmodified="
				+ lastmodified + ", pagesize=" + pagesize + "]";
	}
	
}
