package com.tianyi.datacenter.access.util;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.access.model.DataCenterParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tianxujin on 2019/6/18 14:23
 */
public class DSParamBuilder {
    DataCenterParam dcp;
    List<Map<String, Object>> conditionList = new ArrayList<>();
    Map<String, Integer> pageMap = new HashMap<>();

    public DSParamBuilder(Integer dataObjectId){
        dcp = new DataCenterParam();
        dcp.setDataObjectId(dataObjectId);
        dcp.setCondition(conditionList);
        pageMap.put("page", 0);
        pageMap.put("pageSize", 0);//全是0，代表不分页
        dcp.setPageInfo(pageMap);
    }

    public DSParamBuilder buildDataObjectId(Integer dataObjectId) {
        dcp.setDataObjectId(dataObjectId);
        return this;
    }

    public DSParamBuilder buildPageInfo(Integer page,Integer pageSize) {
        pageMap.put("page", page);
        pageMap.put("pageSize", pageSize);//全是0，代表不分页
        return this;
    }

    public DSParamBuilder buildCondition(String key, String condition, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("condition", condition);
        map.put("value", value);
        conditionList.add(map);
        return this;
    }

    public DSParamBuilder buildData(Object data) {
        dcp.setData(data);
        return this;
    }

    public JSONObject build() {
        return (JSONObject)JSONObject.toJSON(dcp);
    }
}
