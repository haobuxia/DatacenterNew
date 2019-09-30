package com.tianyi.datacenter.inspect.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import com.tianyi.datacenter.inspect.dao.CheckOrderItemDao;
import com.tianyi.datacenter.inspect.service.CheckOrderItemService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 检查单详情
 * 检查报告中的详情和车辆档案中的详情查询不一样前者是字符串  后者是 数组
 * @author liulele
 * @version 0.1
 */
@Service
@Transactional
@Slf4j
public class CheckOrderItemServiceImpl implements CheckOrderItemService {
    @Autowired
    private CheckOrderItemDao checkOrderItemDao;
    @Autowired
    private DataCenterFeignService dataCenterFeignService;

    @Override
    public ResponseVo search(Map<String, Object> param) {
        Object coidObject = (Object) param.get("coid");
        String endTime1 = (String) param.get("endTime");
        String startTime1 = (String) param.get("startTime");
        String checker1 = (String) param.get("checker");
        List<String> result1 = (List<String>) param.get("result");
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (coidObject instanceof String) {
            String coid = (String) coidObject;
            //无条件查询
            if (endTime1 == null && startTime1 == null && checker1==null && result1 == null) {
                Map data = new HashMap();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dataObjectId", 24);
                Map<String, Object> conditionMap = new HashMap<>();
                conditionMap.put("coid", coid);
                Map<String, Integer> pageMap = new HashMap<>();
                pageMap.put("page", 0);
                pageMap.put("pageSize", 0);//全是0，代表不分页
                jsonObject.put("pageInfo", pageMap);
                jsonObject.put("condition", conditionMap);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(jsonObject);
                if (responseVo.isSuccess() && responseVo.getMessage() == null) {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
                    for (Map<String, Object> map : list) {
                        resultList.add(map);
                    }
                }
                Map finalresult = new HashMap();
                finalresult.put("rtnData",resultList);
                return ResponseVo.success(finalresult);
            }


            //有条件
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("dataObjectId", 24);
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("coid", coid);
            String endTime = (String) param.get("endTime");
            if (endTime != null) {
                conditionMap.put("endTime", (String) param.get("endTime"));
            }
            String startTime = (String) param.get("startTime");
            if (startTime != null) {
                conditionMap.put("startTime", (String) param.get("startTime"));
            }
            String checker = (String) param.get("checker");
            if (checker != null) {
                conditionMap.put("checker", (String) param.get("checker"));
            }
            List<String> result = (List<String>) param.get("result");
            if (result != null && result.size() > 0) {
                StringBuffer stringBuffer = new StringBuffer();
                for (String res : result) {
                    stringBuffer.append(res);
                }
                String toString = stringBuffer.toString();
                if (result.size() <= 1) {
                    if (toString.contains("0")) {
                        conditionMap.put("result", 0);
                    }
                    if (toString.contains("1")) {
                        conditionMap.put("result", 1);
                    }
                }

            }
            Map data = new HashMap();
            jsonObject.put("condition", conditionMap);
            Map<String, Integer> pageMap = new HashMap<>();
            pageMap.put("page", 0);
            pageMap.put("pageSize", 0);//全是0，代表不分页
            jsonObject.put("pageInfo", pageMap);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(jsonObject);
            if (responseVo.isSuccess() && responseVo.getMessage() == null) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
                for (Map<String, Object> map : list) {
                    resultList.add(map);
                }
            }
            Map finalresult = new HashMap();
            finalresult.put("rtnData",resultList);
            return ResponseVo.success(finalresult);
        } else {
            //数组
            //无条件查询
            List<String> coid = (List<String>) coidObject;
            if (endTime1 == null && startTime1 == null && checker1==null && result1 == null) {
                Map data = new HashMap();
                for (String s : coid) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("dataObjectId", 24);
                    Map<String, Object> conditionMap = new HashMap<>();
                    conditionMap.put("coid", s);
                    Map<String, Integer> pageMap = new HashMap<>();
                    pageMap.put("page", 0);
                    pageMap.put("pageSize", 0);//全是0，代表不分页
                    jsonObject.put("pageInfo", pageMap);
                    jsonObject.put("condition", conditionMap);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(jsonObject);
                    if (responseVo.isSuccess() && responseVo.getMessage() == null) {
                        List<Map<String, Object>> list = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
                        for (Map<String, Object> map : list) {
                            resultList.add(map);
                        }
                    }
                }
                Map finalresult = new HashMap();
                finalresult.put("rtnData",resultList);
                return ResponseVo.success(finalresult);
            }

