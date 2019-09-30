package com.tianyi.datacenter.storage.service;


import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.storage.vo.DataStorageDMLVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;

/**
 * 数据存储模块执行DML命令服务接口
 * 2018/11/15 19:46
 * @author zhouwei
 * @version 0.1
 */
public interface DataStorageDMLService {
    ResponseVo doServer(RequestVo<DataStorageDMLVo> requestVo)throws DataCenterException;
}
