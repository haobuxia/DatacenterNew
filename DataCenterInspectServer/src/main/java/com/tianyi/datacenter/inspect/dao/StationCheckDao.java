package com.tianyi.datacenter.inspect.dao;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository
public interface StationCheckDao {

    List<Map<String,Object>> searchByPage(Map<String,Object> param);
    List<Map<String,Object>> searchNoPage(Map<String,Object> param);

    int countBy(Map<String,Object> param);
}
