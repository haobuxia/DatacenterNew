package com.tianyi.datacenter.storage.vo;

import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;

import java.util.List;

/**
 * //TODO 说明
 *
 * @author zhouwei
 * 2018/11/20 14:47
 * @version 0.1
 **/
public class DataStorageDMLVo {
    private String dmlSql;

    private String dmlType;
    private DataObject dataObject;
    //where条件字段
    private List<DataObjectAttribute> condition;
    //select查询字段，新增字段信息，更新字段信息
    private List<DataObjectAttribute> attributes;
    //新增字段信息，更新字段信息
//    private List<DataObjectAttribute> updateInfo;

    public String getDmlSql() {
        return dmlSql;
    }

    public void setDmlSql(String dmlSql) {
        this.dmlSql = dmlSql;
    }

    public String getDmlType() {
        return dmlType;
    }

    public void setDmlType(String dmlType) {
        this.dmlType = dmlType;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    public List<DataObjectAttribute> getCondition() {
        return condition;
    }

    public void setCondition(List<DataObjectAttribute> condition) {
        this.condition = condition;
    }

    public List<DataObjectAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<DataObjectAttribute> attributes) {
        this.attributes = attributes;
    }
/*
    public List<DataObjectAttribute> getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(List<DataObjectAttribute> updateInfo) {
        this.updateInfo = updateInfo;
    }*/
}
