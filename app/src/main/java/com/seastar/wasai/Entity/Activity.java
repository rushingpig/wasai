package com.seastar.wasai.Entity;

import com.seastar.wasai.utils.GeneralUtil;
import com.seastar.wasai.views.extendedcomponent.MyApplication;

import java.io.Serializable;

/**
* @ClassName: Activity 
* @Description: 活动
* @author 杨腾
* @date 2015年4月23日 上午11:11:33
 */
public class Activity implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private long activityId;
	private String title;
	private String description;
	private String picUrl;
	private String detailUrl;
	private int status;
	private long createTime;
	private String author;
	private String templateName;
	private String pageUrl;//用于分享的URL
	public long getActivityId() {
		return activityId;
	}
	public void setActivityId(long activityId) {
		this.activityId = activityId;
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
	
	
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getDetailUrl() {
		return detailUrl + "?version=" + GeneralUtil.getAppVersionName(MyApplication.getContextObject()) + "&sessionid=" + MyApplication.getSessionId() + (MyApplication.isLogin()?"&uuid=" + MyApplication.getCurrentUser().getUuid():"");
	}
	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
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
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
}
