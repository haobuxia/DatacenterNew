package com.tianyi.datacenter.common.vo;

import java.io.Serializable;

public class RequestVo2<T> implements Serializable {

    private T condition;

    private PageListVo pageInfo;

    public RequestVo2() {

    }

    public RequestVo2(T condition) {
        this.condition = condition;
    }

    public T getCondition() {
        return condition;
    }

    public void setCondition(T condition) {
        this.condition = condition;
    }

    public PageListVo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageListVo pageInfo) {
        this.pageInfo = pageInfo;
    }
}
