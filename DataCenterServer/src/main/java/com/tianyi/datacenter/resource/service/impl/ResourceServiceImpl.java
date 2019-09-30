package com.tianyi.datacenter.resource.service.impl;


import com.tianyi.datacenter.resource.service.ResourceService;
import com.tianyi.datacenter.storage.vo.PageListVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResourceServiceImpl implements ResourceService {
    @Override
    public RequestVo<Map> integrateData(Integer dataObjectId, String type, String isDic, String keyword, PageListVo pageListVo) {
        Map<String, Object> map = new HashMap<>();
        map.put("dataObjectId", dataObjectId);
        map.put("name", keyword);
        map.put("isDic",isDic);
        map.put("type",type);
        RequestVo<Map> requestVo = new RequestVo<>(map);
        if (pageListVo != null) {
            requestVo.setPageInfo(pageListVo);
        }
        return requestVo;
    }
}
