package com.tianyi.datacenter.feign.controller;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.feign.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 负载均衡
 *
 * @author wenxinyan
 * @version 0.1
 */
@RestController
@RequestMapping("feign")
public class WebController {

    @Autowired
    private DataCenterFeignService dataCenterFeignService;

    @RequestMapping(value="sayhello", method = RequestMethod.GET)
    public String sayHello(@RequestParam String name) {
        return dataCenterFeignService.hello(name);
    }

    @RequestMapping(value="saydata", method = RequestMethod.POST)
    public ResponseVo sayData(@RequestBody JSONObject jsonObject) {
        return dataCenterFeignService.retrieve(jsonObject);
    }

}
