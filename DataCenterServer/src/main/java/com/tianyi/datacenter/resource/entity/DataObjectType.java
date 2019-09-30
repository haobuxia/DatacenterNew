package com.tianyi.datacenter.resource.entity;

import java.util.ArrayList;
import java.util.List;

public class DataObjectType {
    private int id;
    private String typeName;
    private int fatherId;
    private int category; //0对象1数据集
    private List<DataObjectType> children = new ArrayList<DataObjectType>();

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void add(DataObjectType node) {
        children.add(node);
    }

    public List<DataObjectType> getChildren() {
        return children;
    }

    public void setChildren(List<DataObjectType> children) {
        this.children = children;
    }

    public DataObjectType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getFatherId() {
        return fatherId;
    }

    public void setFatherId(int fatherId) {
        this.fatherId = fatherId;
    }
}
