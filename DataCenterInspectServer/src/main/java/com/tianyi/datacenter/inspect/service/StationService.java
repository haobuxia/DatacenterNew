package com.tianyi.datacenter.inspect.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tianyi.datacenter.common.vo.RequestVo2;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.inspect.entity.KStationcheck;

import java.util.Map;

/**
 * 数据对象服务接口
 *
 * @author wenxinyan
 * @version 0.1
 */
public interface StationService extends IService<KStationcheck> {

    ResponseVo handelBatchIds(Map<String, Object> param);

    ResponseVo selectByPage(RequestVo2 requestVo2);

    int selectNoPage(Map<String, Object> param);

    ResponseVo saveOfBest(Map<String,Object> param);
}
