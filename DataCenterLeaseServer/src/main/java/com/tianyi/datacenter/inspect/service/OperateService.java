package com.tianyi.datacenter.inspect.service;

import com.tianyi.datacenter.common.vo.ResponseVo;

/**
 * @author liulele 2019/9/26
 * @version 0.1
 **/
public interface OperateService {
    ResponseVo getOperateOrders(String orderId);
}
