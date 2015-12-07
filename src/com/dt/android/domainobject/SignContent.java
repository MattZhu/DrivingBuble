package com.dt.android.domainobject;

import java.io.Serializable;

public class SignContent  implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5368735915377544120L;

	
	private int id;
	private int categoryId;
	private String name;
	private String picture;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the categoryId
	 */
	public int getCategoryId() {
		return categoryId;
	}
	/**
	 * @param categoryId the categoryId to set
	 */
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SignContent [id=" + id + ", categoryId=" + categoryId
				+ ", name=" + name + ", picture=" + picture + "]";
	}
	
	
	
}
