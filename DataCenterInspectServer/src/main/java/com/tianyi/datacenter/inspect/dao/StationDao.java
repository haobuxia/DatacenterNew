package com.tianyi.datacenter.inspect.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tianyi.datacenter.inspect.entity.KStationcheck;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface StationDao extends BaseMapper<KStationcheck> {

    List<Map<String,String>> selectBystationId(String stationId);

    int deleteBychecktypeId(String checktypeid);
}