            //有条件
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("dataObjectId", 24);
            Map<String, Object> conditionMap = new HashMap<>();
            for (String s : coid) {
                conditionMap.put("coid", s);

                String endTime = (String) param.get("endTime");
                if (endTime != null) {
                    conditionMap.put("endTime", (String) param.get("endTime"));
                }
                String startTime = (String) param.get("startTime");
                if (startTime != null) {
                    conditionMap.put("startTime", (String) param.get("startTime"));
                }
                String checker = (String) param.get("checker");
                if (checker != null) {
                    conditionMap.put("checker", (String) param.get("checker"));
                }
                List<String> result = (List<String>) param.get("result");
                if (result != null && result.size() > 0) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (String res : result) {
                        stringBuffer.append(res);
                    }
                    String toString = stringBuffer.toString();
                    if (result.size() <= 1) {
                        if (toString.contains("0")) {
                            conditionMap.put("result", 0);
                        }
                        if (toString.contains("1")) {
                            conditionMap.put("result", 1);
                        }
                    }

                }
                Map data = new HashMap();
                jsonObject.put("condition", conditionMap);
                Map<String, Integer> pageMap = new HashMap<>();
                pageMap.put("page", 0);
                pageMap.put("pageSize", 0);//全是0，代表不分页
                jsonObject.put("pageInfo", pageMap);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(jsonObject);
                if (responseVo.isSuccess() && responseVo.getMessage() == null) {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
                    for (Map<String, Object> map : list) {
                        resultList.add(map);
                    }
                }
            }
            Map finalresult = new HashMap();
            finalresult.put("rtnData",resultList);
            return ResponseVo.success(finalresult);
        }
    }


    /**
     * 结果查询
     *
     * @param param
     * @return
     */
    @Override
    public ResponseVo resultSearch(Map<String, Object> param) {
        //  //{"condition":{},"dataObjectId":31,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":10}}
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dataObjectId", 31);
        Map<String, Object> conditionMap = new HashMap<>();
        jsonObject.put("condition", conditionMap);
        Map<String, Integer> pageMap = new HashMap<>();
        pageMap.put("page", 0);
        pageMap.put("pageSize", 0);//全是0，代表不分页
        jsonObject.put("pageInfo", pageMap);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(jsonObject);
        if (responseVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
            for (Map<String, Object> map : list) {
                String result = (String) param.get("result");
                if (result.contains("通过")) {
                    conditionMap.put("result", param.get("result"));
                }
                if (result.contains("不通过")) {
                    conditionMap.put("result", param.get("result"));
                }
                if (result.contains("已确认")) {
                    conditionMap.put("makesure", "not");
                }
            }
            return ResponseVo.success(list);
        }
        return ResponseVo.fail("查询失败" + responseVo.getMessage());
    }

    @Transactional
    public ResponseVo save(List<Map<String, Object>> params) {
        //检查明细ID
        String cid = (String) params.get(0).get("cid");
        DSParamBuilder dsParamBuilder = new DSParamBuilder(17);
        //根据检查明细查询检查单
        dsParamBuilder.buildCondition("cid", "equals", cid);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamBuilder.build());
        String carID = "";
        String stationId = "";
        if (retrieve.isSuccess() && retrieve.getMessage() == null) {
            List<Map<String, Object>> rtnData = (List<Map<String, Object>>) retrieve.getData().get("rtnData");
            String checkorderId = (String) rtnData.get(0).get("checkorderId");
            //查询工单成功
            //设置工单状态为已完成
            //{"condition":[{"key":"cid","condition":"equals","value":"2019070800635"}],"dataObjectId":17,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":8}}
            DSParamBuilder dsParamBuilderCheckOrder = new DSParamBuilder(16);
            dsParamBuilderCheckOrder.buildCondition("cid", "equals", checkorderId);
            Map data = new HashMap();
            data.put("status", "已确认");
            dsParamBuilderCheckOrder.buildData(data);
            dataCenterFeignService.update(dsParamBuilderCheckOrder.build());
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCarId = dataCenterFeignService.retrieve(dsParamBuilderCheckOrder.build());
            if (retrieveCarId.isSuccess() && retrieveCarId.getMessage() == null) {
                List<Map<String, Object>> rtnDataCarId = (List<Map<String, Object>>) retrieveCarId.getData().get("rtnData");
                carID = (String) rtnDataCarId.get(0).get("carId");
                stationId = (String) rtnDataCarId.get(0).get("stationId");
            }
        }

        //根据车辆ID和工位ID查询所有工单
        //{"condition":{"carId":"1"},"dataObjectId":64,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":10}}
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(64);
        dsParamDsBuilder.buildCondition("carId", carID);
        dsParamDsBuilder.buildCondition("stationId", stationId);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckType = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        List<Map<String, Object>> rtnData1 = (List<Map<String, Object>>) retrieveCheckType.getData().get("rtnData");
        if (0 == rtnData1.size()) {
            //修改车辆状态
            //获取车辆状态
            DSParamBuilder dsParamBuilderCar = new DSParamBuilder(10);
            dsParamBuilderCar.buildCondition("cid", "equals", carID);
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCar = dataCenterFeignService.retrieve(dsParamBuilderCar.build());
            if (retrieveCar.isSuccess() && retrieveCar.getMessage() == null) {
                List<Map<String, Object>> rtnDataCar = (List<Map<String, Object>>) retrieveCar.getData().get("rtnData");
                String status = (String) rtnDataCar.get(0).get("status");
                Map dataUpdateCar = new HashMap();
                if (status != null && status.equals("台上检查")) {
                    log.info("通过的车辆处于台上检查并修改状态");
                    dataUpdateCar.put("status", "入库检查");
                    dataUpdateCar.put("checkStatus", "入库检查");
                }
                if (status != null && status.equals("入库检查")) {
                    log.info("通过的车辆处于入库检查并修改状态");
                    dataUpdateCar.put("status", "出荷检查");
                    dataUpdateCar.put("checkStatus", "出荷检查");
                }
                if (status != null && status.equals("出荷检查")) {
                    log.info("通过的车辆处于出荷检查并修改状态");
                    dataUpdateCar.put("status", "检查完成");
                    dataUpdateCar.put("checkStatus", "检查完成");
                }
                dsParamBuilderCar.buildData(dataUpdateCar);
                com.tianyi.datacenter.feign.common.vo.ResponseVo update = dataCenterFeignService.update(dsParamBuilderCar.build());
            } else {
                return ResponseVo.fail("修改车辆状态失败:" + retrieveCar.getMessage());
            }
        }
        saveMethod(params);
        return ResponseVo.success("保存成功!");
    }

    /**
     * 保存
     *
     * @param params
     * @return
     */
    @Transactional
    public ResponseVo saveOld(List<Map<String, Object>> params) {
        for (Map<String, Object> param : params) {
            String result = (String) param.get("result");

            //判断 result 通过时cid对用的异常是否为空
//            if ("1".equalsIgnoreCase(result)) {
            //判断是有有异常表已经存储
            String cid = (String) param.get("cid");
            JSONObject jsonObject = new JSONObject();
            //{"condition":[{"key":"checkorderId","condition":"equals","value":"1"}],"dataObjectId":18,
            // "pageInfo":{"page":1,"count":0,"pageSize":100,"total":1}}
            jsonObject.put("dataObjectId", 18);
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("key", "checkorderId");
            conditionMap.put("condition", "equals");
            conditionMap.put("value", param.get("cid"));
            Map<String, Integer> pageMap = new HashMap<>();
            pageMap.put("page", 0);
            pageMap.put("pageSize", 0);//全是0，代表不分页
            jsonObject.put("pageInfo", pageMap);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(jsonObject);
            if (responseVo.isSuccess() && responseVo.getMessage() == null) {
                log.info("查询在通过的情况下有没有存储异常表" + responseVo.getData());
                //删了
                //查询异常表  写的不对
                //{"condition":[{"key":"checkorderId","condition":"equals","value":"1"}],"dataObjectId":18,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":5}}
                JSONObject jsonObjectSearch = new JSONObject();
                jsonObjectSearch.put("dataObjectId", 18);
                List<Map<String, Object>> conditionMapSearch = new ArrayList();
                Map<String, Object> condition = new HashMap<>();
                condition.put("key", "checkorderId");
                condition.put("condition", "equals");
                condition.put("value", param.get("cid"));
                conditionMapSearch.add(condition);
                jsonObjectSearch.put("condition", conditionMapSearch);
                Map<String, Integer> pageMapSearch = new HashMap<>();
                pageMapSearch.put("page", 0);
                pageMapSearch.put("pageSize", 0);//全是0，代表不分页
                jsonObjectSearch.put("pageInfo", pageMapSearch);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSearch = dataCenterFeignService.retrieve(jsonObjectSearch);
                String pid = "";
                if (responseVoSearch.isSuccess() && responseVoSearch.getMessage() == null) {
                    log.info("如果在通过的状态下查询异常表，此时查询成功" + responseVoSearch.getData());
                    List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoSearch.getData().get("rtnData");
                    pid = (String) rtnData.get(0).get("pid");
                    if (pid == null || pid == "") {
                        return ResponseVo.fail("查询异常");
                    }
                }
                //删除{"dataObjectId":18,"condition":[{"key":"pid","condition":"equals","value":"23"}]}
                JSONObject jsonObjectDelete = new JSONObject();
                jsonObjectDelete.put("dataObjectId", 18);
                List<Map<String, Object>> conditionListDelete = new ArrayList<>();
                Map<String, Object> conditionDelete = new HashMap<>();
                conditionDelete.put("key", "pid");
                conditionDelete.put("condition", "equals");
                conditionDelete.put("value", pid);
                conditionListDelete.add(condition);
                jsonObjectDelete.put("condition", conditionListDelete);
                //{"condition":[{"condition":"equals","value":"1","key":"pid"}],"dataObjectId":18}
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoDelete = dataCenterFeignService.delete(jsonObjectDelete);
                //真正的保存

                saveMethod(params);

                log.info("通过后更改车辆信息");

                //查询carID
                String cidPass = (String) param.get("cid");
                //{"condition":{"cid":"1"},"dataObjectId":43,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":5}}
                JSONObject jsonObjectPass = new JSONObject();
                jsonObjectPass.put("dataObjectId", 43);
                Map<String, Object> conditionMapPass = new HashMap<>();
                conditionMapPass.put("cid", cidPass);
                jsonObjectPass.put("condition", conditionMapPass);
                Map<String, Integer> pagePass = new HashMap<>();
                pagePass.put("page", 0);
                pagePass.put("pageSize", 0);//全是0，代表不分页
                jsonObjectPass.put("pageInfo", pagePass);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoPass = dataCenterFeignService.retrieve(jsonObjectPass);
                String carId = "";
                if (responseVoPass.isSuccess() && responseVoPass.getMessage() == null) {
                    log.info("如果在通过的状态下查询车辆carId" + responseVoPass.getData());
                    List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoPass.getData().get("rtnData");
                    if (rtnData != null && rtnData.size() != 0) {
                        carId = (String) rtnData.get(0).get("cid");
                    }
                }
                //根据carId查询车辆信息
                //{"condition":[{"key":"cid","condition":"equals","value":"DBBJT8"}],"dataObjectId":10,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":2}}
                JSONObject jsonObjectSelectCar = new JSONObject();
                jsonObjectSelectCar.put("dataObjectId", 10);
                List<Map<String, Object>> conditionListSelectCar = new ArrayList<>();
                Map<String, Object> conditionSelectCar = new HashMap<>();
                conditionSelectCar.put("key", "pid");
                conditionSelectCar.put("condition", "equals");
                conditionSelectCar.put("value", carId);
                conditionListSelectCar.add(conditionSelectCar);
                jsonObjectSelectCar.put("condition", conditionListSelectCar);
                Map<String, Integer> pageSearchCar = new HashMap<>();
                pageSearchCar.put("page", 0);
                pageSearchCar.put("pageSize", 0);//全是0，代表不分页
                jsonObjectSelectCar.put("pageInfo", pageSearchCar);
                //{"condition":[{"condition":"equals","value":"1","key":"pid"}],"dataObjectId":18}
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSelectCar = dataCenterFeignService.retrieve(jsonObjectSelectCar);
                Map<String, Object> rtnDataSelect = new HashMap<>();
                if (responseVoSelectCar.isSuccess() && responseVoSelectCar.getMessage() == null) {
//                        log.info("查询车辆详情信息" + responseVoSelectCar.getData());
                    List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoSelectCar.getData().get("rtnData");
                    if (rtnDataSelect != null) {
                        String status = (String) rtnData.get(0).get("status");
                        //判断有异常车辆状态是异常中   status=返修中，checkstatus=入库检查
                        if (status != null && status.equals("台上检查")) {
                            log.info("通过的车辆处于台上检查并修改状态");
                            rtnDataSelect.put("status", "入库检查");
                            rtnDataSelect.put("checkStatus", "入库检查");
                        }
                        if (status != null && status.equals("入库检查")) {
                            log.info("通过的车辆处于入库检查并修改状态");
                            rtnDataSelect.put("status", "出荷检查");
                            rtnDataSelect.put("checkStatus", "出荷检查");
                        }
                        if (status != null && status.equals("出荷检查")) {
                            log.info("通过的车辆处于出荷检查并修改状态");
                            rtnDataSelect.put("status", "检查完成");
                            rtnDataSelect.put("checkStatus", "检查完成");
                        }
                        //{"dataObjectId":10,"data":{"cid":"DBBJT8","status":"台上检查","producer":"你","onChecker":"我","inChecker":"我","outChecker":"我","privateOrder":"我","deptName":"我","downTime":"2019-06-01 11:13:13","onStart":null,"onEnd":null,"inStart":null,"inEnd":null,"outStart":null,"outEnd":null,"checkStatus":"入库检查"},
                        // "condition":[{"key":"cid","condition":"equals","value":"DBBJT8"}]}
                        JSONObject jsonObjectUpdateCar = new JSONObject();
                        jsonObjectUpdateCar.put("dataObjectId", 10);
                        Map<String, Object> data = new HashMap<>();
                        jsonObjectUpdateCar.put("data", rtnDataSelect);
                        List<Map<String, Object>> conditionListUpdateCar = new ArrayList<>();
                        Map<String, Object> conditionUpdateCar = new HashMap<>();
                        conditionUpdateCar.put("key", "cid");
                        conditionUpdateCar.put("condition", "equals");
                        conditionUpdateCar.put("value", carId);
                        conditionListUpdateCar.add(conditionUpdateCar);
                        jsonObjectUpdateCar.put("condition", conditionListUpdateCar);
                        log.info("更改车辆状态数据详情" + jsonObjectUpdateCar);
                        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoUpdateCar = dataCenterFeignService.update(jsonObjectUpdateCar);
                        if (responseVoUpdateCar.isSuccess() && responseVoUpdateCar.getMessage() == null) {
                            log.info("更改车辆状态成功");
                        } else {
                            log.info("更改车辆状态失败");
                            return ResponseVo.fail("更改车辆状态失败");
                        }
                    }
                }

            } else {
                //changeCarStatusInProblem( params);
            }
            //通过不通过都查询检查项的source  为空不作操作 不为空 异常项状态设为 1
            //String cid = (String) param.get("cid");
            //{"condition":[],"dataObjectId":17,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":10}}
            JSONObject jsonObjectSearchSource = new JSONObject();
            jsonObjectSearchSource.put("dataObjectId", 17);
            List conditionMapSearchSource = new ArrayList();
            jsonObjectSearchSource.put("condition", conditionMapSearchSource);
            Map<String, Integer> pageSearchSource = new HashMap<>();
            pageSearchSource.put("page", 0);
            pageSearchSource.put("pageSize", 0);//全是0，代表不分页
            jsonObjectSearchSource.put("pageInfo", pageSearchSource);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSelect = dataCenterFeignService.retrieve(jsonObjectSearchSource);
            String source = "";
            if (responseVoSelect.isSuccess()) {
                log.info("查询检查项表的source成功");
                List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoSelect.getData().get("rtnData");
                source = (String) rtnData.get(0).get("source");
                if (source == null) {
                } else {
                    //异常项状态设为1
                    //根据cid  就是异常表的checkorderId 更改异常表的
                    //查询异常表对应的数据
                    //{"condition":[],"dataObjectId":18,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":10}}
                    JSONObject jsonObjectSelectProblem = new JSONObject();
                    jsonObjectSelectProblem.put("dataObjectId", 18);
                    List<Map<String, Object>> conditionListSelectProblem = new ArrayList<>();
                    Map<String, Object> conditionMapSelectProblem = new HashMap<>();
                    conditionMapSelectProblem.put("pid", source);
                    conditionListSelectProblem.add(conditionMapSelectProblem);
                    jsonObjectSelectProblem.put("condition", conditionMapSelectProblem);
                    Map<String, Integer> pageSearchProblem = new HashMap<>();
                    pageSearchProblem.put("page", 0);
                    pageSearchProblem.put("pageSize", 0);//全是0，代表不分页
                    jsonObjectSelectProblem.put("pageInfo", pageSearchProblem);
                    log.info("查询异常表" + jsonObjectSelectProblem);
                    //{"condition":[{"condition":"equals","value":"1","key":"pid"}],"dataObjectId":18}
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSelectProblem = dataCenterFeignService.retrieve(jsonObjectSelectProblem);
                    if (responseVoSelectProblem.isSuccess()) {
                        log.info("根据source查询异常表的信息成功");
                        List<Map> rtnDataUpdate = (List<Map>) responseVoSelectProblem.getData().get("rtnData");
                        Map updateMap = new HashMap();
                        if (rtnDataUpdate != null) {
                            updateMap = rtnDataUpdate.get(0);

                        }
                        if (updateMap.get("status") != null) {
                            updateMap.put("status", 1);


                            //更改异常表
                            //{"dataObjectId":18,"data":{"pid":"1","checkorderId":"f","CheckOrderItemchecker":null,
                            // "problemNum":"f","problemName":"3","problemDesc":"1","reasons":"1",
                            // "responsible":"1","solvePlan":"1","status":"0"},
                            // "condition":[{"key":"pid","condition":"equals","value":"1"}]}
                            JSONObject jsonObjectUpdateProblem = new JSONObject();
                            jsonObjectUpdateProblem.put("dataObjectId", 18);
                            List<Map<String, Object>> conditionListUpdateProblem = new ArrayList<>();
                            Map<String, Object> conditionMapUpdateProblem = new HashMap<>();
                            conditionMapUpdateProblem.put("key", "pid");
                            conditionMapUpdateProblem.put("condition", "equals");
                            conditionMapUpdateProblem.put("value", source);
                            conditionListUpdateProblem.add(conditionMapUpdateProblem);
                            jsonObjectUpdateProblem.put("condition", conditionListUpdateProblem);
                            jsonObjectUpdateProblem.put("data", updateMap);
                            log.info("更改异常表" + jsonObjectUpdateProblem);
                            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSelectUpdate = dataCenterFeignService.update(jsonObjectUpdateProblem);
                            if (responseVoSelectUpdate.isSuccess()) {
                                log.info("更改异常表的status成功" + updateMap.get("status"));
                            } else {
                                log.info("更改异常表失败");
                                return ResponseVo.fail("更改异常表状态失败");
                            }
                        }
                    } else {
                        return ResponseVo.fail("查询异常表状态失败");
                    }

                }
            }
        }

        return ResponseVo.success("保存成功");
    }

    public void changeCarStatusInProblem(List<Map<String, Object>> params) {
        for (Map<String, Object> param : params) {
            //查询carID
            String cid = (String) param.get("cid");
            //{"condition":{"cid":"1"},"dataObjectId":43,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":5}}
            DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(43);
            dsParamDsBuilder.buildCondition("cid", cid);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSelect = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
            String carId = "";
            if (responseVoSelect.isSuccess() && responseVoSelect.getMessage() == null) {
                List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoSelect.getData().get("rtnData");
                if (rtnData != null && rtnData.size() != 0) {
                    carId = (String) rtnData.get(0).get("cid");
                }
            }
            //根据carId查询车辆信息
            //{"condition":[{"key":"cid","condition":"equals","value":"DBBJT8"}],"dataObjectId":10,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":2}}
            DSParamBuilder dsParamBuilder = new DSParamBuilder(10);
            dsParamBuilder.buildCondition("pid", "equals", carId);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSelectCar = dataCenterFeignService.retrieve(dsParamBuilder.build());
            List<Map<String, Object>> rtnDataSelect = new ArrayList<>();
            if (responseVoSelectCar.isSuccess() && responseVoSelectCar.getMessage() == null) {
                rtnDataSelect = (List<Map<String, Object>>) responseVoSelectCar.getData().get("rtnData");
                if (rtnDataSelect != null && rtnDataSelect.size() > 0) {
                    String status = (String) rtnDataSelect.get(0).get("status");
                    //判断有异常车辆状态是异常中   status=返修中，checkstatus=入库检查
                    if (status != null && status.equals("台上检查")) {
                        log.info("不通过的车辆处于台上检查并修改状态");
                        rtnDataSelect.get(0).put("status", "返修中");
                        rtnDataSelect.get(0).put("checkStatus", "入库检查");
                    }
                    if (status != null && status.equals("入库检查")) {
                        log.info("不通过的车辆处于入库检查并修改状态");
                        rtnDataSelect.get(0).put("status", "返修中");
                        rtnDataSelect.get(0).put("checkStatus", "入库检查");
                    }
                    if (status != null && status.equals("出荷检查")) {
                        log.info("不通过的车辆处于出荷检查并修改状态");
                        rtnDataSelect.get(0).put("status", "返修中");
                        rtnDataSelect.get(0).put("checkStatus", "出荷检查");
                    }
                    //{"dataObjectId":10,"data":{"cid":"DBBJT8","status":"台上检查","producer":"你","onChecker":"我","inChecker":"我","outChecker":"我","privateOrder":"我","deptName":"我","downTime":"2019-06-01 11:13:13","onStart":null,"onEnd":null,"inStart":null,"inEnd":null,"outStart":null,"outEnd":null,"checkStatus":"入库检查"},
                    // "condition":[{"key":"cid","condition":"equals","value":"DBBJT8"}]}
                    dsParamBuilder.buildData(rtnDataSelect.get(0));
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoUpdateCar = dataCenterFeignService.update(dsParamBuilder.build());
                }
            }
        }
    }


    @Transactional
    public void saveMethod(List<Map<String, Object>> params) {
        for (Map<String, Object> param : params) {
            //保存结果
            //获取当前时间
            Date dateTime = new DateTime().toDate();
            //24小时制日期
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String today = simpleDateFormat.format(dateTime);
            JSONObject jsonObjectSaveNoPass = new JSONObject();
            jsonObjectSaveNoPass.put("dataObjectId", 17);
            //检查工单项目ID  雪花ID
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoId = dataCenterFeignService.retrieveId(jsonObjectSaveNoPass);
            //{"dataObjectId":17,"data":{"checkorderId":"1","status":"第三方","checkValue":"20","confirmMan":"手动阀","confirmTime":"2019-01-02 00:00:00","cid":"45","checkStart":"2019-01-02 00:00:00","checkEnd":"2019-01-02 00:00:00","itemId":"3","checker":"阿道夫","result":"阿萨德"}}
            String checkOrderItemcid = (String) responseVoId.getData().get("rtnData");

            //检查单ID  前台的cid
            String checkOrderItemId = (String) param.get("cid");

            //检查项类别ID  查询  把检查单详情查询考过来
            //{"condition":{"cid":"11"},"dataObjectId":63,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":31}}
            /*DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(63);
            dsParamDsBuilder.buildCondition("cid", checkOrderItemId);
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
            String itemId = (String) retrieve.getData().get("cid");*/

            //保存检查项目
            //{"dataObjectId":17,"data":{"checkorderId":"1","itemId":"2"}}
            JSONObject jsonObjectSave = new JSONObject();
            jsonObjectSave.put("dataObjectId", 17);
            Map<String, Object> conditionMapSave = new HashMap<>();
//            conditionMapSave.put("itemId", itemId);
            conditionMapSave.put("confirmTime", today);
            conditionMapSave.put("confirmMan", (String) param.get("confirmMan"));
            conditionMapSave.put("checkValue", param.get("checkValue"));
            conditionMapSave.put("result", param.get("result"));
//            conditionMapSave.put("cid", checkOrderItemId);
            //{"dataObjectId":25,"data":{"rid":"1","recodingModel":"拍照","time":6},"condition":[{"key":"rid","condition":"equals","value":"1"}]}
            DSParamBuilder dsParamBuilder = new DSParamBuilder(17);
            dsParamBuilder.buildCondition("cid", "equals", checkOrderItemId);
            dsParamBuilder.buildData(conditionMapSave);

            jsonObjectSave.put("data", conditionMapSave);
            dataCenterFeignService.update(dsParamBuilder.build());
        }

    }
}

