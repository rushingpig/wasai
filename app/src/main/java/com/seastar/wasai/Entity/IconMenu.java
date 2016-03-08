package com.seastar.wasai.Entity;

/**
 * Created by yangteng on 2015/6/17.
 * 首页四个入口
 */
public class IconMenu {
    private long id;
    private String imgUrl;
    private String title;
    private String link;
    private int type;
    private String color;
    private int isNeedLogin;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getIsNeedLogin() {
        return isNeedLogin;
    }

    public void setIsNeedLogin(int isNeedLogin) {
        this.isNeedLogin = isNeedLogin;
    }
}
