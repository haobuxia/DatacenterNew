package com.tianyi.datacenter.storage.service.impl;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.storage.service.DataStorageDDLService;
import com.tianyi.datacenter.storage.util.DBUtil;
import com.tianyi.datacenter.storage.util.DDLUtil;
import com.tianyi.datacenter.storage.vo.DataStorageDDLVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 执行DDL语句服务实现
 *
 * @author tianxujin
 * 2019/03/29
 * @version 0.1
 **/
@Service("tableDDLService")
public class DataStorageTableDDLServiceImpl implements DataStorageDDLService {

    @Autowired
    private DBUtil database;
    private DDLUtil ddlUtil = new DDLUtil();

    private Logger logger = LoggerFactory.getLogger(DataStorageTableDDLServiceImpl.class);

    @Override
    public RequestVo<DataStorageDDLVo> getRequestVo(String ddlType, DataObject dataObject, List<DataObjectAttribute> attributeList){
        DataStorageDDLVo dataStorageDDLVo = new DataStorageDDLVo();
        dataStorageDDLVo.setDdlType(ddlType);
        dataStorageDDLVo.setDataObject(dataObject);
        RequestVo<DataStorageDDLVo> requestVo = new RequestVo<>(dataStorageDDLVo);
        return requestVo;
    }

    @Override
    public ResponseVo doServer(RequestVo<DataStorageDDLVo> requestVo) {

        //校验服务接口
        DataStorageDDLVo ddlVo = requestVo.getRequest();
        ResponseVo validResult = validParam(ddlVo);
        if(!validResult.isSuccess()){
            //参数校验失败
            logger.error(validResult.getMessage());
            return validResult;
        }
        String ddlType = ddlVo.getDdlType();

        //创建ddl语句
        String ddlSql;
        try {
            ddlSql = ddlUtil.generateDDL(ddlVo);
        } catch (DataCenterException e) {
            logger.error("生成alter语句失败:\n"+e.getStackTrace());
            return ResponseVo.fail(e.getMessage());
        }

        //执行ddl语句
        try {
            database.executeDDL(ddlType, ddlSql);
        } catch (Exception e) {
            logger.error("执行alter语句失败:\n"+e.getStackTrace());
            return ResponseVo.fail("执行alter语句异常:"+e.getCause());
        }
        return ResponseVo.success();
    }

    /**
     * 服务参数校验方法
     * @author zhouwei
     * 2018/12/3 14:14
     * @param ddlVo ddl服务入参
     * @return ResponseVo.success为true表示校验成功
    */
    private ResponseVo validParam(DataStorageDDLVo ddlVo) {
        if(StringUtils.isEmpty(ddlVo.getDdlType())){
            return ResponseVo.fail("参数校验失败:操作类型不可为空");
        }
        if (ddlVo.getDataObject() == null) {
            return ResponseVo.fail("参数校验失败:数据对象不可为空");
        }
        if (StringUtils.isEmpty(ddlVo.getDataObject().getDefined())) {
            return ResponseVo.fail("参数校验失败:表名不可为空");
        }
        return ResponseVo.success();
    }
}
