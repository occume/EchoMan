package com.echoman.robot.weibo.model;

import com.echoman.storage.Column;
import com.echoman.storage.Storable;
import com.echoman.storage.EqualColumn;

public class WeiboUser implements Storable{

	@Column(type="varchar", length=30)
	@EqualColumn
	private String uid;
	@Column(type="varchar", length=100)
	private String name;
	private String url;
	
	public WeiboUser(){}
	
	public WeiboUser(String uid, String name) {
		this.uid = uid;
		this.name = name;
	}
	
	@Override
	public Object[] toArray() {
		return new Object[]{uid, name};
	}
	
	@Override
	public Object[] equalValues() {
		return new Object[]{uid};
	}
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
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
		WeiboUser other = (WeiboUser) obj;
		if (uid == null) {
			if (other.uid != null)
				return false;
		} else if (!uid.equals(other.uid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WeiboUser [uid=" + uid + ", name=" + name + ", url=" + url
				+ "]";
	}
}
