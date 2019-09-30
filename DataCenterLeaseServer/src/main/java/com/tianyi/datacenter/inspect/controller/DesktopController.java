package com.tianyi.datacenter.inspect.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Api("个人桌面相关接口")
@RestController
@RequestMapping("inspect/desktop")
public class DesktopController {
    @Autowired
    private  DataCenterFeignService dataCenterFeignService;

    @RequestMapping("manageCarData")
    public ResponseVo manageCarData() {
        //{"condition":{},"dataObjectId":53,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":10}}
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(53);
        //查询条件   本周1到本周日
        Date date = new Date();
        DateTime beginOfWeek = DateUtil.beginOfWeek(date);
        DateTime endOfWeek = DateUtil.endOfWeek(date);
        dsParamDsBuilder.buildCondition("downTimeA",beginOfWeek);
        dsParamDsBuilder.buildCondition("downTimeB",endOfWeek);
        com.tianyi.datacenter.feign.common.vo.ResponseVo ResponseVoCarData = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        List<Map<String, Object>> rtnData = (List<Map<String, Object>>) ResponseVoCarData.getData().get("rtnData");
        return ResponseVo.success(rtnData);
    }
    @RequestMapping("manageProblemData")
    public ResponseVo manageProblemData() {
        //{"condition":{},"dataObjectId":55,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":10}}
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(55);
        //查询条件   本周1到本周日
        Date date = new Date();
        DateTime beginOfWeek = DateUtil.beginOfWeek(date);
        DateTime endOfWeek = DateUtil.endOfWeek(date);
        dsParamDsBuilder.buildCondition("downTimeA",beginOfWeek);
        dsParamDsBuilder.buildCondition("downTimeB",endOfWeek);
        com.tianyi.datacenter.feign.common.vo.ResponseVo ResponseVoProblemData = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        List<Map<String, Object>> rtnData = (List<Map<String, Object>>) ResponseVoProblemData.getData().get("rtnData");
        return ResponseVo.success(rtnData);
    }
}
