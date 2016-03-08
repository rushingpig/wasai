package com.seastar.wasai.Entity;

/**
 * Created by yangteng on 2015/6/24.
 * 奖品
 */
public class Award {
    private long id;
    private String name;
    private String price;
    private String picUrl;
    private String desc;
    private ShopUrl url;
    private String symbol;
    private String platform;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ShopUrl getUrl() {
        return url;
    }

    public void setUrl(ShopUrl url) {
        this.url = url;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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
        String[] pics = this.getPicUrl().split(",");
        for (int i = 0; i < pics.length; i++) {
            if (pics[i].indexOf(picPrefix) >= 0) {
                picUrl = pics[i];
                break;
            }
        }
        return picUrl;
    }
}
