package com.dt.android.serverapi.message;

public class VersionResponse extends Response {
	private String app_version;
	private String data_version;

	private Integer question_num;

	public String getApp_version() {
		return app_version;
	}

	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}

	public String getData_version() {
		return data_version;
	}

	public void setData_version(String data_version) {
		this.data_version = data_version;
	}

	public Integer getQuestion_num() {
		return question_num;
	}

	public void setQuestion_num(Integer question_num) {
		this.question_num = question_num;
	}

	@Override
	public String toString() {
		return "VersionResponse [app_version=" + app_version
				+ ", data_version=" + data_version + "question_num="
				+ question_num + super.toString() + "]";
	}

}
