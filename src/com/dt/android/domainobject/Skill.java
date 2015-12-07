package com.dt.android.domainobject;

public class Skill {
	private int id;
	private String name;
	private String fileName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	@Override
	public String toString() {
		return "Skill [id=" + id + ", name=" + name + ", fileName=" + fileName
				+ "]";
	}
	
}
