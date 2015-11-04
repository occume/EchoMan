package com.echoman.robot.weibo.model;

import com.echoman.storage.Column;
import com.echoman.storage.Storable;
import com.echoman.storage.Unique;

public class WeiboUser implements Storable{

	@Column(type="varchar", length=30)
	@Unique
	private String uid;
	@Column(type="varchar", length=100)
	private String name;
	
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
	public Object[] uniqueValues() {
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "FollowBean [uid=" + uid + ", name=" + name + "]";
	}
	
}
