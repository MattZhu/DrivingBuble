package com.dt.android.domainobject;

import java.io.Serializable;

public class SignCategory  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5618030112844332409L;
	
	private int id;
	private int parent_id;
	private int count;
	private int directCount;
	private String name;
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
	 * @return the parent_id
	 */
	public int getParent_id() {
		return parent_id;
	}
	/**
	 * @param parent_id the parent_id to set
	 */
	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
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
	
	
	
	public int getDirectCount() {
		return directCount;
	}
	public void setDirectCount(int directCount) {
		this.directCount = directCount;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SignCategory [id=" + id + ", parent_id=" + parent_id
				+ ", count=" + count + ", name=" + name + "]";
	}

	
}
