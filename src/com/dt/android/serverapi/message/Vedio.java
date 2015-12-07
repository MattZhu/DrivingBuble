package com.dt.android.serverapi.message;

public class Vedio {
	private Integer id;
	private String title;
	private String thumb;
	private String vo_url;
	private String desc;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	
	
	
	public String getVo_url() {
		return vo_url;
	}
	public void setVo_url(String vo_url) {
		this.vo_url = vo_url;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	@Override
	public String toString() {
		return "Vedio [id=" + id + ", title=" + title + ", thumb=" + thumb
				+ ", vo_url=" + vo_url + ", desc=" + desc + "]";
	}
	
	
	
}
