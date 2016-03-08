package com.seastar.wasai.Entity;

/**
 * Created by yangteng on 2015/6/18.
 */
public class ObjectType {
    private String title;
    private int templateId;

    public ObjectType() {
        super();
    }

    public ObjectType(String title, int templateId) {
        this.title = title;
        this.templateId = templateId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }
}
