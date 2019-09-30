package com.tianyi.datacenter.inspect.service;


import com.tianyi.datacenter.common.vo.ResponseVo;

import java.util.List;
import java.util.Map;

/**
 * 数据对象服务接口
 *
 * @author wenxinyan
 * @version 0.1
 */
public interface CheckOrderItemService  {


    ResponseVo search(Map<String,Object> param);

    ResponseVo resultSearch(Map<String,Object> param);

    ResponseVo save(List<Map<String, Object>> param);
}
