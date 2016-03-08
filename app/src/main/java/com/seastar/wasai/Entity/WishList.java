package com.seastar.wasai.Entity;

/**
* @ClassName: WishList 
* @Description: 商品收藏夹
* @author 杨腾
* @date 2015年4月8日 上午11:15:01
 */
public class WishList {
	private long wishlistId;
	private String wishlistName;
	private String wishlistPic;
	public long getWishlistId() {
		return wishlistId;
	}
	public void setWishlistId(long wishlistId) {
		this.wishlistId = wishlistId;
	}
	public String getWishlistName() {
		return wishlistName;
	}
	public void setWishlistName(String wishlistName) {
		this.wishlistName = wishlistName;
	}
	public String getWishlistPic() {
		return wishlistPic;
	}
	public void setWishlistPic(String wishlistPic) {
		this.wishlistPic = wishlistPic;
	}
	
}
