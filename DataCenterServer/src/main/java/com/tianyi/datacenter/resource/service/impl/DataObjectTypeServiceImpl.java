package com.tianyi.datacenter.resource.service.impl;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.dao.DataObjectTypeDao;
import com.tianyi.datacenter.resource.entity.DataObjectType;
import com.tianyi.datacenter.resource.service.DataObjectTypeService;
import com.tianyi.datacenter.storage.factory.DataStorageDDLFactory;
import com.tianyi.datacenter.storage.vo.PageListVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据对象服务实现
 *
 * @author tixnujin
 * @version 0.1
 */
@Service
public class DataObjectTypeServiceImpl implements DataObjectTypeService {

    @Autowired
    private DataObjectTypeDao dataObjectTypeDao;

    @Autowired
    private DataStorageDDLFactory dataStorageDDLFactory;

    Logger logger = LoggerFactory.getLogger(DataObjectTypeServiceImpl.class);

    @Override
    @Transactional
    public int insert(DataObjectType dataObjectType) {
        return dataObjectTypeDao.insert(dataObjectType);
    }

    @Override
    @Transactional
    public int delete(int id) {
        return dataObjectTypeDao.delete(id);
    }

    @Override
    @Transactional
    public int update(DataObjectType dataObjectType) {
        return dataObjectTypeDao.update(dataObjectType);
    }

    @Override
    public ResponseVo list(RequestVo<Map> requestVo) throws DataCenterException {
        PageListVo pageInfo = requestVo.getPageInfo();
        Map<String, Object> param = PageListVo.createParamMap(pageInfo.getPage(), pageInfo.getPageSize());
        param.putAll(requestVo.getRequest());

        List<DataObjectType> dataObjectTypeList = dataObjectTypeDao.listBy(param);
        int count = dataObjectTypeDao.countBy(param);

        pageInfo.setTotal(count);

        Map<String, Object> result = new HashMap<>();
        result.put("pageInfo", pageInfo);
        result.put("list", dataObjectTypeList);

        ResponseVo responseVo = ResponseVo.success(result);

        return responseVo;
    }

    @Override
    public DataObjectType getById(int id){
        return dataObjectTypeDao.getById(id);
    }

    @Override
    public List<DataObjectType> listNoPage(Map<String, Object> param){
        return dataObjectTypeDao.listByNoPage(param);
    }

}
