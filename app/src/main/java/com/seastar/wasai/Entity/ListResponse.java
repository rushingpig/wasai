package com.seastar.wasai.Entity;

import java.util.List;

/**
 * Created by yangteng on 2015/7/13.
 */
public class ListResponse<T> {
    private List<T> list;
    private Object extra;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
