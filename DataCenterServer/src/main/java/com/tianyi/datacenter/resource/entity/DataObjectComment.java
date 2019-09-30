package com.tianyi.datacenter.resource.entity;

/**
 * 数据集结果集注解
 *
 * @author tianxujin
 * @version 0.1
 */
public class DataObjectComment {
    private int id;
    private int resId;
    private String name;
    private String comment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
