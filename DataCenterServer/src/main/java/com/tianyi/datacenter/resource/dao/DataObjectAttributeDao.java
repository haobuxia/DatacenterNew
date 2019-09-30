package com.tianyi.datacenter.resource.dao;

import com.tianyi.datacenter.resource.entity.DataObjectAttribute;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 数据对象属性
 *
 * @author wenxinyan
 * @version 0.1
 */
@Repository
public interface DataObjectAttributeDao {

    int insert(DataObjectAttribute dataObjectAttribute);

    int delete(Map<String, Object> param);

    int update(DataObjectAttribute dataObjectAttribute);

    List<DataObjectAttribute> listBy(Map<String, Object> param);

    int countBy(Map<String, Object> param);

    List<DataObjectAttribute> listByNoPage(Map<String, Object> param);

    DataObjectAttribute getById(int id);

}
