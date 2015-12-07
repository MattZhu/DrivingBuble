package com.dt.android.serverapi.message;

import java.util.Arrays;

public class Question {
	private Integer question_id;//":"13",
    private String desc;//":"\u673a\u52a8\u8f66\u9a7e\u9a76\u4eba\u8fdd\u6cd5\u9a7e\u9a76\u9020\u6210\u91cd\u5927\u4ea4\u901a\u4e8b\u6545\u6784\u6210\u72af\u7f6a\u7684\uff0c\u4f9d\u6cd5\u8ffd\u7a76\u4ec0\u4e48\u8d23\u4efb\uff1f",
    private String picture;//":"",
    private Integer type;//":"1",
    private Integer is_a;//":0,
    private Integer is_b;//":0,
    private Integer is_c;//":1,
    private String answer[];   
    private String correct_answer[];//":"0",
    private String explanation;//":""
	public Integer getQuestion_id() {
		return question_id;
	}
	public void setQuestion_id(Integer question_id) {
		this.question_id = question_id;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getIs_a() {
		return is_a;
	}
	public void setIs_a(Integer is_a) {
		this.is_a = is_a;
	}
	public Integer getIs_b() {
		return is_b;
	}
	public void setIs_b(Integer is_b) {
		this.is_b = is_b;
	}
	public Integer getIs_c() {
		return is_c;
	}
	public void setIs_c(Integer is_c) {
		this.is_c = is_c;
	}
	public String[] getAnswer() {
		return answer;
	}
	public void setAnswer(String[] answer) {
		this.answer = answer;
	}
	public String[] getCorrect_answer() {
		return correct_answer;
	}
	public void setCorrect_answer(String correct_answer[]) {
		this.correct_answer = correct_answer;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	
	
	@Override
	public String toString() {
		return "Question [question_id=" + question_id + ", desc=" + desc
				+ ", picture=" + picture + ", type=" + type + ", is_a=" + is_a
				+ ", is_b=" + is_b + ", is_c=" + is_c + ", answer="
				+ Arrays.toString(answer) + ", correct_answer="
				+ Arrays.toString(correct_answer) + ", explanation=" + explanation + "]";
	}
    
}
