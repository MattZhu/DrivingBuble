package com.dt.android.domainobject;

import java.util.HashMap;
import java.util.Map;

public class OpData {
	private int imageRes;
	private int stringId;
	private Class clazz;
	private Map<String,Integer>extras=new HashMap<String,Integer>();
	public OpData(int imageRes, int stringId, Class clazz) {
		this.imageRes = imageRes;
		this.stringId = stringId;
		this.clazz=clazz;
	}
	
	public void addExtra(String key,Integer value){
		if(extras==null){
			extras=new HashMap<String,Integer>();
		}
		extras.put(key, value);
	}
	
	public Map<String,Integer>getExtras(){
		return extras;
	}
	
	/**
	 * @return the imageRes
	 */
	public int getImageRes() {
		return imageRes;
	}
	/**
	 * @param imageRes the imageRes to set
	 */
	public void setImageRes(int imageRes) {
		this.imageRes = imageRes;
	}
	/**
	 * @return the stringId
	 */
	public int getStringId() {
		return stringId;
	}
	/**
	 * @param stringId the stringId to set
	 */
	public void setStringId(int stringId) {
		this.stringId = stringId;
	}
	/**
	 * @return the clazz
	 */
	public Class getClazz() {
		return clazz;
	}
	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	
	
	
}
