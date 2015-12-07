package com.dt.android.domainobject;

import java.io.Serializable;


public class AppData  implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4036767623106502086L;

	public static final String SCHOOL_IMAGE_CACHE_FOLDER="/school";
	
	public static final String VEDIO_IMAGE_CACHE_FOLDER="/vedio";
	
	public static final String QUESTION_IMAGE_CACHE_FOLDER="/q_image";
	public static final String QUESTION_VEDIO_CACHE_FOLDER="/q_vedio";

	private static AppData instance;
	

	private CountyCode currentLocation;
	
	private boolean nightMode;
	
	private int carType = 0;
	
	private int qType = 1;
	
	private String appVersion;
	
	private String dataVersion;
	
	private boolean showIntro;


	private AppData(){
		
	}
	
	/**
	 * @return the instnace
	 */
	public static synchronized AppData getInstance() {
		if(instance==null){
			instance=new AppData();
		}
		return instance;
	}
	

	/**
	 * @return the currentLocation
	 */
	public CountyCode getCurrentLocation() {
		return currentLocation;
	}

	/**
	 * @param currentLocation the currentLocation to set
	 */
	public void setCurrentLocation(CountyCode currentLocation) {
		this.currentLocation = currentLocation;
	}

	/**
	 * @return the nightMode
	 */
	public boolean isNightMode() {
		return nightMode;
	}

	/**
	 * @param nightMode the nightMode to set
	 */
	public void setNightMode(boolean nightMode) {
		this.nightMode = nightMode;
	}
	
	/**
	 * @return the carType
	 */
	public int getCarType() {
		return carType;
	}

	/**
	 * @param carType the carType to set
	 */
	public void setCarType(int carType) {
		this.carType = carType;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getDataVersion() {
		return dataVersion;
	}

	public void setDataVersion(String dataVersion) {
		this.dataVersion = dataVersion;
	}

	public int getqType() {
		return qType;
	}

	public void setqType(int qType) {
		this.qType = qType;
	}

	public boolean getShowIntro() {
		return showIntro;
	}
	
	public void setShowIntro(boolean showIntro) {
		this.showIntro=showIntro;
	}
	
	
	
}
