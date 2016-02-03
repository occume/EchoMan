package com.echoman.robot.jd.model;

import java.util.Date;

import com.echoman.storage.EqualColumn;
import com.echoman.storage.NonColumn;
import com.echoman.storage.Storable;

public class RecommendProduct implements Storable{

	@EqualColumn
	private String productName;
	private String price;
	private String pcRatio;
	@NonColumn
	private String mbRatio;
	private String pcCommission;
	@NonColumn
	private String mbCommission;
	private String orders;
	private String totalPrice;
	@NonColumn
	private Date startTime;
	@NonColumn
	private Date endTime;
	private String url;
	private String recommendUrl;
	private String itemType1;
	private String itemType2;
	private String itemType3;
	private String itemType4;
	private String imageUrl;
	
	@NonColumn
	private String platformType;
	private Date grabDate;
	private String grabKeyword;
	private String shopName;
	private String timeRange;
	
	@Override
	public String getUid() {
		return null;
	}
	@Override
	public Object[] toArray() {
		return new Object[]{productName, price, pcRatio, pcCommission, 
				orders, totalPrice, url, recommendUrl, itemType1, itemType2,
				itemType3,itemType4, imageUrl, grabDate, grabKeyword, shopName, timeRange};
	}
	@Override
	public Object[] equalValues() {
		return new Object[]{productName};
	}
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getPcRatio() {
		return pcRatio;
	}
	public void setPcRatio(String pcRatio) {
		this.pcRatio = pcRatio;
	}
	public String getMbRatio() {
		return mbRatio;
	}
	public void setMbRatio(String mbRatio) {
		this.mbRatio = mbRatio;
	}
	public String getPcCommission() {
		return pcCommission;
	}
	public void setPcCommission(String pcCommission) {
		this.pcCommission = pcCommission;
	}
	public String getMbCommission() {
		return mbCommission;
	}
	public void setMbCommission(String mbCommission) {
		this.mbCommission = mbCommission;
	}
	public String getOrders() {
		return orders;
	}
	public void setOrders(String orders) {
		this.orders = orders;
	}
	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getRecommendUrl() {
		return recommendUrl;
	}
	public void setRecommendUrl(String recommendUrl) {
		this.recommendUrl = recommendUrl;
	}
	public String getPlatformType() {
		return platformType;
	}
	public void setPlatformType(String platformType) {
		this.platformType = platformType;
	}
	public Date getGrabDate() {
		return grabDate;
	}
	public void setGrabDate(Date grabDate) {
		this.grabDate = grabDate;
	}
	public String getGrabKeyword() {
		return grabKeyword;
	}
	public void setGrabKeyword(String grabKeyword) {
		this.grabKeyword = grabKeyword;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	
	public String getTimeRange() {
		return timeRange;
	}
	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getItemType1() {
		return itemType1;
	}
	public void setItemType1(String itemType1) {
		this.itemType1 = itemType1;
	}
	public String getItemType2() {
		return itemType2;
	}
	public void setItemType2(String itemType2) {
		this.itemType2 = itemType2;
	}
	public String getItemType3() {
		return itemType3;
	}
	public void setItemType3(String itemType3) {
		this.itemType3 = itemType3;
	}
	public String getItemType4() {
		return itemType4;
	}
	public void setItemType4(String itemType4) {
		this.itemType4 = itemType4;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	@Override
	public String toString() {
		return "RecommendProduct [productName=" + productName + ", url=" + url
				+ ", recommendUrl=" + recommendUrl + ", itemType1=" + itemType1
				+ ", itemType2=" + itemType2 + ", shopName=" + shopName + "]";
	}
	
}
