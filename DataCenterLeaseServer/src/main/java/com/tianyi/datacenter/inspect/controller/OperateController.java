package com.tianyi.datacenter.inspect.controller;

import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.inspect.entity.User;
import com.tianyi.datacenter.inspect.service.OperateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liulele 2019/9/26
 * @version 0.1
 **/
@Api("业务系统查询工单接口")
@RestController
@RequestMapping("inspect")
public class OperateController {
    @Autowired
    private OperateService operateService;

    @PostMapping("orderSearch")
    @ApiIgnore
    public ResponseVo hello(
            @ApiParam(name = "orderId", value = "工单ID", required = true) @RequestParam String orderId) {
        ResponseVo operateOrders = operateService.getOperateOrders(orderId);
        return operateOrders;
    }

}
