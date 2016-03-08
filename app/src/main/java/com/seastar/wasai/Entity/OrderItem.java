package com.seastar.wasai.Entity;

/**
 * Created by yangteng on 2015/8/31.
 */
public class OrderItem {
    private String item_image_url;
    private String item_title;
    private int item_num;

    public String getItem_image_url() {
        return item_image_url;
    }

    public void setItem_image_url(String item_image_url) {
        this.item_image_url = item_image_url;
    }

    public String getItem_title() {
        return item_title;
    }

    public void setItem_title(String item_title) {
        this.item_title = item_title;
    }

    public int getItem_num() {
        return item_num;
    }

    public void setItem_num(int item_num) {
        this.item_num = item_num;
    }
}
