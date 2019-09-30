package com.tianyi.datacenter.inspect.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import com.tianyi.datacenter.inspect.service.OperateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @author liulele 2019/9/26
 * @version 0.1
 **/
@Service
public class OperateServiceImpl implements OperateService {
    @Autowired
    private DataCenterFeignService dataCenterFeignService;
    @Override
    public ResponseVo getOperateOrders(String orderId) {
        if (ObjectUtil.isNull(orderId)){
            return ResponseVo.fail("您传递的orderId为空,请传入正确的参数");
        }
        return getModelIdsByOrderId(orderId);
    }
    private ResponseVo getModelIdsByOrderId(String s) {
        //1.查询 order
        DSParamBuilder dsParamBuilderOrder = new DSParamBuilder(71);
        dsParamBuilderOrder.buildCondition("oid", "equals", s);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveOrder = dataCenterFeignService.retrieve
                (dsParamBuilderOrder.build());
        if (retrieveOrder.isSuccess() && retrieveOrder.getMessage() == null) {
            //查询业务系统查询数据集
            DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(96);
            dsParamDsBuilder.buildCondition("orderId", s);
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
            if (retrieve.isSuccess() && retrieve.getMessage() == null) {
                return ResponseVo.success((List<Map<String, Object>>) retrieve.getData().get("rtnData"));
            }
        }
        //2.查询checkorder
        DSParamBuilder dsParamBuilderCheckOrder = new DSParamBuilder(16);
        dsParamBuilderCheckOrder.buildCondition("cid", "equals", s);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckOrder = dataCenterFeignService.retrieve
                (dsParamBuilderCheckOrder.build());
        if (retrieveCheckOrder.isSuccess() && retrieveCheckOrder.getMessage() == null) {
            //查询业务系统查询数据集
            DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(97);
            dsParamDsBuilder.buildCondition("orderId", s);
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
            if (retrieve.isSuccess() && retrieve.getMessage() == null) {
                return ResponseVo.success((List<Map<String, Object>>) retrieve.getData().get("rtnData"));
            }
        }
        //3.查询checkorderitem
        DSParamBuilder dsParamBuilderCheckOrderItem = new DSParamBuilder(17);
        dsParamBuilderCheckOrderItem.buildCondition("cid", "equals", s);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckOrderItem = dataCenterFeignService.retrieve
                (dsParamBuilderCheckOrderItem.build());
        if (retrieveCheckOrderItem.isSuccess() && retrieveCheckOrderItem.getMessage() == null) {
            //查询业务系统查询数据集
            DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(98);
            dsParamDsBuilder.buildCondition("orderId", s);
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
            if (retrieve.isSuccess() && retrieve.getMessage() == null) {
                return ResponseVo.success((List<Map<String, Object>>) retrieve.getData().get("rtnData"));
            }
        }
        return ResponseVo.success();
    }
}
