package com.tianyi.datacenter.inspect.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.jsoup.helper.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Api("个人桌面相关接口")
@RestController
@RequestMapping("inspect/desktop")
public class DesktopController {
    @Autowired
    private DataCenterFeignService dataCenterFeignService;

    @RequestMapping("manageCarData")
    public ResponseVo manageCarData() {
        //{"condition":{},"dataObjectId":53,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":10}}
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(53);
        //查询条件   本周1到本周日
        Date date = new Date();
        DateTime beginOfWeek = DateUtil.beginOfWeek(date);
        DateTime endOfWeek = DateUtil.endOfWeek(date);
        dsParamDsBuilder.buildCondition("downTimeA", beginOfWeek);
        dsParamDsBuilder.buildCondition("downTimeB", endOfWeek);
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
        dsParamDsBuilder.buildCondition("downTimeA", beginOfWeek);
        dsParamDsBuilder.buildCondition("downTimeB", endOfWeek);
        com.tianyi.datacenter.feign.common.vo.ResponseVo ResponseVoProblemData = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        List<Map<String, Object>> rtnData = (List<Map<String, Object>>) ResponseVoProblemData.getData().get("rtnData");
        return ResponseVo.success(rtnData);
    }

    @RequestMapping("search")
    public ResponseVo search(@RequestBody Map<String, Object> param) {
        Map<String, String> condition = (Map<String, String>) param.get("condition");
        //已下线的时候返回的不一样
        String carStatus = (String) condition.get("carStatus");
        Map data = new HashMap();
        Map<String, Integer> pageInfo = (Map<String, Integer>) param.get("pageInfo");
        if (carStatus != null) {
            if (carStatus.equalsIgnoreCase("已下线")) {
                DSParamBuilder dsParamBuilder = new DSParamBuilder(10);
                dsParamBuilder.buildCondition("status", "equals", "已下线");
                if ( condition.get("startDate")!=null&& condition.get("endDate")!=null) {
                    dsParamBuilder.buildCondition("downTime", "greate than", (String) condition.get("startDate"));
                    dsParamBuilder.buildCondition("status", "less than", (String) condition.get("endDate"));
                }
                dsParamBuilder.buildPageInfo(pageInfo.get("page"), pageInfo.get("pageSize"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo ResponseVo = dataCenterFeignService.retrieve(dsParamBuilder.build());
                data = ResponseVo.getData();
                data.put("pageInfo",pageInfo);
            } else {
                DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(66);
                dsParamDsBuilder.buildCondition("startTime", (String) condition.get("startDate"));
                dsParamDsBuilder.buildCondition("endTime", (String) condition.get("endDate"));
                dsParamDsBuilder.buildCondition("carStatus", (String) condition.get("carStatus"));
                dsParamDsBuilder.buildPageInfo(pageInfo.get("page"), pageInfo.get("pageSize"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo ResponseVo = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
                data = ResponseVo.getData();
                data.put("pageInfo",pageInfo);
            }
        } else {
            DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(66);
            dsParamDsBuilder.buildCondition("startTime", (String) condition.get("startDate"));
            dsParamDsBuilder.buildCondition("endTime", (String) condition.get("endDate"));
            dsParamDsBuilder.buildCondition("carStatus", (String) condition.get("carStatus"));
            dsParamDsBuilder.buildPageInfo(pageInfo.get("page"), pageInfo.get("pageSize"));
            com.tianyi.datacenter.feign.common.vo.ResponseVo ResponseVo = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
            if (ResponseVo.isSuccess() && ResponseVo.getMessage() == null) {
                data = ResponseVo.getData();
            }
            DSParamBuilder dsParamBuilder = new DSParamBuilder(10);
            dsParamBuilder.buildCondition("status", "equals", "已下线");
            if ( condition.get("startDate")!=null&& condition.get("endDate")!=null) {
                dsParamBuilder.buildCondition("downTime", "greate than", (String) condition.get("startDate"));
                dsParamBuilder.buildCondition("status", "less than", (String) condition.get("endDate"));
            }
            dsParamBuilder.buildPageInfo(pageInfo.get("page"), pageInfo.get("pageSize"));
            com.tianyi.datacenter.feign.common.vo.ResponseVo ResponseVo1 = dataCenterFeignService.retrieve(dsParamBuilder.build());
            List<Map<String, Object>> list = (List<Map<String, Object>>) ResponseVo1.getData().get("rtnData");
            List<Map<String, Object>> list1 = (List<Map<String, Object>>) data.get("rtnData");
            if (list1!=null&&list1.size()>0){
                for (Map<String, Object> map : list) {
                    list1.add(map);
                }
                data.put("rtnData",list1);
                Map<String, Integer> pageInfo1 = (Map<String, Integer>) data.get("pageInfo");
                pageInfo1.put("total",list1.size());
            }else {
                Map data2 = new HashMap();
                data2.put("rtnData",list);
                if (list.size()==0){
                    pageInfo.put("total",0);
                    data2.put("pageInfo",pageInfo);
                }else {
                    data2.put("pageInfo",ResponseVo1.getData().get("pageInfo"));
                    return com.tianyi.datacenter.common.vo.ResponseVo.success(data2);
                }
            }
        }
        return ResponseVo.success(data);
    }

    @RequestMapping("cardata")
    public ResponseVo cardata(@RequestBody Map<String, Object> param) {
        Map<String, String> condition = (Map<String, String>) param.get("condition");
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> finalMap = new HashMap<>();
        String machinetypeId = condition.get("machinetypeId");
        String modelId = condition.get("modelId");
        String downTimeA = condition.get("downTimeA");
        String downTimeB = condition.get("downTimeB");
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(53);
        dsParamDsBuilder.buildCondition("machinetypeId", machinetypeId);
        dsParamDsBuilder.buildCondition("modelId", modelId);
        dsParamDsBuilder.buildCondition("downTimeA", downTimeA);
        dsParamDsBuilder.buildCondition("downTimeB", downTimeB);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        if (retrieve.isSuccess() && retrieve.getMessage() == null) {
            List<Map<String, Object>> rtnData = (List<Map<String, Object>>) retrieve.getData().get("rtnData");
            Map<String, Object> stringIntegerMap = rtnData.get(0);
            //{"totalNum":0,"finishNum":null,"waitNum":null,"checkingNum":null,"check1Num":null,"check2Num":null,"check3Num":null,"repairNum":null}
            if (stringIntegerMap.get("totalNum") == null) {
                stringIntegerMap.put("totalNum", 0);
            }
            if (stringIntegerMap.get("finishNum") == null) {
                stringIntegerMap.put("finishNum", 0);
            }
            if (stringIntegerMap.get("waitNum") == null) {
                stringIntegerMap.put("waitNum", 0);
            }
            if (stringIntegerMap.get("checkingNum") == null) {
                stringIntegerMap.put("checkingNum", 0);
            }
            if (stringIntegerMap.get("check1Num") == null) {
                stringIntegerMap.put("check1Num", 0);
            }
            if (stringIntegerMap.get("check2Num") == null) {
                stringIntegerMap.put("check2Num", 0);
            }
            if (stringIntegerMap.get("check3Num") == null) {
                stringIntegerMap.put("check3Num", 0);
            }
            if (stringIntegerMap.get("repairNum") == null) {
                stringIntegerMap.put("repairNum", 0);
            }
            result.add(stringIntegerMap);
            finalMap.put("rtnData", result);
        }
        return ResponseVo.success(finalMap);
    }

    @RequestMapping("problem")
    public ResponseVo problem() {
        Map<String, Object> finalMap = new HashMap<>();
        Date date = new Date();
        DateTime downTimea = DateUtil.beginOfMonth(date);
        DateTime downTimeb = DateUtil.endOfMonth(date);
        String downTimeA = downTimea.toString();
        String downTimeB = downTimeb.toString();
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(53);
        dsParamDsBuilder.buildCondition("downTimeA", downTimeA);
        dsParamDsBuilder.buildCondition("downTimeB", downTimeB);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        return ResponseVo.success(retrieve.getData());
    }

    @RequestMapping("history")
    public ResponseVo history(@RequestBody Map<String, Object> param) {
//{"dataObjectId":59,"condition":{"startTime":"2019-07-01 00:00:00","endTime":"2019-07-01 00:00:00","jihao":"809"},"pageInfo":{"page":1,"pageSize":1,"total":0},"userId":"99","menuId":"2019071100002"}
        Map<String, Integer> pageInfo = (Map<String, Integer>) param.get("pageInfo");
        Map<String, String> condition = (Map<String, String>) param.get("condition");
        String startTime = condition.get("startTime");
        String endTime = condition.get("endTime");
        String jihao = condition.get("jihao");
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = new com.tianyi.datacenter.feign.common.vo.ResponseVo();
        if (startTime == null) {
            DateTime downTimea = DateUtil.parse("0000-00-00 00:00:00", "0000-00-00 00:00:00");
            DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(59);
            dsParamDsBuilder.buildCondition("startTime", downTimea.toString());
            dsParamDsBuilder.buildCondition("endTime", endTime);
            dsParamDsBuilder.buildCondition("jihao", jihao);
            dsParamDsBuilder.buildPageInfo(pageInfo.get("page"),pageInfo.get("pageSize"));
            retrieve = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        } else {
            DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(59);
            dsParamDsBuilder.buildCondition("startTime", startTime);
            dsParamDsBuilder.buildCondition("endTime", endTime);
            dsParamDsBuilder.buildCondition("jihao", jihao);
            dsParamDsBuilder.buildPageInfo(pageInfo.get("page"),pageInfo.get("pageSize"));
            retrieve = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        }
        return ResponseVo.success(retrieve.getData());
    }
}
