package com.seastar.wasai.Entity;

/**
 * Created by yangteng on 2015/7/25.
 */
public class BackOrder {
    private String orderId; //订单ID
    private String itemId; //商品ID
    private String platform; //平台
    private String time; //记录时间
    private String fanli; //返利金额
    private String orderAmount;//订单金额
    private String confirmURL;//订单确认链接
    private String msg;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getConfirmURL() {
        return confirmURL;
    }

    public void setConfirmURL(String confirmURL) {
        this.confirmURL = confirmURL;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
