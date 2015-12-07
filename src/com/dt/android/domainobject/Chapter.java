package com.dt.android.domainobject;

import java.io.Serializable;

public class Chapter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6675634742892542568L;
	private Integer id;
	private Integer chapterId;
	private String chapter;
	private boolean hasChildren;

	/**
	 * can be error question, stared question or all question for this chapter.
	 */
	private int quesitonCount;
	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the chapterId
	 */
	public Integer getChapterId() {
		return chapterId;
	}

	/**
	 * @param chapterId
	 *            the chapterId to set
	 */
	public void setChapterId(Integer chapterId) {
		this.chapterId = chapterId;
	}

	/**
	 * @return the chapter
	 */
	public String getChapter() {
		return chapter;
	}

	/**
	 * @param chapter
	 *            the chapter to set
	 */
	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	/**
	 * @return the quesitonCount
	 */
	public int getQuesitonCount() {
		return quesitonCount;
	}

	/**
	 * @param quesitonCount
	 *            the quesitonCount to set
	 */
	public void setQuesitonCount(int quesitonCount) {
		this.quesitonCount = quesitonCount;
	}

	public boolean isHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}


}
