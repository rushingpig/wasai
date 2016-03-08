package com.seastar.wasai.Entity;

import java.io.Serializable;

/**
* @ClassName: ShopUrl 
* @Description: 商品跳转链接
* @author 杨腾
* @date 2015年4月24日 下午2:17:13
 */
public class ShopUrl implements Serializable{
	private String pcUrl;
	private String mobileUrl;
	private String nativeUrl;
	public String getPcUrl() {
		return pcUrl;
	}
	public void setPcUrl(String pcUrl) {
		this.pcUrl = pcUrl;
	}
	public String getMobileUrl() {
		return mobileUrl;
	}
	public void setMobileUrl(String mobileUrl) {
		this.mobileUrl = mobileUrl;
	}
	public String getNativeUrl() {
		return nativeUrl;
	}
	public void setNativeUrl(String nativeUrl) {
		this.nativeUrl = nativeUrl;
	}
	
}
