package com.tianyi.datacenter.storage.vo;

import java.io.Serializable;

public class RequestVo<T> implements Serializable {

    private T request;
    private PageListVo pageInfo;
    private String orderBy;

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public RequestVo(T request) {
        this.request = request;
    }

    public T getRequest() {
        return request;
    }

    public void setRequest(T request) {
        this.request = request;
    }

    public PageListVo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageListVo pageInfo) {
        this.pageInfo = pageInfo;
    }
}
