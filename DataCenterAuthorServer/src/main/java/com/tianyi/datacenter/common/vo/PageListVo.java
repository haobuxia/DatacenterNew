package com.tianyi.datacenter.common.vo;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

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
    private int pageTotal;
    private int flag;//仅用在工单管理查询视频列表的时候

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

    public int getPageCount() {
        return total / pageSize + (total % pageSize == 0 ? 0 : 1);
    }

    public boolean hasPrePage() {
        return page > 1;
    }

    public boolean hasNextPage() {
        return page < getPageCount();
    }

    public int[] getPageRange() {
        int pageCount = getPageCount();
        if (pageCount <= 10) {
            return IntStream.range(1, pageCount + 1).toArray();
        } else {
            //总页数pageCount > 10
            if (page <= 5) {
                return IntStream.range(1, 10).toArray();
            } else {
                if (page < pageCount - 5) {
                    return IntStream.range(page - 5, page + 5).toArray();
                } else {
                    return IntStream.range(page - 5, pageCount + 1).toArray();
                }
            }
        }
    }

    public int getPageTotal() {
        return getPageCount();
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
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

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
