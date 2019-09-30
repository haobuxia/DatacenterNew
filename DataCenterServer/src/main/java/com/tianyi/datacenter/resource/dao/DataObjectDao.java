package com.tianyi.datacenter.resource.dao;

import com.tianyi.datacenter.resource.entity.DataObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 数据对象
 *
 * @author wenxinyan
 * @version 0.1
 */
@Repository
public interface DataObjectDao {

    int insert(DataObject dataObject);

    int delete(int id);

    int update(DataObject dataObject);

    List<DataObject> listBy(Map<String, Object> param);

    int countBy(Map<String, Object> param);

    List<DataObject> listByNoPage(Map<String, Object> param);

    DataObject getById(int id);

}
