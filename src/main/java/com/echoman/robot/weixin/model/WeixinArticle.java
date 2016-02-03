package com.echoman.robot.weixin.model;

import java.util.Date;

import com.echoman.storage.EqualColumn;
import com.echoman.storage.NonColumn;
import com.echoman.storage.Storable;

public class WeixinArticle implements Storable{

	@NonColumn
	private int id;
	private String weixinName;
	private String weixinCode;
	@EqualColumn
	private String articleName;
	private String articleUrl;
	private String articleContent;
	private String articleDesc;
	private String articleKeywords;
	private Date updateDate = new Date();
	private Date createDate = new Date();
	
	@Override
	public Object[] toArray() {
		return new Object[]{weixinName, weixinCode, articleName, articleUrl, articleContent,
				articleDesc, articleKeywords, updateDate, createDate};
	}
	@Override
	public Object[] equalValues() {
		return new Object[]{articleName};
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getWeixinName() {
		return weixinName;
	}
	public void setWeixinName(String weixinName) {
		this.weixinName = weixinName;
	}
	public String getWeixinCode() {
		return weixinCode;
	}
	public void setWeixinCode(String weixinCode) {
		this.weixinCode = weixinCode;
	}
	public String getArticleName() {
		return articleName;
	}
	public void setArticleName(String articleName) {
		this.articleName = articleName;
	}
	public String getArticleUrl() {
		return articleUrl;
	}
	public void setArticleUrl(String articleUrl) {
		this.articleUrl = articleUrl;
	}
	public String getArticleContent() {
		return articleContent;
	}
	public void setArticleContent(String articleContent) {
		this.articleContent = articleContent;
	}
	public String getArticleDesc() {
		return articleDesc;
	}
	public void setArticleDesc(String articleDesc) {
		this.articleDesc = articleDesc;
	}
	public String getArticleKeywords() {
		return articleKeywords;
	}
	public void setArticleKeywords(String articleKeywords) {
		this.articleKeywords = articleKeywords;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@Override
	public String toString() {
		return "WeixinArticle [weixinName=" + weixinName + ", articleName="
				+ articleName + ", articleDesc=" + articleDesc
				+ ", articleKeywords=" + articleKeywords + "]";
	}
	@Override
	public String getUid() {
		return id + "";
	}
}
