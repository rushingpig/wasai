package com.seastar.wasai.Entity;

import java.util.List;

/**
 * Created by yangteng on 2015/7/10.
 */
public class Order {
    private String orderId; //订单ID
    private String itemId; //商品ID
    private String title;
    private String platform; //平台
    private long time; //记录时间
    private int status;//订单状态 1, 2, 3
    private String picUrl; //商品图片
    private String msg; //提示消息
    private String fanli; //返利金额
    private String orderAmount;//订单金额
    private String confirmURL;//订单确认链接
    private long statusTime1; //下单，订单记录时间
    private long statusTime2; //确认订单时间
    private long statusTime3; //返利可用时间
    private String orderUrl;
    private List<OrderItem> items;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFanli() {
        return fanli;
    }

    public void setFanli(String fanli) {
        this.fanli = fanli;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getStatusTime1() {
        return statusTime1;
    }

    public void setStatusTime1(long statusTime1) {
        this.statusTime1 = statusTime1;
    }

    public long getStatusTime2() {
        return statusTime2;
    }

    public void setStatusTime2(long statusTime2) {
        this.statusTime2 = statusTime2;
    }

    public long getStatusTime3() {
        return statusTime3;
    }

    public void setStatusTime3(long statusTime3) {
        this.statusTime3 = statusTime3;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConfirmURL() {
        return confirmURL;
    }

    public void setConfirmURL(String confirmURL) {
        this.confirmURL = confirmURL;
    }

    public boolean isClosed(){
        return status == 3;
    }
    public boolean isBacked(){
        return status == 4;
    }

    public String getOrderUrl() {
        return orderUrl;
    }

    public void setOrderUrl(String orderUrl) {
        this.orderUrl = orderUrl;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
