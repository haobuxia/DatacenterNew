package com.tianyi.datacenter.resource.dao;

import com.tianyi.datacenter.resource.entity.DataObjectType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by tianxujin on 2019/4/3 14:58
 */
@Repository
public interface DataObjectTypeDao {
    int insert(DataObjectType dataObjectType);

    int delete(int id);

    int update(DataObjectType dataObjectType);

    List<DataObjectType> listBy(Map<String, Object> param);

    int countBy(Map<String, Object> param);

    List<DataObjectType> listByNoPage(Map<String, Object> param);

    DataObjectType getById(int id);
}
