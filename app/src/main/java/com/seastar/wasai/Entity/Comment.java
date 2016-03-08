package com.seastar.wasai.Entity;

import android.graphics.Bitmap;

/**
* @ClassName: Comment 
* @Description: 评论实体类
* @author 杨腾
* @date 2015年4月8日 上午10:45:55
 */
public class Comment {
	private long commentId;
	private String uuid;
	private String nickname;
	private String pictureUrl;
	private String sourceType;
	private String sourceId;
	private String commentInfo;
	private long commentTime;
	private int status;
	Bitmap picBm;
	public long getCommentId() {
		return commentId;
	}
	public void setCommentId(long commentId) {
		this.commentId = commentId;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getPictureUrl() {
		return pictureUrl;
	}
	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getCommentInfo() {
		return commentInfo;
	}
	public void setCommentInfo(String commentInfo) {
		this.commentInfo = commentInfo;
	}
	public long getCommentTime() {
		return commentTime;
	}
	public void setCommentTime(long commentTime) {
		this.commentTime = commentTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Bitmap getPicBm() {
		return this.picBm;
	}
	public void setPicBm(Bitmap bm) {
		this.picBm = bm;
	}
}
