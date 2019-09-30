package com.tianyi.datacenter.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置freemarker信息对象
 * @author zhouwei
 * 2018/12/5 11:44
 * @version  0.1
 */
public class FreeMarkerRoot {
    private Map<String, Object> ftlRoot = new HashMap<>();

    public static FreeMarkerRoot build() {
        return new FreeMarkerRoot();
    }
    public Map<String, Object> getFtlRoot() {
        return ftlRoot;
    }

    public FreeMarkerRoot put(String key, Object value) {
        getFtlRoot().put(key, value);
        return this;
    }
}
