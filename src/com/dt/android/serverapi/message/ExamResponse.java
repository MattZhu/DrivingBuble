package com.dt.android.serverapi.message;

import java.util.Arrays;

public class ExamResponse  extends Response{
	private ExamSet[]first_content;
	private  ExamSet[]third_content;
	public ExamSet[] getFirst_content() {
		return first_content;
	}
	public void setFirst_content(ExamSet[] first_content) {
		this.first_content = first_content;
	}
	public ExamSet[] getThird_content() {
		return third_content;
	}
	public void setThird_content(ExamSet[] third_content) {
		this.third_content = third_content;
	}
	@Override
	public String toString() {
		return "ExamResponse [first_content=" + Arrays.toString(first_content)
				+ ", third_content=" + Arrays.toString(third_content) + super.toString()+ "]";
	}
	
	
}
