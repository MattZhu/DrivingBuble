package com.dt.android.serverapi.message;

public class SchoolDetailResponse extends Response {
	private School content;

	public School getContent() {
		return content;
	}

	public void setContent(School content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "SchoolDetailResponse [content=" + content +super.toString()+ "]";
	}
	
}
