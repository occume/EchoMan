package com.echoman.robot.weibo.model;

import java.util.Date;

import com.echoman.storage.Column;
import com.echoman.storage.Storable;
import com.echoman.storage.EqualColumn;

public class WeiboUser implements Storable{

	@Column(type="varchar", length=30)
	@EqualColumn
	private String userId;
	@Column(type="varchar", length=100)
	private String userName;
	private String url;
	private String baseAddress;
	private String gender;
	private String sex;
	private String emotion;
	private Date 	birthday;
	private String	blood;
	private String	constellation;
	private String	intro;
	private String	blog;
	private String	msn;
	private String	qq;
	private String	school;
	private String	company;
	private String 	companyAddr;
	private String	companyJob;
	private String	tag;
	private int		attentions;
	private int		fans;
	private int		sendCount;
	private String	grabTag;
	private String 	classTag;
	private Date	fetchedTime = new Date();
	
	public WeiboUser(){}
	
	public WeiboUser(String userId, String userName) {
		this.userId = userId;
		this.userName = userName;
	}
	
	@Override
	public Object[] toArray() {
		return new Object[]{userId, userName,url,baseAddress,gender,sex,emotion,birthday,blood,constellation,intro,blog,msn,qq,school,company,companyAddr,
				companyJob,tag,attentions,fans,sendCount,grabTag,classTag,fetchedTime};
	}
	
	@Override
	public Object[] equalValues() {
		return new Object[]{userId};
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBaseAddress() {
		return baseAddress;
	}

	public void setBaseAddress(String baseAddress) {
		this.baseAddress = baseAddress;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getEmotion() {
		return emotion;
	}

	public void setEmotion(String emotion) {
		this.emotion = emotion;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getBlood() {
		return blood;
	}

	public void setBlood(String blood) {
		this.blood = blood;
	}

	public String getConstellation() {
		return constellation;
	}

	public void setConstellation(String constellation) {
		this.constellation = constellation;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getBlog() {
		return blog;
	}

	public void setBlog(String blog) {
		this.blog = blog;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCompanyAddr() {
		return companyAddr;
	}

	public void setCompanyAddr(String companyAddr) {
		this.companyAddr = companyAddr;
	}

	public String getCompanyJob() {
		return companyJob;
	}

	public void setCompanyJob(String companyJob) {
		this.companyJob = companyJob;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getAttentions() {
		return attentions;
	}

	public void setAttentions(int attentions) {
		this.attentions = attentions;
	}

	public int getFans() {
		return fans;
	}

	public void setFans(int fans) {
		this.fans = fans;
	}

	public int getSendCount() {
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

	public String getGrabTag() {
		return grabTag;
	}

	public void setGrabTag(String grabTag) {
		this.grabTag = grabTag;
	}

	public String getClassTag() {
		return classTag;
	}

	public void setClassTag(String classTag) {
		this.classTag = classTag;
	}

	public Date getFetchedTime() {
		return fetchedTime;
	}

	public void setFetchedTime(Date fetchedTime) {
		this.fetchedTime = fetchedTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WeiboUser [userId=" + userId + ", baseAddress=" + baseAddress
				+ ", gender=" + gender + ", school=" + school + ", company="
				+ company + ", attentions=" + attentions + ", grabTag="
				+ grabTag + "]";
	}
	
}
