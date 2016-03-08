package com.seastar.wasai.Entity;

import java.io.Serializable;

/**
* @ClassName: Author 
* @Description: 导购作者
* @author 杨腾
* @date 2015年4月23日 上午10:57:38
 */
public class Author implements Serializable{
	private static final long serialVersionUID = -397312184673258769L;
	private String author;
	private String type;
	private String url;
	private String description;
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
