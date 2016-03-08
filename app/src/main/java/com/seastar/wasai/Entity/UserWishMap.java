package com.seastar.wasai.Entity;

/**
* @ClassName: UserWishMap 
* @Description: 用户商品喜欢映射
* @author 杨腾
* @date 2015年4月23日 下午4:14:56
 */
public class UserWishMap {
	private long itemId;
	private long wishId;
	public long getItemId() {
		return itemId;
	}
	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
	public long getWishId() {
		return wishId;
	}
	public void setWishId(long wishId) {
		this.wishId = wishId;
	}
}
