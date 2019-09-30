package com.tianyi.datacenter.resource.service;

import com.tianyi.datacenter.storage.vo.PageListVo;
import com.tianyi.datacenter.storage.vo.RequestVo;

import java.util.Map;

public interface ResourceService {

    /**
     * 整合对象数据
     */
    RequestVo<Map> integrateData(Integer dataObjectId, String type, String isDic, String keyword, PageListVo pageListVo);
}
