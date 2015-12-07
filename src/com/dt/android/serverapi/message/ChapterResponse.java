package com.dt.android.serverapi.message;

import java.util.Arrays;

public class ChapterResponse extends Response {
	private Chapter[]first_content;
	private Chapter[]third_content;
	public Chapter[] getFirst_content() {
		return first_content;
	}
	public void setFirst_content(Chapter[] first_content) {
		this.first_content = first_content;
	}
	public Chapter[] getThird_content() {
		return third_content;
	}
	public void setThird_content(Chapter[] third_content) {
		this.third_content = third_content;
	}
	@Override
	public String toString() {
		return "ChapterResponse [first_content="
				+ Arrays.toString(first_content) + ", third_content="
				+ Arrays.toString(third_content) + ","+super.toString()+"]";
	}
	
	
}
