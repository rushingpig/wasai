package com.seastar.wasai.Entity;

import java.io.Serializable;
import java.util.List;

/**
* @ClassName: Favorite 
* @Description: 导购收藏
* @author 杨腾
* @date 2015年4月8日 上午11:05:09
 */
public class Favorite implements Serializable{
	private static final long serialVersionUID = 1L;
	private long guideId;
	private int type;
	private String title;
	private String picUrl;
	private String detailUrl;
	private String description;
	private int favoriteCount;
	private int commentCount;
	private List<Tag> tags;
	private String items;
	private Author author;
	private long lastUpdateTime;
	private long shareCount;
	private long pvCount;
	private List<FocusPosition> position;
	public long getGuideId() {
		return guideId;
	}
	public void setGuideId(long guideId) {
		this.guideId = guideId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getDetailUrl() {
		return detailUrl;
	}
	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getFavoriteCount() {
		return favoriteCount;
	}
	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	
	public String getPic(int type) {
		String picPrefix = "_small";
		switch (type) {
		case Image.SIZE_SMALL:
			break;
		case Image.SIZE_MIDDLE:
			picPrefix = "_middle";
			break;
		case Image.SIZE_BIG:
			picPrefix = "_big";
			break;
		case Image.SIZE_LARGE:
			picPrefix = "_large";
			break;
		case Image.SIZE_THUMB:
			picPrefix = "_thumb";
			break;
		default:
			picPrefix = "_small";
			break;
		}
		String picUrl = "";
		String[] guidePics = this.getPicUrl().split(",");
		for (int i = 0; i < guidePics.length; i++) {
			if (guidePics[i].indexOf(picPrefix) >= 0) {
				picUrl = guidePics[i];
				break;
			}
		}
		return picUrl;
	}
	public List<Tag> getTags() {
		return tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	public String getItems() {
		return items;
	}
	public void setItems(String items) {
		this.items = items;
	}
	public Author getAuthor() {
		return author;
	}
	public void setAuthor(Author author) {
		this.author = author;
	}
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public long getShareCount() {
		return shareCount;
	}
	public void setShareCount(long shareCount) {
		this.shareCount = shareCount;
	}
	public long getPvCount() {
		return pvCount;
	}
	public void setPvCount(long pvCount) {
		this.pvCount = pvCount;
	}
	public List<FocusPosition> getPosition() {
		return position;
	}
	public void setPosition(List<FocusPosition> position) {
		this.position = position;
	}
	
}
