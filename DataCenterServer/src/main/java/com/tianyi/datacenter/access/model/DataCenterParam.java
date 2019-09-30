package com.tianyi.datacenter.access.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by tianxujin on 2019/6/18 14:14
 */
public class DataCenterParam implements Serializable{
    private Integer dataObjectId;
    private Map<String, Integer> pageInfo;
    private Object condition;
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getDataObjectId() {
        return dataObjectId;
    }

    public void setDataObjectId(Integer dataObjectId) {
        this.dataObjectId = dataObjectId;
    }

    public Map<String, Integer> getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(Map<String, Integer> pageInfo) {
        this.pageInfo = pageInfo;
    }

    public Object getCondition() {
        return condition;
    }

    public void setCondition(Object condition) {
        this.condition = condition;
    }
}
