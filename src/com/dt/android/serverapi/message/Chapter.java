package com.dt.android.serverapi.message;

public class Chapter {
	private Integer chapter_id;
	private String chapter_name;
	private Integer parent_chapter_id;
	private String questions;
	public Integer getChapter_id() {
		return chapter_id;
	}
	public void setChapter_id(Integer chapter_id) {
		this.chapter_id = chapter_id;
	}
	public String getChapter_name() {
		return chapter_name;
	}
	public void setChapter_name(String chapter_name) {
		this.chapter_name = chapter_name;
	}
	public Integer getParent_chapter_id() {
		return parent_chapter_id;
	}
	public void setParent_chapter_id(Integer parent_chapter_id) {
		this.parent_chapter_id = parent_chapter_id;
	}
	public String getQuestions() {
		return questions;
	}
	public void setQuestions(String questions) {
		this.questions = questions;
	}
	@Override
	public String toString() {
		return "Chapter [chapter_id=" + chapter_id + ", chapter_name="
				+ chapter_name + ", parent_chapter_id=" + parent_chapter_id
				+ ", questions=" + questions + "]";
	}
	
	
}
