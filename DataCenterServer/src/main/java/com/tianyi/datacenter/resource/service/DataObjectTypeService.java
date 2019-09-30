package com.tianyi.datacenter.resource.service;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObjectType;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;

import java.util.List;
import java.util.Map; /**
 * Created by tianxujin on 2019/4/3 14:42
 */

public interface DataObjectTypeService {
    ResponseVo list(RequestVo<Map> requestVo) throws DataCenterException;

    DataObjectType getById(int id);

    List<DataObjectType> listNoPage(Map<String, Object> map);

    int update(DataObjectType dataObjectType);

    int delete(int id);

    int insert(DataObjectType dataObjectType);
}
