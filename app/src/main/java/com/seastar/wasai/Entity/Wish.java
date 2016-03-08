package com.seastar.wasai.Entity;

/**
* @ClassName: Wish 
* @Description: 商品收藏
* @author 杨腾
* @date 2015年4月8日 上午11:32:30
 */
public class Wish {
	private long itemId;
	private String itemName;
	private String picUrlSet;
	private String price;
	private String shopType;
	private ShopUrl shopUrl;
	private String itemDesc;
	private String platform;
	private int favoriteCount;
	private String unit;
	private String opid;

	public String getOpid() {
		return opid;
	}

	public void setOpid(String opid) {
		this.opid = opid;
	}

	public long getItemId() {
		return itemId;
	}
	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getPicUrlSet() {
		return picUrlSet;
	}
	public void setPicUrlSet(String picUrlSet) {
		this.picUrlSet = picUrlSet;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
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
		default:
			picPrefix = "_small";
			break;
		}
		String picUrl = "";
		String[] guidePics = this.getPicUrlSet().split(",");
		for (int i = 0; i < guidePics.length; i++) {
			if (guidePics[i].indexOf(picPrefix) >= 0) {
				picUrl = guidePics[i];
				break;
			}
		}
		return picUrl;
	}
	
	public String getBeforePrice(){
		String beforePrice = "";
		String[] prices = this.getPrice().split(",");
		if(prices.length > 1){
			beforePrice = prices[0];
		}
		return unit + beforePrice;
	}
	public String getNowPrice(){
		String nowPrice = "";
		String[] prices = this.getPrice().split(",");
		if(prices.length > 1){
			nowPrice = prices[1];
		}else{
			nowPrice = prices[0];
		}
		return unit + nowPrice;
	}
	public void setUnit(String unit){
		this.unit = unit;
	}
	public String getUnit(){
		return unit;
	}
	public String getShopType() {
		return shopType;
	}
	public void setShopType(String shopType) {
		this.shopType = shopType;
	}
	public ShopUrl getShopUrl() {
		return shopUrl;
	}
	public void setShopUrl(ShopUrl shopUrl) {
		this.shopUrl = shopUrl;
	}
	public String getItemDesc() {
		return itemDesc;
	}
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public int getFavoriteCount() {
		return favoriteCount;
	}
	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}
	
}
