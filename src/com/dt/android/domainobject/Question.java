package com.dt.android.domainobject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

public class Question implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7094438566750592040L;
	private Integer id;
	private String desciption;
	private String image;
	private List<String> options;
	private Integer[] answer;
	private String answerDetail;
	private Integer userAnswer[] = null;
	private boolean stared;
	private boolean errored;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the desciption
	 */
	public String getDesciption() {
		return desciption;
	}

	/**
	 * @param desciption
	 *            the desciption to set
	 */
	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the options
	 */
	public List<String> getOptions() {

		return options;
	}

	/**
	 * @param options
	 *            the options to set
	 */
	public void setOptions(List<String> options) {
		this.options = options;
	}

	/**
	 * @return the answer
	 */
	public Integer[] getAnswer() {
		return answer;
	}

	/**
	 * @param answer
	 *            the answer to set
	 */
	public void setAnswer(Integer[] answer) {
		this.answer = answer;
	}

	/**
	 * @return the answerDetail
	 */
	public String getAnswerDetail() {
		return answerDetail;
	}

	/**
	 * @param answerDetail
	 *            the answerDetail to set
	 */
	public void setAnswerDetail(String answerDetail) {
		this.answerDetail = answerDetail;
	}

	/**
	 * @return the userAnswer
	 */
	public Integer[] getUserAnswer() {
		return userAnswer;
	}

	/**
	 * @param userAnswer
	 *            the userAnswer to set
	 */
	public void setUserAnswer(Integer[] userAnswer) {
		this.userAnswer = userAnswer;
	}

	public boolean isAnswered() {
		return this.userAnswer != null;
	}

	public boolean isCorrected() {
		return isAnswered() && answerEquals();
	}

	private boolean answerEquals() {
		if(userAnswer.length!=answer.length){
			return false;
		}
		for(Integer i:userAnswer){
			boolean flag=false;
			for(Integer a:answer){
				if(i.equals(a)){
					flag=true;
					break;					
				}
			}
			if(!flag){
				return false;
			}
		}
		return true;
	}

	/**
	 * @return the stared
	 */
	public boolean isStared() {
		return stared;
	}

	/**
	 * @param stared
	 *            the stared to set
	 */
	public void setStared(boolean stared) {
		this.stared = stared;
	}

	/**
	 * @return the errored
	 */
	public boolean isErrored() {
		return errored;
	}

	/**
	 * @param errored
	 *            the errored to set
	 */
	public void setErrored(boolean errored) {
		this.errored = errored;
	}
	
	public boolean isSingleOption(){
		return this.answer.length==1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Question [id=" + id + ", desciption=" + desciption + ", image="
				+ image + ", options=" + options + ", answer=" + Arrays.toString(answer)
				+ ", answerDetail=" + answerDetail + ", userAnswer="
				+  Arrays.toString(userAnswer) + ", stared=" + stared + "]";
	}

}
