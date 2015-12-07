package com.dt.android.domainobject;

import java.io.Serializable;

public class City  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2540309660561075949L;
	
	private Integer id;
	private String name;
	private CountyCode province;
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
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
	 * @return the province
	 */
	public CountyCode getProvice() {
		return province;
	}
	/**
	 * @param province the province to set
	 */
	public void setProvice(CountyCode provice) {
		this.province = provice;
	}
	
	public String getLocation(){
		if(province==null){
			return this.getName();
		}else{
			return province.getName()+" "+this.getName();
		}
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "City [id=" + id + ", name=" + name + ", province=" + province
				+ "]";
	}
	
	
	
}
