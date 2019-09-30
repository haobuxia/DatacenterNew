package com.tianyi.datacenter.resource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.common.util.TianYuanIntesrvApiHelper;
import com.tianyi.datacenter.resource.dao.DataObjectDao;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.service.DataObjectAttributeService;
import com.tianyi.datacenter.resource.service.DataObjectService;
import com.tianyi.datacenter.storage.entity.DdlTypeEnum;
import com.tianyi.datacenter.storage.factory.DataStorageDDLFactory;
import com.tianyi.datacenter.storage.service.DataStorageDDLService;
import com.tianyi.datacenter.storage.vo.PageListVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据对象服务实现
 *
 * @author wenxinyan
 * @version 0.1
 */
@Service
public class DataObjectServiceImpl implements DataObjectService {

    @Autowired
    private DataObjectDao dataObjectDao;

    @Autowired
    private DataStorageDDLFactory dataStorageDDLFactory;
    @Autowired
    private DataObjectAttributeService dataObjectAttributeService;
    @Autowired
    private TianYuanIntesrvApiHelper tianYuanIntesrvApiHelper;
    Logger logger = LoggerFactory.getLogger(DataObjectServiceImpl.class);
    @Override
    @Transactional
    public int insert(DataObject dataObject) {
        try {
            DataStorageDDLService dataStorageDDLService = dataStorageDDLFactory.getDDLService(DdlTypeEnum.table);
            dataStorageDDLService.doServer(dataStorageDDLService.getRequestVo("C", dataObject, null));
        } catch (DataCenterException e) {
            logger.error(e.toString());
        }
        return dataObjectDao.insert(dataObject);
    }

    @Override
    @Transactional
    public int delete(int id) {
        DataObject dataObject = this.getById(id);
        if("对象".equals(dataObject.getType())){// 不执行删表操作，修改表名
            try {
                DataStorageDDLService dataStorageDDLService = dataStorageDDLFactory.getDDLService(DdlTypeEnum.table);
                dataObject.setOldDefined(dataObject.getDefined());
                dataObject.setDefined("del_"+dataObject.getDefined());
                dataStorageDDLService.doServer(dataStorageDDLService.getRequestVo("R", dataObject, null));
            } catch (DataCenterException e) {
                logger.error(e.toString());
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("resId", id);
        dataObjectAttributeService.delete(map);
        return dataObjectDao.delete(id);
    }

    @Override
    @Transactional
    public int update(DataObject dataObject) {
        if("对象".equals(dataObject.getType())){
            try {
                DataStorageDDLService dataStorageDDLService = dataStorageDDLFactory.getDDLService(DdlTypeEnum.table);
                DataObject oldDataObject = this.getById(dataObject.getId());
                if(!(oldDataObject.getDefined().equalsIgnoreCase(dataObject.getDefined()))){
                    dataObject.setOldDefined(oldDataObject.getDefined());
                    dataStorageDDLService.doServer(dataStorageDDLService.getRequestVo("R", dataObject, null));
                }
            } catch (DataCenterException e) {
                logger.error(e.toString());
            }
        }
        return dataObjectDao.update(dataObject);
    }

    @Override
    /*
     * @author xiayuan
     * @version 0.1
     * @description //TODO
     * @date
     * @param [requestVo]
     * @return java.lang.Object
     */
    public ResponseVo list(RequestVo<Map> requestVo) throws DataCenterException {
        PageListVo pageInfo = requestVo.getPageInfo();
        Map<String, Object> param = PageListVo.createParamMap(pageInfo.getPage(), pageInfo.getPageSize());
        param.putAll(requestVo.getRequest());

        List<DataObject> dataObjectList = dataObjectDao.listBy(param);
        int count = dataObjectDao.countBy(param);

        pageInfo.setTotal(count);

        Map<String, Object> result = new HashMap<>();
        result.put("pageInfo", pageInfo);
        result.put("list", dataObjectList);

        ResponseVo responseVo = ResponseVo.success(result);

        return responseVo;
    }

    @Override
    public DataObject getById(int id){
        return dataObjectDao.getById(id);
    }

    @Override
    public List<DataObject> listNoPage(Map<String, Object> param){
        return dataObjectDao.listByNoPage(param);
    }

    @Override
    public List<Map<String, Object>> listDwf() {
        JSONObject jo = tianYuanIntesrvApiHelper.getResult("meta/class-names-min");
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        JSONObject jsonObject = jo.getJSONObject("data");
        for (String key : jsonObject.keySet()) {
            Map dataObject = new HashMap();
            dataObject.put("id", key);
            dataObject.put("name", jsonObject.getString(key));
            list.add(dataObject);
        }
        return list;
    }
}
