package com.seastar.wasai.Entity;

import java.util.List;

/**
* @ClassName: GuideTags 
* @Description: 导购标签
* @author 杨腾
* @date 2015年4月22日 下午4:08:09
 */
public class GuideTags {
	private long guideId;
	private List<Tag> tags;
	public long getGuideId() {
		return guideId;
	}
	public void setGuideId(long guideId) {
		this.guideId = guideId;
	}
	public List<Tag> getTags() {
		return tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
}
