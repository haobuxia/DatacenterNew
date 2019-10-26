package com.tianyi.datacenter.inspect.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.constants.OrderConstant;
import com.tianyi.datacenter.common.util.TimeUtil;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.config.TianYiConfig;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.common.vo.*;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import com.tianyi.datacenter.inspect.dao.CheckOrderItemDao;
import com.tianyi.datacenter.inspect.service.CheckOrderItemService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 检查单详情
 *
 * @author wenxinyan
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
    @Autowired
    TianYiConfig tianYiConfig;
    @Autowired
    RestTemplate template;

    @Override
    public ResponseVo search(Map<String, Object> param) {
//        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        String oid = "";
        if (StrUtil.isEmpty(param.get("oid").toString())) {
            return ResponseVo.fail("缺少参数工单ID");
        } else {
            oid = param.get("oid").toString();
        }
        DSParamDsBuilder itemParamBuilder = new DSParamDsBuilder(24);
        itemParamBuilder.buildCondition("oid", param.get("oid"));
        if (param.get("checker") != null && !StrUtil.isEmpty(param.get("checker").toString())) {
            itemParamBuilder.buildCondition("checker", param.get("checker"));
        }
        if (param.get("startTime") != null && !StrUtil.isEmpty(param.get("startTime").toString())) {
            itemParamBuilder.buildCondition("startTime", param.get("startTime"));
        }
        if (param.get("endTime") != null && !StrUtil.isEmpty(param.get("endTime").toString())) {
            itemParamBuilder.buildCondition("endTime", param.get("endTime"));
        }
        if (param.get("result") != null && !StrUtil.isEmpty(param.get("result").toString())) {
            if (param.get("result").toString().split(",").length > 1) {

            } else {
                itemParamBuilder.buildCondition("result", param.get("result"));
            }
        }
        com.tianyi.datacenter.feign.common.vo.ResponseVo itemVo = dataCenterFeignService.retrieve(itemParamBuilder
                .build());
        if (itemVo.isSuccess()) {
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) itemVo.getData().get("rtnData");
            if (itemList.size() > 0) { // 按照大工单，工单，检查项分层次返回结果。
                Map<String, Object> orderMap = new HashMap<>();
                result.put("oid", oid);
                result.put("isQuick", itemList.get(0).get("oquick"));
                // 设置订单列表
                List<Map<String, Object>> checkorderresult = new ArrayList<>();
                setCheckOrderResult(oid, itemList, checkorderresult);
                result.put("checkorders", checkorderresult);
                /*for(Map<String, Object> item : itemList) {
                    if(orderMap.get(item.get("oid").toString()) == null) {// 大工单
                        orderMap.put(item.get("oid").toString(), item.get("oquick"));

                        result.put("oid", item.get("oid"));
                        result.put("isQuick", item.get("oquick"));
                        // 设置订单列表
                        List<Map<String, Object>> checkorderresult = new ArrayList<>();
                        setCheckOrderResult(item.get("oid").toString(),itemList,checkorderresult);
                        result.put("checkorders", checkorderresult);
//                        resultList.add(result);
                    }
                }*/
            }
        }
        return ResponseVo.success(result);
    }

    private void setCheckOrderResult(String oid, List<Map<String, Object>> itemList, List<Map<String, Object>>
            checkorderresult) {
        Set<String> checkOrderSet = new HashSet<>();
        for (Map<String, Object> item : itemList) {
            if (oid.equals(item.get("oid").toString())) {// 生成小工单
                if (!checkOrderSet.contains(item.get("coid").toString())) {
                    checkOrderSet.add(item.get("coid").toString());
                    Map<String, Object> checkOrder = new HashMap<>();
                    checkOrder.put("cid", item.get("coid"));
                    checkOrder.put("isQuick", item.get("coquick"));
                    List<Map<String, Object>> checkorderitemresult = new ArrayList<>();
                    setCheckOrderItemResult(item.get("coid").toString(), itemList, checkorderitemresult);
                    checkOrder.put("checkorderitems", checkorderitemresult);
                    checkorderresult.add(checkOrder);
                }
            }
        }
    }

    private void setCheckOrderItemResult(String coid, List<Map<String, Object>> itemList, List<Map<String, Object>>
            checkorderitemresult) {
        for (Map<String, Object> item : itemList) {
            if (coid.equals(item.get("coid").toString())) {// 生成检查项
                checkorderitemresult.add(item);
            }
        }
    }

    private Map<String, Object> queryMedia(String oid) {
        try {
            String url = tianYiConfig.getTianYiIntesrvUrl() + "/helmetmedia/list";
            HttpHeaders headers = new HttpHeaders();
            //定义请求体
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("orderNo", oid);
            // 以表单的方式提交
            //定义请求头
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            //将请求头部和参数合成一个请求
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
            //参数请求地址,请求封装的实体,响应的类型   向头盔发送请求并获取响应信息
            ResponseEntity<ResponseVo> response1 = template.postForEntity(url, requestEntity, ResponseVo.class);
            //获取响应的响应体
            ResponseVo vo = response1.getBody();
            if (vo.isSuccess()) {
                return (Map<String, Object>) vo.getData();
            }
        } catch (Exception e) {

        }
        return null;
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
        String cid = (String) params.get(0).get("coiid");
        DSParamBuilder dsParamBuilder = new DSParamBuilder(17);
        //根据检查明细查询检查单
        dsParamBuilder.buildCondition("cid", "equals", cid);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamBuilder
                .build());
        String oid = "";
        if (retrieve.isSuccess()) {
            List<Map<String, Object>> rtnData = (List<Map<String, Object>>) retrieve.getData().get("rtnData");
            String checkorderId = (String) rtnData.get(0).get("checkorderId");
            //查询工单成功
            //设置工单状态为已完成
            //{"condition":[{"key":"cid","condition":"equals","value":"2019070800635"}],"dataObjectId":17,
            // "pageInfo":{"page":1,"count":0,"pageSize":100,"total":8}}
            DSParamBuilder dsParamBuilderCheckOrder = new DSParamBuilder(16);
            dsParamBuilderCheckOrder.buildCondition("cid", "equals", checkorderId);
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCarId = dataCenterFeignService.retrieve
                    (dsParamBuilderCheckOrder.build());
            if (retrieveCarId.isSuccess()) {
                List<Map<String, Object>> rtnDataCarId = (List<Map<String, Object>>) retrieveCarId.getData().get
                        ("rtnData");
                oid = (String) rtnDataCarId.get(0).get("oid");
                DSParamBuilder checkOrders = new DSParamBuilder(16);
                checkOrders.buildCondition("oid", "equals", oid);
                com.tianyi.datacenter.feign.common.vo.ResponseVo checkOrdersResponse = dataCenterFeignService
                        .retrieve(checkOrders.build());
                if (checkOrdersResponse.isSuccess()) {
                    List<Map<String, Object>> checkOrderList = (List<Map<String, Object>>) checkOrdersResponse
                            .getData().get("rtnData");
                    for (Map<String, Object> checkOrder : checkOrderList) {
                        DSParamBuilder checkOrderParam = new DSParamBuilder(16);
                        checkOrderParam.buildCondition("cid", "equals", checkOrder.get("cid"));
                        Map data = new HashMap();
                        data.put("status", OrderConstant.ORDER_CONFIRMED);
                        checkOrderParam.buildData(data);
                        dataCenterFeignService.update(checkOrderParam.build());
                    }
                }
            }
        }
        //根据大工单ID
        //{"condition":{"carId":"1"},"dataObjectId":64,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":10}}
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(64);
        dsParamDsBuilder.buildCondition("oid", oid);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckType = dataCenterFeignService.retrieve
                (dsParamDsBuilder.build());
        List<Map<String, Object>> rtnData1 = (List<Map<String, Object>>) retrieveCheckType.getData().get("rtnData");
        if (0 == rtnData1.size()) {
            //修改大工单状态
            DSParamBuilder dsParamBuilderCar = new DSParamBuilder(71);
            dsParamBuilderCar.buildCondition("oid", "equals", oid);
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCar = dataCenterFeignService.retrieve
                    (dsParamBuilderCar.build());
            if (retrieveCar.isSuccess()) {
                Map dataUpdateCar = new HashMap();
                dataUpdateCar.put("checkstatus", OrderConstant.ORDER_CONFIRMED);
                dsParamBuilderCar.buildData(dataUpdateCar);
                dataCenterFeignService.update(dsParamBuilderCar.build());
            } else {
                return ResponseVo.fail("修改车辆状态失败:" + retrieveCar.getMessage());
            }
        }
        saveMethod(params);
        return ResponseVo.success("保存成功!");
    }

    @Override
    public ResponseVo searchResult(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        if (StrUtil.isEmpty(param.get("oid").toString())) {
            return ResponseVo.fail("缺少参数工单ID");
        }
        String oid = param.get("oid").toString();
        DSParamBuilder orderParamBuilder = new DSParamBuilder(71); // 查询大工单信息
        orderParamBuilder.buildCondition("oid", "equals", oid);
        com.tianyi.datacenter.feign.common.vo.ResponseVo existVo = dataCenterFeignService.retrieve(orderParamBuilder
                .build());
        if (existVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) existVo.getData().get("rtnData");
            if (list == null || list.size() == 0) {// 没有执行中的工单
                return ResponseVo.fail("工单不存在：" + oid);
            } else {
                result.put("oid", list.get(0).get("oid"));
                result.put("isQuick", list.get(0).get("isQuick"));// 如果是1则查询视频列表
                if ("1".equals(list.get(0).get("isQuick"))) {
                    Map<String, Object> media = queryMedia(list.get(0).get("oid").toString());
                    result.put("media", media);
                }
                /**
                 * 查询大工单下的小工单
                 */
                DSParamBuilder checkorderParamBuilder = new DSParamBuilder(16); // 查询小工单信息
                checkorderParamBuilder.buildCondition("oid", "equals", oid);
                com.tianyi.datacenter.feign.common.vo.ResponseVo checkorderVo = dataCenterFeignService.retrieve
                        (checkorderParamBuilder.build());
                if (checkorderVo.isSuccess()) {
                    List<Map<String, Object>> checkorderlist = (List<Map<String, Object>>) checkorderVo.getData().get
                            ("rtnData");
                    List<Map<String, Object>> checkorderresult = new ArrayList<>();
                    if (checkorderlist != null || checkorderlist.size() > 0) {
                        for (Map<String, Object> checkorder : checkorderlist) {
                            Map<String, Object> co = new HashMap<>();
                            co.put("cid", checkorder.get("cid"));
                            co.put("isQuick", checkorder.get("isQuick"));// 如果是1则查询视频列表
                            if ("1".equals(checkorder.get("isQuick").toString())) {
                                Map<String, Object> media1 = queryMedia(list.get(0).get("cid").toString());
                                co.put("media", media1);
                            } else {// 没有快速检查的则查询结果是[]
                                continue;
                            }

                            /**
                             * 查询检查项明细
                             */
                            DSParamDsBuilder itemParamBuilder = new DSParamDsBuilder(24);
                            itemParamBuilder.buildCondition("coid", checkorder.get("cid"));
                            com.tianyi.datacenter.feign.common.vo.ResponseVo itemVo = dataCenterFeignService.retrieve
                                    (itemParamBuilder.build());
                            if (itemVo.isSuccess()) {
                                List<Map<String, Object>> itemList = (List<Map<String, Object>>) itemVo.getData().get
                                        ("rtnData");
                                if (itemList.size() > 0) {
                                    co.put("checkorderitems", itemList);
                                }
                            }
                            checkorderresult.add(co);
                        }
                    }
                    result.put("checkorders", checkorderresult);
                }
            }
        }
        return ResponseVo.success(result);
    }

    @Override
    public ResponseVo imgresult(Map<String, Object> param) {
        String oid = (String) param.get("oid");//大工单ID
        Map resultData = new HashMap();
        DSParamDsBuilder orderParamBuilder = new DSParamDsBuilder(88);
        orderParamBuilder.buildCondition("oid", oid);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoOrder = dataCenterFeignService.retrieve
                (orderParamBuilder.build());
        List<Map<String, Object>> orderList = new ArrayList<>();

        if (responseVoOrder.isSuccess() && responseVoOrder.getMessage() == null) {
            orderList = (List<Map<String, Object>>) responseVoOrder.getData().get("rtnData");
            resultData.put("oid", orderList.get(0).get("oid"));
            resultData.put("isQuick", orderList.get(0).get("isQuick"));
            resultData.put("companyName", orderList.get(0).get("companyName"));
            resultData.put("modelName", orderList.get(0).get("modelName"));
            resultData.put("deviceNo", orderList.get(0).get("deviceNo"));
            resultData.put("checkStart", orderList.get(0).get("checkStart"));
            resultData.put("checker", orderList.get(0).get("checker"));
            resultData.put("stationName", orderList.get(0).get("stationName"));
        }
        //查询  checkorders   封装checkorderitemList
        List<Map<String, Object>> checkorderList = new ArrayList();
        List<Map<String, Object>> checkorderitemList = new ArrayList();
        Set<String> checkorderitemIdList = new HashSet<>();
        DSParamDsBuilder checkorderParamBuilder = new DSParamDsBuilder(90);
        checkorderParamBuilder.buildCondition("oid", oid);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVocheckorder = dataCenterFeignService.retrieve
                (checkorderParamBuilder.build());
        if (responseVocheckorder.isSuccess() && responseVocheckorder.getMessage() == null) {
            checkorderList = (List<Map<String, Object>>) responseVocheckorder.getData().get("rtnData");
            //保存快速检查工单checkorder的ID
            for (Map<String, Object> map : checkorderitemList) {
                if ("1".equalsIgnoreCase((String)map.get("isQuick"))){
                    checkorderitemIdList.add((String) map.get("cid"));
                }
            }
            int total = 0;
            int matchNum = 0;
            for (Map<String, Object> map : checkorderList) {
                DSParamDsBuilder checkorderitemParamBuilder = new DSParamDsBuilder(89);
                checkorderitemParamBuilder.buildCondition("oid", oid);
                checkorderitemParamBuilder.buildCondition("coid", map.get("cid"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVocheckorderitem = dataCenterFeignService
                        .retrieve
                                (checkorderitemParamBuilder.build());
                if (responseVocheckorderitem.isSuccess() && responseVocheckorderitem.getMessage() == null) {
                    checkorderitemList = (List<Map<String, Object>>) responseVocheckorderitem.getData().get("rtnData");
                }
                List<Map<String, String>> imgresult = new ArrayList();//图像识别结果数组
                List<Map<String, String>> voicetags = new ArrayList();//语音标签结果数组
                //快速查询替换coiid为检查项类别ID
                for (Map<String, Object> checkorderitem : checkorderitemList) {
                    for (String checkorderitemId : checkorderitemIdList) {
                        if (checkorderitem.get("checkorderId").equals(checkorderitemId)){
                            checkorderitem.put("coiid",checkorderitemId);
                        }
                    }
                }
                for (Map<String, Object> appendimgvoicetagmap : checkorderitemList) {
                    DSParamDsBuilder dsParamDsBuildermodel = new DSParamDsBuilder(92);
                    dsParamDsBuildermodel.buildCondition("itemId", appendimgvoicetagmap.get("coiid"));
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVovoicemodel = dataCenterFeignService
                            .retrieve
                                    (dsParamDsBuildermodel.build());
                    if (responseVovoicemodel.isSuccess()) {
                        imgresult = (List<Map<String, String>>) responseVovoicemodel.getData().get("rtnData");
                    }

                    appendimgvoicetagmap.put("imgresult", imgresult);
                    DSParamDsBuilder dsParamDsBuildervoicelag = new DSParamDsBuilder(93);
                    dsParamDsBuildervoicelag.buildCondition("itemId", appendimgvoicetagmap.get("coiid"));
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVovoicevoicetags =
                            dataCenterFeignService
                                    .retrieve
                                            (dsParamDsBuildervoicelag.build());
                    if (responseVovoicevoicetags.isSuccess()) {
                        voicetags = (List<Map<String, String>>) responseVovoicevoicetags.getData().get("rtnData");
                    }

                    appendimgvoicetagmap.put("imgresult", imgresult);
                    appendimgvoicetagmap.put("voicetags", voicetags);
                }
                map.put("checkorderitems", checkorderitemList);
                Map getchecktyperate = getchecktyperate((String) map.get("cid"));//检查工单ID
                map.put("rate", getchecktyperate.get("percentage"));
                //存储用于计算总合格率
                total += (int) getchecktyperate.get("total");
                matchNum += (int) getchecktyperate.get("matchNum");
            }
            String totalpercentage = TimeUtil.getPercentage(matchNum, total); //计算百分比
            resultData.put("rate", totalpercentage);
            resultData.put("checkorders", checkorderList);
        }
        return ResponseVo.success(resultData);
    }

    @Override
    public ResponseVo markresult(Map<String, String> param) {
        String videoId = param.get("videoId");
        //遍历checkorder添加图像识别和语音标签
        Map resultData = new HashMap();
        List imgresult = new ArrayList();
        List voicetags = new ArrayList();
        DSParamDsBuilder dsParamBuilderimg = new DSParamDsBuilder(92);
        dsParamBuilderimg.buildCondition("videoId", videoId);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoimg = dataCenterFeignService
                .retrieve
                        (dsParamBuilderimg.build());
        if (responseVoimg.isSuccess() && responseVoimg.getMessage() == null) {
            imgresult = (List<Map<String, Object>>) responseVoimg.getData().get("rtnData");
        }
        DSParamDsBuilder dsParamBuildervoicetag = new DSParamDsBuilder(93);
        dsParamBuildervoicetag.buildCondition("videoId", videoId);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVovoicetag = dataCenterFeignService
                .retrieve
                        (dsParamBuildervoicetag.build());
        if (responseVovoicetag.isSuccess() && responseVovoicetag.getMessage() == null) {
            voicetags = (List<Map<String, Object>>) responseVovoicetag.getData().get("rtnData");
        }
        resultData.put("videoId", videoId);
        resultData.put("imgresult", imgresult);
        resultData.put("voicetags", voicetags);
        return ResponseVo.success(resultData);
    }

    @Override
    public ResponseVo getBarrage(Map<String, String> param) {
        //查询语音识别
        List<Map<Object, Object>> searchBarrage = new ArrayList();
        DSParamBuilder dsParamBuilderimg = new DSParamBuilder(101);
        dsParamBuilderimg.buildCondition("videoId","equals", param.get("videoId"));
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseBarrage = dataCenterFeignService
                .retrieve
                        (dsParamBuilderimg.build());
        if (responseBarrage.isSuccess() && responseBarrage.getMessage() == null) {
            searchBarrage = (List<Map<Object, Object>>) responseBarrage.getData().get("rtnData");
        }
        List<Map<Object,Object>> barrage = new ArrayList<>();
        Map result = new HashMap<>();
        for (Map<Object, Object> stringObjectMap : searchBarrage) {
            Map<Object,Object> barrageMap = new HashMap<>();
            barrageMap.put("time",stringObjectMap.get("timeseconds"));
            barrageMap.put("title",stringObjectMap.get("perspeech"));
            barrage.add(barrageMap);
        }
        result.put("perspeech",barrage);
        result.put("videoId",param.get("videoId"));
        return ResponseVo.success(result);
    }

    @Override
    public ResponseVo getVoiceRemark(Map<String, String> param) {
        //查询语音识别
        List<Map<String, Object>> searchBarrage = new ArrayList();
        DSParamBuilder dsParamBuilderimg = new DSParamBuilder(101);
        dsParamBuilderimg.buildCondition("videoId","equals", param.get("videoId"));
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseBarrage = dataCenterFeignService
                .retrieve
                        (dsParamBuilderimg.build());
        if (responseBarrage.isSuccess() && responseBarrage.getMessage() == null) {
            searchBarrage = (List<Map<String, Object>>) responseBarrage.getData().get("rtnData");
        }
        List<Map> barrage = new ArrayList<>();
        Map result = new HashMap<>();
        for (Map<String, Object> stringObjectMap : searchBarrage) {
            String timeseconds = TimeUtil.getTimeStrBySecond((Integer) stringObjectMap.get("timeseconds"));
            String perspeech = (String)stringObjectMap.get("perspeech");
            String finall = timeseconds+perspeech;
            Map map = new HashMap();
            map.put("bid",stringObjectMap.get("bid"));
            map.put("voiceRemark",finall);
            barrage.add(map);
        }
        result.put("voiceRemarks",barrage);
        result.put("videoId",param.get("videoId"));
        return ResponseVo.success(result);
    }

    @Override
    public ResponseVo updateVoiceRemark(Map<String, Object> param) {
        List<Map<String, Object>> voiceRemarks = (List<Map<String, Object>>) param.get("voiceRemarks");
        for (Map<String, Object> stringObjectMap : voiceRemarks) {
            String voiceRemark = (String) stringObjectMap.get("voiceRemark");
            String bid = (String) stringObjectMap.get("bid");
            Map saveMap = new HashMap();
            saveMap.put("perspeech",voiceRemark.substring(8));//保存语音
            DSParamBuilder dsParamBuilderBarrage = new DSParamBuilder(101);
            dsParamBuilderBarrage.buildCondition("bid","equals", bid);
            dsParamBuilderBarrage.buildData(saveMap);
            com.tianyi.datacenter.feign.common.vo.ResponseVo update = dataCenterFeignService.update(dsParamBuilderBarrage.build());
            log.info(update.isSuccess()+"   "+update.isSuccess()+"语音标签更新++++++++++++++++++++");
        }
        return ResponseVo.success(param);
    }

    @Override
    public ResponseVo getAnalysisData(Map<String, String> param) {
        String videoId = param.get("videoId");
        String s = HttpUtil.get(tianYiConfig.getTianYiIntesrvUrl()+videoId);
        JSONObject jsonObject = JSONObject.parseObject(s);
        Map<String, Object> jsonObjectMap = jsonObject;
        Map<String, Object> data = (Map<String, Object>) jsonObjectMap.get("data");
        Map<String, List<Map<String, Object>>> val = (Map<String, List<Map<String, Object>>>) data.get("val");
        List<Map<String, Object>> zhuansurpm = val.get("转速rpm");
        double val1=0;
        for (Map<String, Object> stringObjectMap : zhuansurpm) {
            val1 += Double.parseDouble(stringObjectMap.get("val") + "");
        }
        double rpmavg = 0;
        if (zhuansurpm!=null&&zhuansurpm.size()>0) {
            rpmavg = val1 / zhuansurpm.size();
        }
        //todo:还没有需求
        return ResponseVo.success(rpmavg);
    }



    @Override
    public ResponseVo getItemTagresult(String videoId) {
        List<Map<String, Object>> result = new ArrayList();
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(103);
        dsParamDsBuilder.buildCondition("videoId",videoId);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        if (retrieve.isSuccess()&&retrieve.getMessage()==null){
            result = (List<Map<String, Object>>) retrieve.getData().get
                    ("rtnData");
            return ResponseVo.success(result);
        }
        return ResponseVo.success(result);
    }

    private Map getchecktyperate(String cid) {
        Map result = new HashMap();
        DSParamDsBuilder rateParamBuilder = new DSParamDsBuilder(91);
        rateParamBuilder.buildCondition("cid", cid);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVorate = dataCenterFeignService.retrieve
                (rateParamBuilder.build());
        int total;
        int matchNum = 0;
        if (responseVorate.isSuccess() && responseVorate.getMessage() == null) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) responseVorate.getData().get
                    ("rtnData");
            total = (Integer) list.get(0).get("total");
            result.put("total", total);
//            查询匹配的数量
            rateParamBuilder.buildCondition("oid", cid);
            rateParamBuilder.buildCondition("isMatch", "1");
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVorate2 = dataCenterFeignService.retrieve
                    (rateParamBuilder.build());
            if (responseVorate2.isSuccess() && responseVorate2.getMessage() == null) {
                List<Map<String, Object>> list2 = (List<Map<String, Object>>) responseVorate2.getData().get
                        ("rtnData");
                matchNum = (Integer) list2.get(0).get("total");
            }
            result.put("matchNum", matchNum);
            String percentage = TimeUtil.getPercentage(matchNum, total);
            result.put("percentage", percentage);
        }
        return result;
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
                //{"condition":[{"key":"checkorderId","condition":"equals","value":"1"}],"dataObjectId":18,
                // "pageInfo":{"page":1,"count":0,"pageSize":100,"total":5}}
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
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSearch = dataCenterFeignService.retrieve
                        (jsonObjectSearch);
                String pid = "";
                if (responseVoSearch.isSuccess() && responseVoSearch.getMessage() == null) {
                    log.info("如果在通过的状态下查询异常表，此时查询成功" + responseVoSearch.getData());
                    List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoSearch.getData().get
                            ("rtnData");
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
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoDelete = dataCenterFeignService.delete
                        (jsonObjectDelete);
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
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoPass = dataCenterFeignService.retrieve
                        (jsonObjectPass);
                String carId = "";
                if (responseVoPass.isSuccess() && responseVoPass.getMessage() == null) {
                    log.info("如果在通过的状态下查询车辆carId" + responseVoPass.getData());
                    List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoPass.getData().get
                            ("rtnData");
                    if (rtnData != null && rtnData.size() != 0) {
                        carId = (String) rtnData.get(0).get("cid");
                    }
                }
                //根据carId查询车辆信息
                //{"condition":[{"key":"cid","condition":"equals","value":"DBBJT8"}],"dataObjectId":10,
                // "pageInfo":{"page":1,"count":0,"pageSize":100,"total":2}}
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
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSelectCar = dataCenterFeignService
                        .retrieve(jsonObjectSelectCar);
                Map<String, Object> rtnDataSelect = new HashMap<>();
                if (responseVoSelectCar.isSuccess() && responseVoSelectCar.getMessage() == null) {
//                        log.info("查询车辆详情信息" + responseVoSelectCar.getData());
                    List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoSelectCar.getData().get
                            ("rtnData");
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
                        //{"dataObjectId":10,"data":{"cid":"DBBJT8","status":"台上检查","producer":"你","onChecker":"我",
                        // "inChecker":"我","outChecker":"我","privateOrder":"我","deptName":"我","downTime":"2019-06-01
                        // 11:13:13","onStart":null,"onEnd":null,"inStart":null,"inEnd":null,"outStart":null,
                        // "outEnd":null,"checkStatus":"入库检查"},
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
                        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoUpdateCar = dataCenterFeignService
                                .update(jsonObjectUpdateCar);
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
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSelect = dataCenterFeignService.retrieve
                    (jsonObjectSearchSource);
            String source = "";
            if (responseVoSelect.isSuccess()) {
                log.info("查询检查项表的source成功");
                List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoSelect.getData().get
                        ("rtnData");
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
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSelectProblem = dataCenterFeignService
                            .retrieve(jsonObjectSelectProblem);
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
                            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSelectUpdate =
                                    dataCenterFeignService.update(jsonObjectUpdateProblem);
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
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSelect = dataCenterFeignService.retrieve
                    (dsParamDsBuilder.build());
            String carId = "";
            if (responseVoSelect.isSuccess() && responseVoSelect.getMessage() == null) {
                List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoSelect.getData().get
                        ("rtnData");
                if (rtnData != null && rtnData.size() != 0) {
                    carId = (String) rtnData.get(0).get("cid");
                }
            }
            //根据carId查询车辆信息
            //{"condition":[{"key":"cid","condition":"equals","value":"DBBJT8"}],"dataObjectId":10,
            // "pageInfo":{"page":1,"count":0,"pageSize":100,"total":2}}
            DSParamBuilder dsParamBuilder = new DSParamBuilder(10);
            dsParamBuilder.buildCondition("pid", "equals", carId);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSelectCar = dataCenterFeignService.retrieve
                    (dsParamBuilder.build());
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
                    //{"dataObjectId":10,"data":{"cid":"DBBJT8","status":"台上检查","producer":"你","onChecker":"我",
                    // "inChecker":"我","outChecker":"我","privateOrder":"我","deptName":"我","downTime":"2019-06-01
                    // 11:13:13","onStart":null,"onEnd":null,"inStart":null,"inEnd":null,"outStart":null,
                    // "outEnd":null,"checkStatus":"入库检查"},
                    // "condition":[{"key":"cid","condition":"equals","value":"DBBJT8"}]}
                    dsParamBuilder.buildData(rtnDataSelect.get(0));
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoUpdateCar = dataCenterFeignService
                            .update(dsParamBuilder.build());
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
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoId = dataCenterFeignService.retrieveId
                    (jsonObjectSaveNoPass);
            //{"dataObjectId":17,"data":{"checkorderId":"1","status":"第三方","checkValue":"20","confirmMan":"手动阀",
            // "confirmTime":"2019-01-02 00:00:00","cid":"45","checkStart":"2019-01-02 00:00:00",
            // "checkEnd":"2019-01-02 00:00:00","itemId":"3","checker":"阿道夫","result":"阿萨德"}}
            String checkOrderItemcid = (String) responseVoId.getData().get("rtnData");

            //检查单ID  前台的cid
            String checkOrderItemId = (String) param.get("coiid");

            //检查项类别ID  查询  把检查单详情查询考过来
            //{"condition":{"cid":"11"},"dataObjectId":63,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":31}}
            /*DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(63);
            dsParamDsBuilder.buildCondition("cid", checkOrderItemId);
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve
            (dsParamDsBuilder.build());
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
            //{"dataObjectId":25,"data":{"rid":"1","recodingModel":"拍照","time":6},"condition":[{"key":"rid",
            // "condition":"equals","value":"1"}]}
            DSParamBuilder dsParamBuilder = new DSParamBuilder(17);
            dsParamBuilder.buildCondition("cid", "equals", checkOrderItemId);
            dsParamBuilder.buildData(conditionMapSave);

            jsonObjectSave.put("data", conditionMapSave);
            dataCenterFeignService.update(dsParamBuilder.build());
        }

    }
}

