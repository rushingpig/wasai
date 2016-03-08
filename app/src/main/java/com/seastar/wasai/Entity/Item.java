package com.seastar.wasai.Entity;

import com.seastar.wasai.utils.StringUtil;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* @ClassName: Item 
* @Description: 商品实体类
* @author 杨腾
* @date 2015年4月8日 上午9:41:35
 */
public class Item implements Serializable{
	private static final long serialVersionUID = 131584121073821627L;
	private long itemId;
	private int itemCateId;
	private String itemName;
	private String itemDesc;
	private String picUrlSet;
	private String price;
	private String detailUrl;
	private String shopType;
	private ShopUrl shopUrl;
	private int isHot;
	private int favoriteCount;
	private int shareCount;
	private int commentCount;
	private String putaway;
	private long lastUpdateTime;
	private long createTime;
	private String author;
	private long wishId;
	private String platform;
	private String unit;
	private String rate;
	private String opid;
	private long remainTime = -100;
	private String location;
	private String saleCount;
	private String fanli;

	public String getFanli() {
		return fanli;
	}

	public void setFanli(String fanli) {
		this.fanli = fanli;
	}

	public long getItemId() {
		return itemId;
	}
	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
	public int getItemCateId() {
		return itemCateId;
	}
	public void setItemCateId(int itemCateId) {
		this.itemCateId = itemCateId;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemDesc() {
		return itemDesc;
	}
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
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
	public String getDetailUrl() {
		return detailUrl;
	}
	public void setDetailUrl(String detailUrl) {
		this.detailUrl = detailUrl;
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
	public int getIsHot() {
		return isHot;
	}
	public void setIsHot(int isHot) {
		this.isHot = isHot;
	}
	public int getFavoriteCount() {
		return favoriteCount;
	}
	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}
	public int getShareCount() {
		return shareCount;
	}
	public void setShareCount(int shareCount) {
		this.shareCount = shareCount;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public String getPutaway() {
		return putaway;
	}
	public void setPutaway(String putaway) {
		this.putaway = putaway;
	}
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
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
	public long getWishId() {
		return wishId;
	}
	public void setWishId(long wishId) {
		this.wishId = wishId;
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
	public String getOff(){
		String[] prices = this.getPrice().split(",");
		BigDecimal b = new BigDecimal((Double.parseDouble(prices[0]) - Double.parseDouble(prices[1])));
		return unit + b.setScale(0,BigDecimal.ROUND_HALF_UP);
	}

	public String getSale(){
		String[] prices = this.getPrice().split(",");
		double currentPrice = Double.parseDouble(prices[0]) - Double.parseDouble(prices[1]);
		BigDecimal b = new BigDecimal(currentPrice / (Double.parseDouble(prices[0])) * 100);
		return "-" + b.setScale(0, BigDecimal.ROUND_HALF_UP) + "%";
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
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
		String[] guidePics = this.getPicUrlSet().split(",");
		for (int i = 0; i < guidePics.length; i++) {
			if (guidePics[i].indexOf(picPrefix) >= 0) {
				picUrl = guidePics[i];
				break;
			}
		}
		return picUrl;
	}
	public void setUnit(String unit){
		this.unit = unit;
	}
	public String getUnit(){
		return unit;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getRate() {
		return StringUtil.isNotEmpty(rate)?rate:"0";
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public long getRemainTime() {
		return remainTime;
	}

	public void setRemainTime(long remainTime) {
		this.remainTime = remainTime;
	}

	public boolean isSuperItem(){
		return String.valueOf(this.getItemId()).startsWith("9");
	}

	public String getOpid() {
		return opid;
	}

	public void setOpid(String opid) {
		this.opid = opid;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSaleCount() {
		return saleCount;
	}

	public void setSaleCount(String saleCount) {
		this.saleCount = saleCount;
	}
}
