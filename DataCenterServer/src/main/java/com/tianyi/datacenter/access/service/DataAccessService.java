package com.tianyi.datacenter.access.service;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.storage.vo.DataStorageDMLVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;

import java.util.List;

/**
 * 数据访问
 *
 * @author wenxinyan
 * @version 0.1
 */
public interface DataAccessService {

    /**
     * 整合对象数据
     *
     * @author wenxinyan
     * */
    RequestVo<DataStorageDMLVo> integrateData(DataObject dataObject, String operaType, int page, int pageSize,
                                              List<DataObjectAttribute> conditionAttributes,
                                              List<DataObjectAttribute> updateAttributes, JSONObject jsonObject);

    ResponseVo addData(JSONObject jsonObject) throws Exception;

    ResponseVo deleteData(JSONObject jsonObject) throws Exception;

    ResponseVo updateData(JSONObject jsonObject) throws Exception;

    ResponseVo queryData(JSONObject jsonObject) throws Exception;

    ResponseVo queryDataDic(JSONObject jsonObject) throws Exception;

    ResponseVo queryDataTree(JSONObject jsonObject) throws Exception;

    ResponseVo retrieveMaxId(JSONObject jsonObject) throws Exception;

    ResponseVo checkReference(JSONObject jsonObject) throws Exception;
}
