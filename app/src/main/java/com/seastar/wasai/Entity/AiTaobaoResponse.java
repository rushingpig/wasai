package com.seastar.wasai.Entity;

import java.util.List;

/**
 * Created by yangteng on 2015/9/1.
 */
public class AiTaobaoResponse {
    private double customerRate;
    private List<AiTaobaoProduct> items;

    public double getCustomerRate() {
        return customerRate;
    }

    public void setCustomerRate(double customerRate) {
        this.customerRate = customerRate;
    }

    public List<AiTaobaoProduct> getItems() {
        return items;
    }

    public void setItems(List<AiTaobaoProduct> items) {
        this.items = items;
    }
}
