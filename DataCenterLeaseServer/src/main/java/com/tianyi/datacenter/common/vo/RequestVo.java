package com.tianyi.datacenter.common.vo;

import java.io.Serializable;

public class RequestVo<T> implements Serializable {

    private T request;
    private PageListVo pageInfo;

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
