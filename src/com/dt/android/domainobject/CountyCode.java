package com.dt.android.domainobject;

import java.io.Serializable;

public class CountyCode  implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8485731037095031989L;
	
	private Integer id;
	private String name;
	private boolean municipality;
	
	private CountyCode parent;
	
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
	 * @return the municipality
	 */
	public boolean isMunicipality() {
		return municipality;
	}
	/**
	 * @param municipality the municipality to set
	 */
	public void setMunicipality(boolean municipality) {
		this.municipality = municipality;
	}
	
	
	
	/**
	 * @return the parent
	 */
	public CountyCode getParent() {
		return parent;
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParent(CountyCode parent) {
		this.parent = parent;
	}
	
	public String getLocation(){
		if(parent!=null){
			return parent.getLocation()+" "+this.name;
		}else{
			return this.name;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CountyCode [id=" + id + ", name=" + name + ", municipality="
				+ municipality + "]";
	}

}
