package com.echoman.robot.weixin.model;

import java.io.Serializable;

public class PublicAccount implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8369150722268615707L;
	
	private int id;
	private String cnName;
	private String enName;
	private String intro;
	private String img;
	private String qrcode;
	private String url;
	private int		type;
	
	public PublicAccount(){}
	
	public PublicAccount(String cnName, String enName, String intro,
			String img, String qrcode, String url) {
		this.cnName = cnName;
		this.enName = enName;
		this.intro = intro;
		this.img = img;
		this.qrcode = qrcode;
		this.url = url;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCnName() {
		return cnName;
	}
	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getQrcode() {
		return qrcode;
	}
	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "PublicAccount [cnName=" + cnName + ", enName=" + enName
				+ ", intro=" + intro + ", img=" + img + ", qrcode=" + qrcode
				+ ", url=" + url + "]";
	}
}
