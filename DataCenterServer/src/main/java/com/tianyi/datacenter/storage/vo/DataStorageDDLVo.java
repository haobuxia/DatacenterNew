package com.tianyi.datacenter.storage.vo;

import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;

import java.util.List;
import java.util.Map;

/**
 * 数据存储模块执行DDL接口Vo
 *
 * @author zhouwei
 * @version 0.1
 */
public class DataStorageDDLVo {
    private String ddlType;
    private DataObject dataObject;
    private List<DataObjectAttribute> attributes;
    private List<DataObjectAttribute> addColumns;
    private List<DataObjectAttribute> alterColumns;
    private List<DataObjectAttribute> dropColumns;
    private Map<String, Object> pkInfo;

    public String getDdlType() {
        return ddlType;
    }

    public void setDdlType(String ddlType) {
        this.ddlType = ddlType;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    public List<DataObjectAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<DataObjectAttribute> attributes) {
        this.attributes = attributes;
    }

    public void setAddColumns(List<DataObjectAttribute> addColumns) {
        this.addColumns = addColumns;
    }

    public List<DataObjectAttribute> getAddColumns() {
        return addColumns;
    }

    public List<DataObjectAttribute> getAlterColumns() {
        return alterColumns;
    }

    public void setAlterColumns(List<DataObjectAttribute> alterColumns) {
        this.alterColumns = alterColumns;
    }

    public void setDropColumns(List<DataObjectAttribute> dropColumns) {
        this.dropColumns = dropColumns;
    }

    public List<DataObjectAttribute> getDropColumns() {
        return dropColumns;
    }

    public Map<String, Object> getPkInfo() {
        return pkInfo;
    }

    public void setPkInfo(Map<String, Object> pkInfo) {
        this.pkInfo = pkInfo;
    }
}
