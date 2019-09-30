package com.tianyi.datacenter.inspect.controller;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Api("车辆档案操作接口")
@RestController
@RequestMapping("inspect/archives")
@Slf4j
public class CarArchivesController {
    @Autowired
    private DataCenterFeignService dataCenterFeignService;

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseVo search(@RequestBody Map<String, Object> param) {

        Map<String, Object> condition = (Map<String, Object>) param.get("condition");
        Map<String, Integer> pageInfo = (Map<String, Integer>) param.get("pageInfo");

        List<Map<String, Object>> resultList = new ArrayList<>();
        JSONObject jsonObjectFinalResult = new JSONObject();

        List<String> stations = new ArrayList<>();
        stations.add(0, "台上检查");
        stations.add(1, "入库检查");
        stations.add(2, "出荷检查");

        JSONObject jsonObjectSelect = new JSONObject();
        jsonObjectSelect.put("dataObjectId", 37);
        //{"condition":{"jihao":"DBBJT8","modelName":"PC200-8","typeName":"挖掘机",
        // "endTime":"2019-07-01 11:13:13","startTime":"2019-06-01 11:13:13",
        // "producer":"我","status":"已下线"},
        // "dataObjectId":37,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":20}}
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("jihao", (String) condition.get("jihao"));
        conditionMap.put("modelName", (String) condition.get("modelName"));
        conditionMap.put("typeName", (String) condition.get("typeName"));
        conditionMap.put("endTime", (String) condition.get("endTime"));
        conditionMap.put("startTime", (String) condition.get("startTime"));
        conditionMap.put("producer", (String) condition.get("producer"));
        conditionMap.put("status", (String) condition.get("status"));
        jsonObjectSelect.put("condition", conditionMap);
        Map<String, Integer> pageMap = new HashMap<>();
        pageMap.put("page", pageInfo.get("page"));
        pageMap.put("pageSize", pageInfo.get("pageSize"));//全是0，代表不分页
        jsonObjectSelect.put("pageInfo", pageMap);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(jsonObjectSelect);
        if (responseVo.isSuccess()&&responseVo.getMessage()==null) {
            log.info("车辆档案基本信息查询成功");
            if (responseVo.getData().get("rtnData") != null) {
                List<Map> rtnDataList = (List<Map>) responseVo.getData().get("rtnData");
                if (rtnDataList.size() != 0) {
                    for (Map rtnData : rtnDataList) {
                        Map<String, Object> returnMap = new HashMap<>();
                        for (String station : stations) {
                            returnMap.put("status", rtnData.get("status"));
                            returnMap.put("typeName", rtnData.get("typeName"));
                            returnMap.put("modelName", rtnData.get("modelName"));
                            returnMap.put("jihao", rtnData.get("cid"));
                            returnMap.put("producer", rtnData.get("producer"));
                            returnMap.put("downTime", rtnData.get("downTime"));
                            returnMap.put("privateOrder", rtnData.get("privateOrder"));
                            returnMap.put("deptName", rtnData.get("deptName"));

                            //查询检查人
                            JSONObject jsonObjectSelectProducer = new JSONObject();
                            jsonObjectSelectProducer.put("dataObjectId", 40);
                            Map<String, Object> conditionMapProducer = new HashMap<>();
                            conditionMapProducer.put("stationName", station);
                            conditionMapProducer.put("jihao", rtnData.get("cid"));
                            conditionMapProducer.put("modelName", rtnData.get("modelName"));
                            jsonObjectSelectProducer.put("condition", conditionMapProducer);
                            Map<String, Integer> pageMapChecker = new HashMap<>();
                            pageMapChecker.put("page",0);
                            pageMapChecker.put("pageSize",0);
                            jsonObjectSelectProducer.put("pageInfo", pageMapChecker);
                            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoProducer = dataCenterFeignService.retrieve(jsonObjectSelectProducer);
                            StringBuffer producer = new StringBuffer();
                            producer.append("");
                            //检查人重复去重
                            if (responseVoProducer.isSuccess()) {
                                List<Map> rtnDataChecker = (List<Map>) responseVoProducer.getData().get("rtnData");
                                for (int i = 0; i < rtnDataChecker.size(); i++) {
                                    String checker = (String) rtnDataChecker.get(i).get("checker");
                                    if (i <= rtnDataChecker.size() - 2) {
                                        if (i == 0) {
                                            if (i == rtnDataChecker.size() - 1) {
                                                if (checker == null || checker == "") {
                                                    producer.append("");
                                                } else {
                                                    producer.append(checker);
                                                }
                                            } else {
                                                if (checker == null || checker == "") {
                                                    producer.append("");
                                                } else {
                                                    producer.append(checker + ",");
                                                }
                                            }
                                        } else {
                                            String checker2 = (String) rtnDataChecker.get(i + 1).get("checker");
                                            if (checker.equalsIgnoreCase(checker2)) {
                                                continue;
                                            }
                                            if (checker == null || checker == "") {
                                                producer.append("");
                                            } else {
                                                producer.append(checker + ",");
                                            }
                                        }
                                    }else {
                                        //只有一个检查项
                                        if (checker == null || checker == "") {
                                            producer.append("");
                                        } else {
                                            producer.append(checker);
                                        }
                                    }
                                }
                                //考虑下标越界问题
                                if (rtnDataChecker.size()>=2){
                                    //比较最后两个
                                    String checker = (String) rtnDataChecker.get(rtnDataChecker.size() - 2).get("checker");
                                    String lastchecker = (String) rtnDataChecker.get(rtnDataChecker.size() - 1).get("checker");
                                    if (checker != null && lastchecker != null) {
                                        if (checker.equalsIgnoreCase(lastchecker)) {
                                        } else {
                                            producer.append(lastchecker);
                                        }

                                    }
                                }
                                if (producer.lastIndexOf(",") > 0) {
                                    String temp = producer.toString();
                                    producer = new StringBuffer(temp.substring(0, temp.length() - 1));
                                }
                            }
                            if (station.equals("台上检查")) {
                                returnMap.put("onProducer", producer.toString());
                            }
                            if (station.equals("入库检查")) {
                                returnMap.put("inProducer", producer.toString());
                            }
                            if (station.equals("出荷检查")) {
                                returnMap.put("outProducer", producer.toString());
                            }
                            //查询检查单号
                            //{"condition":{"jihao":"1"},"dataObjectId":65,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":14}}
                            DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(65);
                            dsParamDsBuilder.buildCondition("jihao",rtnData.get("cid"));
                            dsParamDsBuilder.buildCondition("modelName",rtnData.get("modelName"));
                            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
                            List<Map<String, String>> rtnData1 = (List<Map<String, String>>) retrieve.getData().get("rtnData");

                            List<String> cid = new ArrayList<>();
                            if (rtnData1.size()>0&&rtnData1!=null){
                                for (Map<String, String> stringStringMap : rtnData1) {
                                    cid.add(stringStringMap.get("cid"));
                                }
                            }
                            returnMap.put("coid",cid);
                            //查询开始结束时间
                            JSONObject jsonObjectSelectTime = new JSONObject();
                            jsonObjectSelectTime.put("dataObjectId", 38);
                            Map<String, Object> conditionMapTime = new HashMap<>();
                            conditionMapTime.put("stationName", station);
                            conditionMapTime.put("jihao", rtnData.get("cid"));
                            conditionMapTime.put("modelName", rtnData.get("modelName"));
                            jsonObjectSelectTime.put("condition", conditionMapTime);
                            Map<String, Integer> pageMapTime = new HashMap<>();
                            pageMapTime.put("page",0);
                            pageMapTime.put("pageSize",0);
                            jsonObjectSelectTime.put("pageInfo", pageMapTime);
                            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoTime = dataCenterFeignService.retrieve(jsonObjectSelectTime);
                            if (responseVoTime.getData().get("rtnData") != null) {
                                List<Map> rtnDataTime = (List<Map>) responseVoTime.getData().get("rtnData");
                                if (rtnDataTime.size() != 0) {
                                    if (station.equals("台上检查")) {
                                        returnMap.put("onStart", rtnDataTime.get(0).get("onStart"));
                                        returnMap.put("onEnd", rtnDataTime.get(0).get("onEnd"));
                                    }
                                    if (station.equals("入库检查")) {
                                        returnMap.put("inStart", rtnDataTime.get(0).get("onStart"));
                                        returnMap.put("inEnd", rtnDataTime.get(0).get("onEnd"));
                                    }
                                    if (station.equals("出荷检查")) {
                                        returnMap.put("outStart", rtnDataTime.get(0).get("onStart"));
                                        returnMap.put("outEnd", rtnDataTime.get(0).get("onEnd"));
                                    }
                                } else {
                                    return ResponseVo.success(jsonObjectFinalResult, "查询数据为空");
                                }
                            } else {
                                return ResponseVo.success(jsonObjectFinalResult,"查询数据为空");
                            }
                        }
                        resultList.add(returnMap);
                    }
                    Map<String, Integer> pageInfoSearch = (Map<String, Integer>) responseVo.getData().get("pageInfo");
                    Integer total = pageInfoSearch.get("total");
                    pageInfo.put("page",(Integer) pageInfo.get("page"));
                    pageInfo.put("pageSize",(Integer) pageInfo.get("pageSize"));
                    pageInfo.put("total",total);
                    jsonObjectFinalResult.put("rtnData", resultList);
                    jsonObjectFinalResult.put("pageInfo",pageInfo);
                    return ResponseVo.success(jsonObjectFinalResult);
                }
                return ResponseVo.success(jsonObjectFinalResult, "查询数据为空");
            }
            return ResponseVo.success(jsonObjectFinalResult, "查询数据为空");
        }
        return ResponseVo.success(jsonObjectFinalResult,responseVo.getMessage());
    }
}

