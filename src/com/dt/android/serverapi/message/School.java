package com.dt.android.serverapi.message;

import java.io.Serializable;

public class School  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8616948912998158273L;
	private Integer school_id;//":"11",
	private String name;//":"\u987a\u5cf0\u9a7e\u6821",
	private String distance;//":null,
	private String fphone;//":"",
	private String mphone;//":"",
	private String address;//":"",
	private String price;//":"",
	private String picture;//":"",
	private Float latitude;//":null,
	private Float longtitude;//":null,
	private String description;//":"\u897f\u5b89\u987a\u5cf0\u9a7e\u6821\u5386\u7ecf10\u51e0\u5e74\u6ca7\u6851\uff0c\u6709\u8bad\u7ec3\u573a\u57303\u5904\u5171150\u4ea9\uff0c\u57f9\u8bad\u8f66\u8f86100\u591a\u8f86\uff0c\u73b0\u6709\u6bd4\u4e9a\u8fea\u65b0\u8f66\u3002\u51e0\u5341\u5e74\u519b\u8f6c\u4e13\u4e1a\u6559\u5458\u8ba9\u4f60\u5b66\u5230\u771f\u672c\u4e8b\uff0c\u73b0\u62a5\u540d\u968f\u5230\u968f...",
	private Integer areaid;//":"2931"
	
	private String content;
	private String bmxz;
	private String bclx;
	private String zizhi_status;
	private String changdi_status;
	/**
	 * @return the school_id
	 */
	public Integer getSchool_id() {
		return school_id;
	}
	/**
	 * @param school_id the school_id to set
	 */
	public void setSchool_id(Integer school_id) {
		this.school_id = school_id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the distance
	 */
	public String getDistance() {
		return distance;
	}
	/**
	 * @param distance the distance to set
	 */
	public void setDistance(String distance) {
		this.distance = distance;
	}
	/**
	 * @return the fphone
	 */
	public String getFphone() {
		return fphone;
	}
	/**
	 * @param fphone the fphone to set
	 */
	public void setFphone(String fphone) {
		this.fphone = fphone;
	}
	/**
	 * @return the mphone
	 */
	public String getMphone() {
		return mphone;
	}
	/**
	 * @param mphone the mphone to set
	 */
	public void setMphone(String mphone) {
		this.mphone = mphone;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}
	/**
	 * @return the picture
	 */
	public String getPicture() {
		return picture;
	}
	/**
	 * @param picture the picture to set
	 */
	public void setPicture(String picture) {
		this.picture = picture;
	}
	/**
	 * @return the latitude
	 */
	public Float getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the longtitude
	 */
	public Float getLongtitude() {
		return longtitude;
	}
	/**
	 * @param longtitude the longtitude to set
	 */
	public void setLongtitude(Float longtitude) {
		this.longtitude = longtitude;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the areaid
	 */
	public Integer getAreaid() {
		return areaid;
	}
	/**
	 * @param areaid the areaid to set
	 */
	public void setAreaid(Integer areaid) {
		this.areaid = areaid;
	}
	
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getBmxz() {
		return bmxz;
	}
	public void setBmxz(String bmxz) {
		this.bmxz = bmxz;
	}
	public String getBclx() {
		return bclx;
	}
	public void setBclx(String bclx) {
		this.bclx = bclx;
	}	
	
	public String getZizhi_status() {
		return zizhi_status;
	}
	public void setZizhi_status(String zizhi_status) {
		this.zizhi_status = zizhi_status;
	}
	public String getChangdi_status() {
		return changdi_status;
	}
	public void setChangdi_status(String changdi_status) {
		this.changdi_status = changdi_status;
	}
	@Override
	public String toString() {
		return "School [school_id=" + school_id + ", name=" + name
				+ ", distance=" + distance + ", fphone=" + fphone + ", mphone="
				+ mphone + ", address=" + address + ", price=" + price
				+ ", picture=" + picture + ", latitude=" + latitude
				+ ", longtitude=" + longtitude + ", description=" + description
				+ ", areaid=" + areaid + ", content=" + content + ", bmxz="
				+ bmxz + ", bclx=" + bclx + ", zizhi_status=" + zizhi_status + ", changdi_status=" + changdi_status + "]";
	}

	
	
}
