package com.echoman.robot.weixin.model;

import java.io.Serializable;

import com.echoman.storage.NonColumn;

public class WeixinGZH implements Serializable{
	
	private String id;
	private String weixinName;
	private String weixinCode;
	@NonColumn
	private String openId;
	@NonColumn
	private String url;
	@NonColumn
	private int totalPage;
	
	public WeixinGZH(){}

	public WeixinGZH(String weixinName, String weixinCode, String openId) {
		this.weixinName = weixinName;
		this.weixinCode = weixinCode;
		this.openId = openId;
	}

	public WeixinGZH(String weixinName, String weixinCode, String url, String openId) {
		this.weixinName = weixinName;
		this.weixinCode = weixinCode;
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	@Override
	public String toString() {
		return "WeixinGZH [wexinName=" + weixinName + ", weixinCode="
				+ weixinCode + ", openId=" + openId + ", url=" + url + "]";
	}
	
}
