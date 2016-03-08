package com.seastar.wasai.Entity;

import com.seastar.wasai.views.common.ActWebActivity;
import com.seastar.wasai.views.common.EvaluationWebActivity;
import com.seastar.wasai.views.common.GuideWebActivity;
import com.seastar.wasai.views.common.ProductWebActivity;

/**
 * Created by yangteng on 2015/6/18.
 */
public enum Template {
    GUIDE(1, GuideWebActivity.class), EVALUATION(2, EvaluationWebActivity.class);
    private int id;
    private Class clazz;

    private Template(int id, Class clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}
