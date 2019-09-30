package com.tianyi.datacenter.resource.service;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;

import java.util.List;
import java.util.Map;

/**
 * 数据对象属性服务接口
 *
 * @author wenxinyan
 * @version 0.1
 */
public interface DataObjectAttributeService {

    /**
     * 新增数据对象属性
     *
     * @author wenxinyan
     */
    int insert(DataObjectAttribute dataObjectAttribute);

    /**
     * 删除数据对象属性
     *
     * @author wenxinyan
     */
    int delete(Map<String, Object> param);

    /**
     * 修改数据对象属性
     *
     * @author wenxinyan
     */
    int update(DataObjectAttribute dataObjectAttribute);

    /**
     * 分页查询数据对象属性
     *
     * @author wenxinyan
     */
    ResponseVo list(RequestVo<Map> requestVo) throws DataCenterException;

    /**
     * 无分页查询数据对象属性
     *
     * @author wenxinyan
     */
    List<DataObjectAttribute> listNoPage(Map<String, Object> param);

    /**
     * 查询数据对象属性（附带字典字段）
     *
     * @author wenxinyan
     */
    List<DataObjectAttribute> listAttributesIncludeDic(Map<String, Object> param);

    /**
     * 根据id查询数据对象属性
     *
     * @author wenxinyan
     */
    DataObjectAttribute getById(int id);

    List<DataObjectAttribute> listDwf(String attributeDwfUrl);

    List<DataObjectAttribute> listDb(String driver, String url, String user, String password, String tableName);
}
