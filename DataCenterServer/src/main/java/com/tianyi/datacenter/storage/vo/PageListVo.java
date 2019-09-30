package com.tianyi.datacenter.storage.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * 分页数据对象
 *
 * @author wenxinyan
 * @version 0.1
 */
public class PageListVo {
    private int page;
    private int pageSize;
    private int total;

    public static final int DEFAULT_PAGE_SIZE = 12;

    public static Map<String, Object> createParamMap(Integer page) {
        return createParamMap(page,DEFAULT_PAGE_SIZE);
    }

    public static Map<String, Object> createParamMap(Integer page, Integer pageSize) {
        if (page == null) page = 1;
        if (pageSize == null) pageSize = DEFAULT_PAGE_SIZE;

        Map<String, Object> map =  new HashMap<>();
        map.put("start", (page - 1) * pageSize);
        map.put("length", pageSize);

        return map;
    }

    public PageListVo() {
        this(1);
    }

    public PageListVo(int page) {
        this(page,DEFAULT_PAGE_SIZE);
    }

    public PageListVo(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public PageListVo(int page, int pageSize, int total) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
