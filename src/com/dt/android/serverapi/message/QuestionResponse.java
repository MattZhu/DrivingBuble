package com.dt.android.serverapi.message;

import java.util.Arrays;

public class QuestionResponse extends Response{
	private String data_version;
	private Question content[];
	public String getData_version() {
		return data_version;
	}
	public void setData_version(String data_version) {
		this.data_version = data_version;
	}
	public Question[] getContent() {
		return content;
	}
	public void setContent(Question[] content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "QuestionResponse [data_version=" + data_version + ", content="
				+ Arrays.toString(content) +", super="+ super.toString()+"]";
	}
	
}
