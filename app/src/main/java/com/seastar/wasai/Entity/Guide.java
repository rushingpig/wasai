package com.seastar.wasai.Entity;

import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: Guide
 * @Description: 导购实体类
 * @author 杨腾
 * @date 2015年4月7日 下午5:48:23
 */
public class Guide implements Serializable{
	private static final long serialVersionUID = 377198745338604548L;
	private long guideId;
	private int type;
	private String title;
	private String description;
	private String picUrl;
	private String detailUrl;
	private int isHot;
	private long favoriteCount;
	private long shareCount;
	private long commentCount;
	private long pvCount;
	private int status;
	private long createTime;
	private long favoriteId;
	private long lastUpdateTime;
	private List<Tag> tags;
	private String items;
	private Author author;
	private List<FocusPosition> position;
	private boolean isFocusVisible = true;

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getDetailUrl() {
		return detailUrl + "?version=" + GeneralUtil.getAppVersionName(MyApplication.getContextObject()) + "&sessionid=" + MyApplication.getSessionId() + (MyApplication.isLogin()?"&uuid=" + MyApplication.getCurrentUser().getUuid():"");
	}

	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
	}

	public int getIsHot() {
		return isHot;
	}

	public void setIsHot(int isHot) {
		this.isHot = isHot;
	}

	public long getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public long getShareCount() {
		return shareCount;
	}

	public void setShareCount(long shareCount) {
		this.shareCount = shareCount;
	}

	public long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(long commentCount) {
		this.commentCount = commentCount;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(long favoriteId) {
		this.favoriteId = favoriteId;
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
		case Image.SIZE_LOGO:
			picPrefix = "logo-";
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

	public boolean isFocusVisible() {
		return isFocusVisible;
	}

	public void setFocusVisible(boolean isFocusVisible) {
		this.isFocusVisible = isFocusVisible;
	}
}
