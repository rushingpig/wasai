package com.seastar.wasai.Entity;

/**
 * Created by yangteng on 2015/6/19.
 */
public class Store {
    private long id;
    private String imgUrl;
    private ShopUrl storeUrl;
    private String title;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public ShopUrl getStoreUrl() {
        return storeUrl;
    }

    public void setStoreUrl(ShopUrl storeUrl) {
        this.storeUrl = storeUrl;
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
        String[] guidePics = this.getImgUrl().split(",");
        for (int i = 0; i < guidePics.length; i++) {
            if (guidePics[i].indexOf(picPrefix) >= 0) {
                picUrl = guidePics[i];
                break;
            }
        }
        return picUrl;
    }
}
