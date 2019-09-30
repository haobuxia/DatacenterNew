package com.tianyi.datacenter.storage.service;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.storage.vo.DataStorageDDLVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;

import java.util.List;

/**
 * ddl操作服务接口
 *
 * @author zhouwei
 * 2018/11/21 18:50
 * @version 0.1
 **/
public interface DataStorageDDLService {

    RequestVo<DataStorageDDLVo> getRequestVo(String ddlType, DataObject dataObject, List<DataObjectAttribute> attributeList);

    ResponseVo doServer(RequestVo<DataStorageDDLVo> requestVo) throws DataCenterException;

}
