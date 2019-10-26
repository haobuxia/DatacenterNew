package com.tianyi.datacenter.rabbitmq.listeners;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.util.TimeUtil;
import com.tianyi.datacenter.common.vo.RabbitMqVo;
import com.tianyi.datacenter.config.RabbitMQConfig;
import com.tianyi.datacenter.config.TianYiConfig;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import com.tianyi.datacenter.rabbitmq.MQProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by tianxujin on 2019/8/31 9:17
 */
@Component
@Slf4j
public class QueueListenter {

    @Autowired
    private MQProducer mqProducer;
    @Autowired
    private DataCenterFeignService dataCenterFeignService;
    @Autowired
    private TianYiConfig tianYiConfig;

    /**
     * 接收到视频上传成功的消息
     *
     * @param msg
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void videoMessage(Message msg) {
        try {
            System.out.print(msg.toString());
            Map<String, Object> message = JSONObject.parseObject(msg.getBody(), HashMap.class);
            // 1、获取视频需要识别的模型MID集合（根据工单ID）
            JSONObject messageJson = (JSONObject) message.get("message");
            List<String> modelIdList = getModelIdsByOrderId(messageJson.getString("orderId"), messageJson.getString
                    ("videoId"));
            // 2、拼接图像识别消息
            String modelIds = null;
            if (modelIdList != null && modelIdList.size() > 0) {
                modelIds = modelIdList.toString().
                        replace("[", "").replace("]", "").replace(" ", "");
                // 存储信令消息到数据库
                DSParamBuilder dsParamBuilderSave = new DSParamBuilder(83);
                ResponseVo responseVo = dataCenterFeignService.retrieveId(dsParamBuilderSave.build());
                String mid = null;
                if (responseVo.isSuccess() && responseVo.getMessage() == null) {
                    mid = (String) responseVo.getData().get("rtnData");
                }
                Map messageSave = new HashMap();
                messageSave.put("topic", RabbitMQConfig.QUEUE_RESULT);
                messageSave.put("videoId", messageJson.getString("videoId"));
                messageSave.put("modelIds", modelIds);
                messageSave.put("source", "1");
                messageSave.put("typeCode", "1");
                messageSave.put("createTime", new Date().getTime() + "");
                messageSave.put("mid", mid);
                dsParamBuilderSave.buildData(messageSave);
                ResponseVo retrieveOrder = dataCenterFeignService.add(dsParamBuilderSave.build());
                if (retrieveOrder.isSuccess()) {
                    log.info("信令保存成功");
                }

                // 3、发送图像识别消息到信令平台
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("topic", RabbitMQConfig.QUEUE_RESULT);
                params.put("videoId", messageJson.getString("videoId"));
                params.put("project", messageJson.getString("project"));
                params.put("modelIds", modelIds);
                params.put("source", "1");
                params.put("typeCode", "1");
                RabbitMqVo rabbitMqVo = new RabbitMqVo();
                rabbitMqVo.setsTime(new Date().getTime() + "");
                rabbitMqVo.setMessage(params);
                rabbitMqVo.setMessageId("1");
                rabbitMqVo.setRoutingKey("TYHelmet.File.Video.Recognition");
                mqProducer.sendDataToQueue("TYHelmet.File.Video.Recognition", rabbitMqVo);
            }

            //保存语音到本地
            String voicelag = HttpRequest.post(tianYiConfig
                    .getTianYiIntesrvImgocrmodelVideoUrl() + "/helmetmedia/video/getById?vid=" + messageJson.getString
                    ("videoId"))
                    .execute().body();
            System.out.println(voicelag + "11111111111111");
            JSONObject voicejo = JSONObject.parseObject(voicelag);
            Map<String, Object> voicetag = (Map<String, Object>) voicejo.get("data");
            List<Map<String, Object>> keywordsList = (List<Map<String, Object>>) voicetag.get("keywords");
            DSParamBuilder keywordsSave = new DSParamBuilder(101);
            Map<String, Object> saveMap = new HashMap<String, Object>();
            for (Map<String, Object> keywordsListMap : keywordsList) {
                ResponseVo responseVo = dataCenterFeignService.retrieveId(keywordsSave.build());
                String bid = null;
                if (responseVo.isSuccess() && responseVo.getMessage() == null) {
                    bid = (String) responseVo.getData().get("rtnData");
                }
                saveMap.put("bid", bid);
                saveMap.put("videoId", (String) keywordsListMap.get("videoId"));
                saveMap.put("perspeech", (String) keywordsListMap.get("perspeech"));
                saveMap.put("timeseconds", (Integer) keywordsListMap.get("timeseconds"));
                keywordsSave.buildData(saveMap);
                ResponseVo add = dataCenterFeignService.add(keywordsSave.build());
                if (add.isSuccess()) {
                    log.info(add.getMessage() + "语音弹幕保存成功+++++++++++++++++++++++++++++++++++++");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getModelIdsByOrderId(String s, String videoId) {
        Set modelIdList = new HashSet();
        Set voiceIdList = new HashSet();
        List<String> modelIdListFinalls = new ArrayList();//保存图像识别ID
        List<String> voiceIdListFinalls = new ArrayList();//保存语音识别ID
        //1.查询 order
        DSParamBuilder dsParamBuilderOrder = new DSParamBuilder(71);
        dsParamBuilderOrder.buildCondition("oid", "equals", s);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveOrder = dataCenterFeignService.retrieve
                (dsParamBuilderOrder.build());
        if (retrieveOrder.isSuccess() && retrieveOrder.getMessage() == null) {
            DSParamBuilder dsParamBuilderCheckOrder = new DSParamBuilder(16);
            dsParamBuilderCheckOrder.buildCondition("oid", "equals", s);
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckOrder = dataCenterFeignService.retrieve
                    (dsParamBuilderCheckOrder.build());
            if (retrieveCheckOrder.isSuccess() && retrieveCheckOrder.getMessage() == null) {
                List<Map<String, Object>> CheckOrderList = (List<Map<String, Object>>) retrieveCheckOrder.getData()
                        .get("rtnData");
                List<String> checkOrderIds = new ArrayList();
                for (Map<String, Object> map : CheckOrderList) {
                    String cid = (String) map.get("cid");
                    checkOrderIds.add(cid);
                }
                List<String> itemIdList = new ArrayList();
                for (String checkOrderId : checkOrderIds) {
                    DSParamBuilder dsParamBuilderCheckOrderItem = new DSParamBuilder(17);
                    dsParamBuilderCheckOrderItem.buildCondition("checkorderId", "equals", checkOrderId);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckOrderItem = dataCenterFeignService
                            .retrieve(dsParamBuilderCheckOrderItem.build());
                    if (retrieveCheckOrderItem.isSuccess() && retrieveCheckOrderItem.getMessage() == null) {
                        List<Map<String, Object>> CheckOrderItemList = (List<Map<String, Object>>)
                                retrieveCheckOrderItem.getData()
                                        .get("rtnData");

                        for (Map<String, Object> map : CheckOrderItemList) {
                            String itemId = (String) map.get("cid");
                            itemIdList.add(itemId);
                            DSParamBuilder dsParamBuilderCheckItem = new DSParamBuilder(11);
                            dsParamBuilderCheckItem.buildCondition("cid", "equals", (String) map.get("itemId"));
                            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckItem =
                                    dataCenterFeignService.retrieve(dsParamBuilderCheckItem.build());
                            if (retrieveCheckItem.isSuccess() && retrieveCheckItem.getMessage() == null) {
                                List<Map<String, Object>> imgocrList = (List<Map<String, Object>>) retrieveCheckItem
                                        .getData()
                                        .get("rtnData");
                                modelIdList.add((String) imgocrList.get(0).get("imgocr"));
                                voiceIdList.add((String) imgocrList.get(0).get("voicetag"));
                            }
                        }
                    }
                }
                modelIdListFinalls.addAll(modelIdList);
                voiceIdListFinalls.addAll(voiceIdList);
                savecheckorderitemImg(s, videoId, modelIdListFinalls, itemIdList);
                savecheckorderitemVoice(s, videoId, voiceIdListFinalls, itemIdList);
                return modelIdListFinalls;
            }
        }
        //2.查询checkorder
        DSParamBuilder dsParamBuilderCheckOrder = new DSParamBuilder(16);
        dsParamBuilderCheckOrder.buildCondition("cid", "equals", s);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckOrder = dataCenterFeignService.retrieve
                (dsParamBuilderCheckOrder.build());
        if (retrieveCheckOrder.isSuccess() && retrieveCheckOrder.getMessage() == null) {
            DSParamBuilder dsParamBuilderCheckOrderItem = new DSParamBuilder(17);
            dsParamBuilderCheckOrderItem.buildCondition("checkorderId", "equals", s);
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckOrderItem = dataCenterFeignService.retrieve
                    (dsParamBuilderCheckOrderItem.build());
            if (retrieveCheckOrderItem.isSuccess() && retrieveCheckOrderItem.getMessage() == null) {
                List<Map<String, Object>> CheckOrderItemList = (List<Map<String, Object>>) retrieveCheckOrderItem
                        .getData()
                        .get("rtnData");
                List<String> itemIdList = new ArrayList();
                for (Map<String, Object> map : CheckOrderItemList) {
                    String itemId = (String) map.get("cid");
                    itemIdList.add(itemId);
                    DSParamBuilder dsParamBuilderCheckItem = new DSParamBuilder(11);
                    dsParamBuilderCheckItem.buildCondition("cid", "equals", (String) map.get("itemId"));
                    com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckItem = dataCenterFeignService
                            .retrieve(dsParamBuilderCheckItem.build());
                    if (retrieveCheckItem.isSuccess() && retrieveCheckItem.getMessage() == null) {
                        List<Map<String, Object>> imgocrList = (List<Map<String, Object>>) retrieveCheckItem.getData()
                                .get("rtnData");
                        modelIdList.add((String) imgocrList.get(0).get("imgocr"));
                    }
                }
                modelIdListFinalls.addAll(modelIdList);
                voiceIdListFinalls.addAll(voiceIdList);
                savecheckorderitemImg(s, videoId, modelIdListFinalls, itemIdList);
                savecheckorderitemVoice(s, videoId, voiceIdListFinalls, itemIdList);
                return modelIdListFinalls;
            }
        }
        //3.查询checkorderitem
        DSParamBuilder dsParamBuilderCheckOrderItem = new DSParamBuilder(17);
        dsParamBuilderCheckOrderItem.buildCondition("cid", "equals", s);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckOrderItem = dataCenterFeignService.retrieve
                (dsParamBuilderCheckOrderItem.build());
        if (retrieveCheckOrderItem.isSuccess() && retrieveCheckOrderItem.getMessage() == null) {
            List<Map<String, Object>> CheckOrderItemList = (List<Map<String, Object>>) retrieveCheckOrderItem.getData()
                    .get("rtnData");
            List<String> itemIdList = new ArrayList();
            for (Map<String, Object> map : CheckOrderItemList) {
                String itemId = (String) map.get("cid");
                itemIdList.add(itemId);
                DSParamBuilder dsParamBuilderCheckItem = new DSParamBuilder(11);
                dsParamBuilderCheckItem.buildCondition("cid", "equals", (String) map.get("itemId"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckItem = dataCenterFeignService.retrieve
                        (dsParamBuilderCheckItem.build());
                if (retrieveCheckItem.isSuccess() && retrieveCheckItem.getMessage() == null) {
                    List<Map<String, Object>> imgocrList = (List<Map<String, Object>>) retrieveCheckItem.getData()
                            .get("rtnData");
                    modelIdList.add((String) imgocrList.get(0).get("imgocr"));
                }
            }
            modelIdListFinalls.addAll(modelIdList);
            voiceIdListFinalls.addAll(voiceIdList);
            savecheckorderitemImg(s, videoId, modelIdListFinalls, itemIdList);
            savecheckorderitemVoice(s, videoId, voiceIdListFinalls, itemIdList);
            return modelIdListFinalls;
        }
        return modelIdListFinalls;
    }

    private void savecheckorderitemImg(String orderId, String videoId, List<String> modelIdListFinalls, List<String>
            itemIdList) {
        //保存图像识别结果记录
        for (String itemId : itemIdList) {
            if (modelIdListFinalls == null || modelIdListFinalls.size() == 0) {
                DSParamBuilder dsParamBuilder = new DSParamBuilder(86);
                ResponseVo checkOrderItemImg =
                        dataCenterFeignService.retrieveId(dsParamBuilder.build());
                String checkOrderItemImgId = null;
                if (checkOrderItemImg.isSuccess()) {
                    checkOrderItemImgId = (String) checkOrderItemImg.getData().get("rtnData");
                }
                Map checkOrderItemImgMap = new HashMap();
                checkOrderItemImgMap.put("iid", checkOrderItemImgId);
                checkOrderItemImgMap.put("itemId", itemId);
                checkOrderItemImgMap.put("videoId", Integer.parseInt(videoId));
                checkOrderItemImgMap.put("orderId", orderId);
                checkOrderItemImgMap.put("modelId", "-1");
                dsParamBuilder.buildData(checkOrderItemImgMap);
                dataCenterFeignService.add(dsParamBuilder.build());
            } else {
                for (String modelId : modelIdListFinalls) {
                    DSParamBuilder dsParamBuilder = new DSParamBuilder(86);
                    ResponseVo checkOrderItemImg =
                            dataCenterFeignService.retrieveId(dsParamBuilder.build());
                    String checkOrderItemImgId = null;
                    if (checkOrderItemImg.isSuccess()) {
                        checkOrderItemImgId = (String) checkOrderItemImg.getData().get("rtnData");
                    }
                    Map checkOrderItemImgMap = new HashMap();
                    checkOrderItemImgMap.put("iid", checkOrderItemImgId);
                    checkOrderItemImgMap.put("itemId", itemId);
                    checkOrderItemImgMap.put("videoId", Integer.parseInt(videoId));
                    checkOrderItemImgMap.put("orderId", orderId);
                    checkOrderItemImgMap.put("modelId", modelId);
                    dsParamBuilder.buildData(checkOrderItemImgMap);
                    ResponseVo add = dataCenterFeignService.add(dsParamBuilder.build());
                    log.info(add.isSuccess() + add.getMessage() + "保存图像识别结果记录成功(初次保存没有匹配)+++++++++++++");
                }
            }
        }
    }

    private void savecheckorderitemVoice(String orderId, String videoId, List<String> voiceIdListFinalls, List<String>
            itemIdList) {
        for (String itemId : itemIdList) {
            if (voiceIdListFinalls == null || voiceIdListFinalls.size() == 0) {
                DSParamBuilder dsParamBuilder = new DSParamBuilder(87);
                ResponseVo checkOrderItemImg =
                        dataCenterFeignService.retrieveId(dsParamBuilder.build());
                String checkOrderItemImgId = null;
                if (checkOrderItemImg.isSuccess()) {
                    checkOrderItemImgId = (String) checkOrderItemImg.getData().get("rtnData");
                }
                Map checkOrderItemImgMap = new HashMap();
                checkOrderItemImgMap.put("tid", checkOrderItemImgId);
                checkOrderItemImgMap.put("itemId", itemId);
                checkOrderItemImgMap.put("videoId", Integer.parseInt(videoId));
                checkOrderItemImgMap.put("orderId", orderId);
                checkOrderItemImgMap.put("tagId", "-1");
                dsParamBuilder.buildData(checkOrderItemImgMap);
                dataCenterFeignService.add(dsParamBuilder.build());
            } else {
                for (String voiceId : voiceIdListFinalls) {
                    DSParamBuilder dsParamBuilder = new DSParamBuilder(87);
                    ResponseVo checkOrderItemImg =
                            dataCenterFeignService.retrieveId(dsParamBuilder.build());
                    String checkOrderItemImgId = null;
                    if (checkOrderItemImg.isSuccess()) {
                        checkOrderItemImgId = (String) checkOrderItemImg.getData().get("rtnData");
                    }
                    Map checkOrderItemImgMap = new HashMap();
                    checkOrderItemImgMap.put("tid", checkOrderItemImgId);
                    checkOrderItemImgMap.put("itemId", itemId);
                    checkOrderItemImgMap.put("videoId", Integer.parseInt(videoId));
                    checkOrderItemImgMap.put("orderId", orderId);
                    checkOrderItemImgMap.put("tagId", voiceId);
                    dsParamBuilder.buildData(checkOrderItemImgMap);
                    ResponseVo add = dataCenterFeignService.add(dsParamBuilder.build());
                    log.info(add.isSuccess() + add.getMessage() + "保存语音识别结果记录成功(初次保存没有匹配)++++++++++");
                }
            }
        }
    }

    /**
     * 接收到图像识别结果的通知消息
     *
     * @param msg
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_RESULT)
    public void resultMessage(Message msg) {
        try {
            System.out.print(msg.toString());
            Map<String, Object> message = JSONObject.parseObject(msg.getBody(), HashMap.class);
            // 1、通过接口平台去查询识别结果进一步处理
            JSONObject messageJson = (JSONObject) message.get("message");
            String videoId = messageJson.getString("videoId");
            String modelId = messageJson.getString("modelId");
            Map<String, Object> param = new HashMap<>();
            param.put("videoId", videoId);
            param.put("modelId", modelId);
            String result2 = HttpRequest.post(tianYiConfig
                    .getTianYiIntesrvImgocrmodelVideoUrl() + "/interface/video/getRecognizedResult")
                    .body(JSONObject.toJSONString(param))
                    .execute().body();
            System.out.println(result2 + "11111111111111");
            JSONObject jo = JSONObject.parseObject(result2);
            try {
                if (jo.get("data") == null) {
                    return;
                }
                // 2、将图像结果分析并更新到数据库中  object不为空就认为是识别成功
                updateImgisMatchmatchTime(modelId, videoId, (Map<String, Object>) jo.get("data"));
            } catch (Exception e) {
                System.out.println(e);
            }
            String voicelag = HttpRequest.post(tianYiConfig
                    .getTianYiIntesrvImgocrmodelVideoUrl() + "/helmetmedia/video/getById?vid=" + videoId)
                    .body(JSONObject.toJSONString(param))
                    .execute().body();
            System.out.println(voicelag + "11111111111111");
            JSONObject voicejo = JSONObject.parseObject(voicelag);
            Map<String, Object> voicetag = (Map<String, Object>) voicejo.get("data");
            //保存语音到本地
            List<Map<String, Object>> keywordsList = (List<Map<String, Object>>) voicetag.get("keywords");

            try {
                if (voicejo.get("data") == null) {
                    return;
                }
                // 2、将语音结果分析并更新到数据库中
                updatevoiceisMatchmatchTime(videoId, keywordsList);
            } catch (Exception e) {
                System.out.println(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatevoiceisMatchmatchTime(String videoId, List<Map<String, Object>> keywordsList) {
        DSParamBuilder dsParamBuildevoicelag = new DSParamBuilder(87);
        dsParamBuildevoicelag.buildCondition("videoId", "equals", videoId);
        ResponseVo responseVoimgoIds =
                dataCenterFeignService.retrieve(dsParamBuildevoicelag.build());
        List<Map<String, Object>> voicelagList = new ArrayList<>();
        if (responseVoimgoIds.isSuccess() && responseVoimgoIds.getMessage() == null) {
            voicelagList = (List<Map<String, Object>>) responseVoimgoIds.getData().get("rtnData");//语音识别字典
            for (Map<String, Object> stringStringMap : voicelagList) {//查询检查工单项目Id对应的检查项ID
                String itemId = (String) stringStringMap.get("itemId");
                //查询检查项ID
                DSParamBuilder dsParamBuilcheckitem = new DSParamBuilder(16);
                dsParamBuilcheckitem.buildCondition("cid", "equals", itemId);
                ResponseVo responsecheckitem =
                        dataCenterFeignService.retrieve(dsParamBuilcheckitem.build());
                if (responsecheckitem.isSuccess() && responsecheckitem.getMessage() == null) {//获取到检查项Id
                    List<Map<String, Object>> rtnDatacheckitemId = (List<Map<String, Object>>) responsecheckitem.getData().get("rtnData");
                    String voicetag = (String) rtnDatacheckitemId.get(0).get("voicetag");
                    Map<String, String> voiceName = new HashMap();
                    List<Map<String, String>> voiceNameLists = new ArrayList();
                    if (voicetag.contains(",")) {//有多个语音识别标签
                        String[] voicetagSplits = voicetag.split(",");
                        for (String voicetagSplitId : voicetagSplits) {//查询语音标签名称
                            DSParamBuilder dsParamBuildevoicelagName = new DSParamBuilder(85);
                            dsParamBuildevoicelagName.buildCondition("tid", "equals", voicetagSplitId);
                            ResponseVo responseVovoicelagName =
                                    dataCenterFeignService.retrieve(dsParamBuildevoicelagName.build());
                            if (responseVovoicelagName.isSuccess() && responseVovoicelagName.getMessage() == null) {
                                List<Map<String, Object>> rtnDatavoicelagName = (List<Map<String, Object>>) responseVovoicelagName.getData().get("rtnData");
                                voiceName.put((String) rtnDatavoicelagName.get(0).get("tid"), (String) rtnDatavoicelagName.get(0).get("name"));
                            }
                            voiceNameLists.add(voiceName);
                        }
                        String isMatch = "0";
                        int matchTime = -1;
                        //先设置成没有识别的
                        Map saveData = new HashMap();
                        saveData.put("isMatch", isMatch);
                        saveData.put("matchTime", matchTime);
                        dsParamBuildevoicelag.buildData(saveData);
                        dsParamBuildevoicelag.buildCondition("iid", "equals", stringStringMap.get("iid"));
                        dsParamBuildevoicelag.buildCondition("itemId", "equals", stringStringMap.get("itemId"));
                        ResponseVo update = dataCenterFeignService.update(dsParamBuildevoicelag.build());
                        //判断是否识别
                        for (Map<String, Object> stringObjectMap : keywordsList) {
                            String perspeech = (String) stringObjectMap.get("perspeech");
                            for (Map<String, String> voiceNameList : voiceNameLists) {
                                for (String key : voiceNameList.keySet()) {
                                    if (perspeech.contains(voiceNameList.get(key))) {
                                        isMatch = "1";
                                        matchTime = (Integer) stringObjectMap.get("timeseconds");
                                        saveData.put("isMatch", isMatch);
                                        saveData.put("matchTime", matchTime);
                                        dsParamBuildevoicelag.buildData(saveData);
                                        dsParamBuildevoicelag.buildCondition("iid", "equals", stringStringMap.get("iid"));
                                        dsParamBuildevoicelag.buildCondition("itemId", "equals", stringStringMap.get("itemId"));
                                        ResponseVo updateUpdate = dataCenterFeignService.update(dsParamBuildevoicelag.build());
                                        log.info("语音识别标签更新成功++++++++++++++" + updateUpdate.isSuccess() + "   " + updateUpdate.getMessage() + "检查工单项目ID" + stringStringMap.get("itemId"));
                                    }
                                }
                            }
                        }
                    } else {//只有一个语音标签的验证匹配
                        DSParamBuilder dsParamBuildevoicelagName = new DSParamBuilder(85);
                        dsParamBuildevoicelagName.buildCondition("tid", "equals", voicetag);
                        ResponseVo responseVovoicelagName =
                                dataCenterFeignService.retrieve(dsParamBuildevoicelagName.build());
                        if (responseVovoicelagName.isSuccess() && responseVovoicelagName.getMessage() == null) {
                            List<Map<String, Object>> rtnDatavoicelagName = (List<Map<String, Object>>) responseVovoicelagName.getData().get("rtnData");
                            String voicelagName = (String) rtnDatavoicelagName.get(0).get("name");
                            String isMatch = "0";
                            int matchTime = -1;
                            //先设置成没有识别的
                            Map saveData = new HashMap();
                            saveData.put("isMatch", isMatch);
                            saveData.put("matchTime", matchTime);
                            dsParamBuildevoicelag.buildData(saveData);
                            dsParamBuildevoicelag.buildCondition("iid", "equals", stringStringMap.get("iid"));
                            dsParamBuildevoicelag.buildCondition("itemId", "equals", stringStringMap.get("itemId"));
                            ResponseVo updateNoMatch = dataCenterFeignService.update(dsParamBuildevoicelag.build());
                            //判断是否识别
                            for (Map<String, Object> stringObjectMap : keywordsList) {
                                String perspeech = (String) stringObjectMap.get("perspeech");
                                if (perspeech.contains((voicelagName))) {
                                    isMatch = "1";
                                    matchTime = (Integer) stringObjectMap.get("timeseconds");
                                    saveData.put("isMatch", isMatch);
                                    saveData.put("matchTime", matchTime);
                                    dsParamBuildevoicelag.buildData(saveData);
                                    dsParamBuildevoicelag.buildCondition("iid", "equals", stringStringMap.get("iid"));
                                    ResponseVo updateMatch = dataCenterFeignService.update(dsParamBuildevoicelag.build());
                                    log.info("语音识别标签更新成功" + updateMatch.isSuccess() + "   " + updateMatch.getMessage() + "检查工单项目ID" + stringStringMap.get("itemId"));
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private void updateImgisMatchmatchTime(String modelId, String videoId, Map<String, Object> message) {
        List<String> timeList = new ArrayList();//模型第一次出现时间
        for (String s : message.keySet()) {
            String objectString = (String) message.get(s);
            Map<String, List> parseObject = JSONObject.parseObject(objectString, HashMap.class);
            List list = parseObject.get("object");
            if (list != null && list.size() > 0) {
                timeList.add(s);
                break;
            }
        }
        //根据视频和图像识别id查询图像识别结果记录表id
        DSParamBuilder dsParamBuildeimgId = new DSParamBuilder(86);
        dsParamBuildeimgId.buildCondition("videoId", "equals", videoId);
        dsParamBuildeimgId.buildCondition("modelId", "equals", modelId);
        ResponseVo responseVoimgoId =
                dataCenterFeignService.retrieve(dsParamBuildeimgId.build());
        List<Map<String, Object>> imgocrList = new ArrayList<>();
        if (responseVoimgoId.isSuccess() && responseVoimgoId.getMessage() == null) {
            imgocrList = (List<Map<String, Object>>) responseVoimgoId.getData()
                    .get("rtnData");
            for (Map<String, Object> map : imgocrList) {
                //更新图像识别结果记录表的匹配成功时间点和是否匹配
                Map updataData = new HashMap();
                if (timeList != null && timeList.size() > 0) {
                    Integer imgocrStartTimehhmm = TimeUtil.getImgocrStartTime(timeList.get(0));
                    updataData.put("matchTime", imgocrStartTimehhmm);//更新模型识别时间
                    updataData.put("isMatch", "1");
                } else {
                    updataData.put("matchTime", -1);//更新模型识别时间
                    updataData.put("isMatch", "0");
                }
                dsParamBuildeimgId.buildCondition("iid", "equals", map.get("iid"));
                dsParamBuildeimgId.buildData(updataData);
                ResponseVo update = dataCenterFeignService.update(dsParamBuildeimgId.build());
                log.info("图像识别更新成功" + update.isSuccess() + "   " + update.getMessage());
            }
        }
    }
}
