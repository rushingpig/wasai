package com.seastar.wasai.Entity;

import java.io.Serializable;

/**
 * Created by jamie on 2015/6/23.
 */
public class IntegralListEntity implements Serializable{
    public int prize_id;
    public int scores;
    public String name = "";
    public String price;
    public String pic_url;
    public String desc;
    public String url;

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
        String[] guidePics = pic_url.split(",");
        for (int i = 0; i < guidePics.length; i++) {
            if (guidePics[i].indexOf(picPrefix) >= 0) {
                picUrl = guidePics[i];
                break;
            }
        }
        return picUrl;
    }
}
