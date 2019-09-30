package com.tianyi.datacenter.resource.service;


import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;

import java.util.List;
import java.util.Map;

/**
 * 数据对象服务接口
 *
 * @author wenxinyan
 * @version 0.1
 */
public interface DataObjectService {

    /**
     * 新增数据对象
     *
     * @author wenxinyan
     */
    int insert(DataObject dataObject);

    /**
     * 删除数据对象
     *
     * @author wenxinyan
     */
    int delete(int id);

    /**
     * 修改数据对象
     *
     * @author wenxinyan
     */
    int update(DataObject dataObject);

    /**
     * 分页查询数据对象
     *
     * @author wenxinyan
     */
    ResponseVo list(RequestVo<Map> requestVo) throws DataCenterException;

    /**
     * 根据id查询数据对象
     *
     * @author wenxinyan
     */
    DataObject getById(int id);

    /**
     * 无分页查询数据对象
     *
     * @author wenxinyan
     */
    List<DataObject> listNoPage(Map<String, Object> param);

    List<Map<String, Object>> listDwf();
}
