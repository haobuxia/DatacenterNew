package com.tianyi.datacenter.access.util;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.access.model.DataCenterParam;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tianxujin on 2019/6/18 14:28
 */
public class DSParamDsBuilder{
    DataCenterParam dcp;
    Map<String, Object> conditionMap = new HashMap<>();
    Map<String, Integer> pageMap = new HashMap<>();

    public DSParamDsBuilder(Integer dataObjectId){
        dcp = new DataCenterParam();
        dcp.setDataObjectId(dataObjectId);
        dcp.setCondition(conditionMap);
        pageMap.put("page", 0);
        pageMap.put("pageSize", 0);//全是0，代表不分页
        dcp.setPageInfo(pageMap);
    }

    public DSParamDsBuilder buildDataObjectId(Integer dataObjectId) {
        dcp.setDataObjectId(dataObjectId);
        return this;
    }

    public DSParamDsBuilder buildPageInfo(Integer page,Integer pageSize) {
        pageMap.put("page", page);
        pageMap.put("pageSize", pageSize);//全是0，代表不分页
        return this;
    }

    public DSParamDsBuilder buildCondition(String key, Object value) {
        conditionMap.put(key, value);
        return this;
    }

    public JSONObject build() {
        return (JSONObject)JSONObject.toJSON(dcp);
    }
}
