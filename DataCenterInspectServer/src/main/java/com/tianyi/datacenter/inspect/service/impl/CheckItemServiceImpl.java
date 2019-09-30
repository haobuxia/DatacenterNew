package com.tianyi.datacenter.inspect.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import com.tianyi.datacenter.inspect.service.CheckItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Service
@Slf4j
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private DataCenterFeignService dataCenterFeignService;

    @Override
    @Transactional
    public ResponseVo saveCheckType(Map<String, Object> param) {


        String modelId = (String) param.get("modelId");
        String checktypeNum = (String) param.get("checktypeNum");

        JSONObject jsonObjectSaveNoPass = new JSONObject();
        jsonObjectSaveNoPass.put("dataObjectId", 12);
        //检查工单项目ID  雪花ID
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdtypeNum = dataCenterFeignService.retrieveId(jsonObjectSaveNoPass);
        if (responseVoIdtypeNum.isSuccess()) {
            //查询检查项类别
            DSParamBuilder dsParamBuilder = new DSParamBuilder(12);
            dsParamBuilder.buildCondition("checktypeNum", "equals", checktypeNum);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckType = dataCenterFeignService.retrieve(dsParamBuilder.build());
            if (responseVoIdcheckType.isSuccess() && responseVoIdcheckType.getMessage() == null) {
                return ResponseVo.fail("检查项类别编号已存在,请修改");
                /* List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoIdcheckType.getData().get("rtnData");
                String cidcheckType = "";
                String Modelmid = "";
                if (rtnData.size() != 0 && rtnData != null) {
                    //修改
                    //获取检查项类别cid
                    cidcheckType = (String) rtnData.get(0).get("cid");
                    String modelId = (String) rtnData.get(0).get("modelId");
                    Modelmid = (String) rtnData.get(0).get("Modelmid");
                    String checktypeName = (String) rtnData.get(0).get("checktypeName");
                    Double checkOrder = (Double) rtnData.get(0).get("checkTypeOrder");
                    String voice = (String) rtnData.get(0).get("voice");
                    *//**
                 * {"dataObjectId":12,"data":{"cid":"3","modelId":"3","Modelmid":"PC200-8","checktypeName":"机油",
                 * "checkOrder":"45","voice":"g","checktypeNum":"gggggg"},
                 * "condition":[{"key":"cid","condition":"equals","value":"3"}]}
                 *//*
                    DSParamBuilder dsParamBuilderUpdate = new DSParamBuilder(12);
                    Map data = new HashMap();
                    data.put("cid", cidcheckType);
                    data.put("Modelmid", Modelmid);
                    data.put("checktypeNum", checktypeNum);
                    data.put("checktypeName", (String) param.get("checktypeName"));
                    data.put("checkOrder", (Double) param.get("checkOrder"));
                    data.put("voice", (String) param.get("voice"));
                    dsParamBuilderUpdate.buildCondition("checktypeNum", "equals", checktypeNum);
                    dsParamBuilderUpdate.buildData(data);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckTypeUpdate = dataCenterFeignService.update(dsParamBuilderUpdate.build());
                    if (responseVoIdcheckTypeUpdate.isSuccess()) {
                        return ResponseVo.success("保存成功!");
                    }*/
            } else {

                DSParamBuilder dsParamBuilderSave = new DSParamBuilder(12);
                Map dataSave = new HashMap();
                dataSave.put("cid", (String) responseVoIdtypeNum.getData().get("rtnData"));
                dataSave.put("modelId", modelId);
                dataSave.put("checktypeNum", checktypeNum);
                dataSave.put("checktypeName", (String) param.get("checktypeName"));
                dataSave.put("checkOrder", (Double.parseDouble(param.get("checkOrder") + "")));
                dataSave.put("voice", (String) param.get("voice"));
                dsParamBuilderSave.buildData(dataSave);
                System.out.println(dsParamBuilderSave.build());
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckTypeUpdate = dataCenterFeignService.add(dsParamBuilderSave.build());
                if (responseVoIdcheckTypeUpdate.isSuccess()) {
                    return ResponseVo.success("保存成功!");
                }
            }
        }
        return ResponseVo.fail("保存失败!");
    }


    @Override
    @Transactional
    public ResponseVo saveCheckItem(Map<String, Object> param) {
        //保存检查项
        String checkitemNum = (String) param.get("checkitemNum");


        //查询检查项
        DSParamBuilder dsParamBuilderItem = new DSParamBuilder(11);
        dsParamBuilderItem.buildCondition("checkitemNum", "equals", checkitemNum);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckItem = dataCenterFeignService.retrieve(dsParamBuilderItem.build());
        if (responseVoIdcheckItem.isSuccess() && responseVoIdcheckItem.getMessage() == null) {
            return ResponseVo.fail("检查项编号重复,请修改");
        }

        //保存
        /**
         {"dataObjectId":11,"data":{"cid":"343","checktypeName":"1","modelId":"1","checkOrder":"1","voice":"2","checktypeNum":"2"}}
         */
        DSParamBuilder dsParamBuilderSave = new DSParamBuilder(11);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckRecodeSave = dataCenterFeignService.retrieveId(dsParamBuilderSave.build());
        String rtnDataCid = "";
        if (responseVoIdcheckRecodeSave.isSuccess() && responseVoIdcheckRecodeSave.getMessage() == null) {
            rtnDataCid = (String) responseVoIdcheckRecodeSave.getData().get("rtnData");

        }
        Map dataSave = new HashMap();
        dataSave.put("cid", rtnDataCid);
        dataSave.put("checkitemtypeId", (String) param.get("cid"));
        dataSave.put("checkitemName", (String) param.get("checkitemName"));
        dataSave.put("checkitemNum", (String) param.get("checkitemNum"));
        dataSave.put("standard", (String) param.get("standard"));
        dataSave.put("solveMethod", (String) param.get("solveMethod"));
        dataSave.put("decisionMethod", (String) param.get("decisionMethod"));
        if ((String) param.get("recodingModelId") == null || (String) param.get("recodingModelId") == "") {
            dataSave.put("recodingModelId", "-1");
        } else {
            dataSave.put("recodingModelId", (String) param.get("recodingModelId"));
        }
        // daSaveta.put("recodingModel", (String) param.get("recodingModel"));
        // daSaveta.put("time", (String) param.get("time"));
        dataSave.put("resultType", (String) param.get("resultType"));
        dataSave.put("minResult", (String) param.get("minResult"));
        dataSave.put("maxResult", (String) param.get("maxResult"));
        dataSave.put("voiceInfo", (String) param.get("voiceInfo"));
        dataSave.put("guidance", (String) param.get("guidance"));
        dataSave.put("checkOrder", param.get("checkItemOrder"));
        dsParamBuilderSave.buildData(dataSave);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckTypeUpdate = dataCenterFeignService.add(dsParamBuilderSave.build());
        if (responseVoIdcheckTypeUpdate.isSuccess() && responseVoIdcheckTypeUpdate.getMessage() == null) {
            return ResponseVo.success("保存成功!");
        }
        /*if (responseVoIdcheckTypeUpdate.isSuccess()) {
            if (param.get("recodingModelId") != null && param.get("recodingModelId") != "") {
                //修改记录方式
                //{"dataObjectId":25,"data":{"rid":"1","recodingModel":"康查士","time":"20"},"condition":[{"key":"rid","condition":"equals","value":"1"}]}
                DSParamBuilder dsParamBuilderSaveRecode = new DSParamBuilder(25);
                Map<String, Object> dataSave2 = new HashMap();
                dataSave2.put("rid", (String) param.get("recodingModelId"));
                dataSave2.put("recodingModel", (String) param.get("recodingModel"));
                dataSave2.put("time", (Integer) param.get("time"));
                dsParamBuilderSaveRecode.buildData(dataSave2);
                dsParamBuilderSaveRecode.buildCondition("rid", "equals", (String) param.get("recodingModelId"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckRecodeUpdate = dataCenterFeignService.update(dsParamBuilderSaveRecode.build());
                if (responseVoIdcheckRecodeUpdate.isSuccess()) {
                    return ResponseVo.success("保存成功!");
                }
            }
        }*/
        return ResponseVo.success("保存失败!");
    }


    @Override
    @Transactional
    public ResponseVo saveAll(List<Map<String, Object>> param) {
        //excel自身对比  有重复的先提取出来  标记为重复
        for (int i = 0; i < param.size(); i++) {
            String first = "";
            String checkitemNum = (String) param.get(i).get("checkitemNum");
            String modelName = (String) param.get(i).get("modelName");
            String checktypeNum = (String) param.get(i).get("checktypeNum");
            //失败状态之机型+检查项类别编号+检查项编号  重复
            first = modelName+checktypeNum+checkitemNum;
            for (int j = i+1; j < param.size(); j++) {
                String second = "";
                String checkitemNum2 = (String) param.get(j).get("checkitemNum");
                String modelName2 = (String) param.get(j).get("modelName");
                String checktypeNum2 = (String) param.get(j).get("checktypeNum");
                second = modelName2+checktypeNum2+checkitemNum2;
                if (first.equalsIgnoreCase(second)){
                    Map map1 = param.get(j);
                    map1.put("results", "重复");
                    break;
                }
            }
        }
        //保存检查项和类别
        for (int i = 0; i < param.size(); i++) {
            String results = (String) param.get(i).get("results");
            if (results.equalsIgnoreCase("重复")) {
                param.get(i).put("results", "重复");
                continue;
            }
            if (results.equalsIgnoreCase("失败")) {
                param.get(i).put("results", "失败");
                continue;
            }
            if (results.equalsIgnoreCase("成功")) {
                param.get(i).put("results", "成功");
                continue;
            }
            String checkTypeId = getCheckTypeId(param.get(i));
            if (results.equalsIgnoreCase("新增")) {

                //保存检查项   在这个循环中  去param.get(y).get(...)   批量保存检查项
                DSParamBuilder dsParamBuilderItem = new DSParamBuilder(11);// 获取检查项类别ID
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoCheckItemCid = dataCenterFeignService.retrieveId(dsParamBuilderItem.build());
                if (responseVoCheckItemCid.isSuccess() && responseVoCheckItemCid.getMessage() == null) {
                    DSParamBuilder dsParamBuilderItemSave = new DSParamBuilder(11);
                    Map<String, Object> itemSave = new HashMap<>();
                    itemSave.put("cid", responseVoCheckItemCid.getData().get("rtnData"));
                    itemSave.put("checkitemNum", param.get(i).get("checkitemNum"));
                    itemSave.put("checkitemtypeId", checkTypeId);
                    //保存记录方式id
                    DSParamBuilder dsParamBuilderRecoding = new DSParamBuilder(25);
                    dsParamBuilderRecoding.buildCondition("recodingModel", "equals", (String) param.get(i).get("recodingModel"));
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding = dataCenterFeignService.retrieve(dsParamBuilderRecoding.build());
                    if (responseVoRecoding.isSuccess() && responseVoRecoding.getMessage() == null) {
                        //可能修改记录方式的时间
//                        Integer time = (Integer) param.get(i).get("time");
                        List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoRecoding.getData().get("rtnData");
                        itemSave.put("recodingModelId", (String) rtnData.get(0).get("rid"));

                    } else {
                        itemSave.put("recodingModelId", "-1");
                    }
                    itemSave.put("checkitemName", param.get(i).get("checkitemName"));
                    itemSave.put("checkOrder", param.get(i).get("checkOrderItem"));
                    itemSave.put("standard", param.get(i).get("standard"));
                    itemSave.put("solveMethod", param.get(i).get("solveMethod"));
                    itemSave.put("decisionMethod", param.get(i).get("decisionMethod"));
                    itemSave.put("resultType", param.get(i).get("resultType"));
                    itemSave.put("minResult", param.get(i).get("minResult"));
                    itemSave.put("maxResult", param.get(i).get("maxResult"));
                    itemSave.put("voiceInfo", param.get(i).get("voiceInfo"));
                    itemSave.put("guidance", param.get(i).get("guidance"));
                    dsParamBuilderItem.buildData(itemSave);
                    dataCenterFeignService.add(dsParamBuilderItem.build());
                }
                param.get(i).put("results", "成功");
            } else if (results.equalsIgnoreCase("修改")) {

                //修改检查项
                DSParamBuilder dsParamBuilderItem = new DSParamBuilder(11);// 获取检查项类别ID
                dsParamBuilderItem.buildCondition("checkitemNum", "equals", param.get(i).get("checkitemNum"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoCheckItemCid = dataCenterFeignService.retrieve(dsParamBuilderItem.build());
                List<Map<String, Object>> rtnData1 = (List<Map<String, Object>>) responseVoCheckItemCid.getData().get("rtnData");

                if (responseVoCheckItemCid.isSuccess() && responseVoCheckItemCid.getMessage() == null) {
                    DSParamBuilder dsParamBuilderItemUpdate = new DSParamBuilder(11);
                    dsParamBuilderItemUpdate.buildCondition("cid", "equals", (String) rtnData1.get(0).get("cid"));
                    Map<String, Object> itemSave = new HashMap<>();
                    itemSave.put("checkitemNum", param.get(i).get("checkitemNum"));
                    itemSave.put("checkitemtypeId", checkTypeId);
                    //保存记录方式id
                    DSParamBuilder dsParamBuilderRecoding = new DSParamBuilder(25);
                    dsParamBuilderRecoding.buildCondition("recodingModel", "equals", (String) param.get(i).get("recodingModel"));
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding = dataCenterFeignService.retrieve(dsParamBuilderRecoding.build());
                    if (responseVoRecoding.isSuccess() && responseVoRecoding.getMessage() == null) {
                        //可能修改记录方式的时间
                        Integer time = (Integer) param.get(i).get("time");
                        List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoRecoding.getData().get("rtnData");
                        itemSave.put("recodingModelId", (String) rtnData.get(0).get("rid"));

                    } else {
                        itemSave.put("recodingModelId", "-1");
                    }
                    itemSave.put("checkitemName", param.get(i).get("checkitemName"));
                    itemSave.put("checkOrder", param.get(i).get("checkOrderItem"));
                    itemSave.put("standard", param.get(i).get("standard"));
                    itemSave.put("solveMethod", param.get(i).get("solveMethod"));
                    itemSave.put("decisionMethod", param.get(i).get("decisionMethod"));
                    itemSave.put("resultType", param.get(i).get("resultType"));
                    itemSave.put("minResult", param.get(i).get("minResult"));
                    itemSave.put("maxResult", param.get(i).get("maxResult"));
                    itemSave.put("voiceInfo", param.get(i).get("voiceInfo"));
                    itemSave.put("guidance", param.get(i).get("guidance"));
                    dsParamBuilderItemUpdate.buildData(itemSave);
                    dataCenterFeignService.update(dsParamBuilderItemUpdate.build());
                    param.get(i).put("results", "成功");
                }
            }

        }
        return ResponseVo.success(param);
    }
    private String getCheckTypeId(Map param) {

        String checktypeNum = (String) param.get("checktypeNum");
        String cidType = "";
        //检查项类别不一定  无论是新增还是修改都先删除再新增  如果是修改  首先获取老的  cidType
        //检查项类别{"condition":[{"key":"checktypeNum","condition":"equals","value":"bbb"}],"dataObjectId":12,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":36}}
        DSParamBuilder dsParamBuilderType = new DSParamBuilder(12);
        dsParamBuilderType.buildCondition("checktypeNum", "equals", checktypeNum);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoCheckType = dataCenterFeignService.retrieve(dsParamBuilderType.build());
        List<Map<String, Object>> rtnDataType = (List<Map<String, Object>>) responseVoCheckType.getData().get("rtnData");
        if (rtnDataType != null &&rtnDataType.size() > 0) {
            cidType = (String) rtnDataType.get(0).get("cid");
        }
        DSParamBuilder dsParamBuilderTypeSave = new DSParamBuilder(12);
        Map<String, Object> typeSave = new HashMap<>();
        if (cidType != null && cidType != "") { //存在就的修改
            typeSave.put("cid", cidType);
            typeSave.put("checktypeName", param.get("checktypeName"));
            typeSave.put("voice", param.get("voice"));
            typeSave.put("checktypeNum", param.get("checktypeNum"));
            typeSave.put("checkOrder", param.get("checkOrderType"));
            dsParamBuilderTypeSave.buildData(typeSave);
            com.tianyi.datacenter.feign.common.vo.ResponseVo update = dataCenterFeignService.update(dsParamBuilderTypeSave.build());
            System.out.println("检查项类别修改成功");
        } else {
            //不存在就的  新增保存新的检查项类别ID
            DSParamBuilder dsParamBuilder = new DSParamBuilder(12);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieveId(dsParamBuilder.build());
            cidType = (String) responseVo.getData().get("rtnData");
            typeSave.put("cid", cidType);
            typeSave.put("checktypeName", param.get("checktypeName"));
            typeSave.put("voice", param.get("voice"));
            typeSave.put("checktypeNum", param.get("checktypeNum"));
            typeSave.put("checkOrder", param.get("checkOrderType"));
            dsParamBuilderTypeSave.buildData(typeSave);
            com.tianyi.datacenter.feign.common.vo.ResponseVo add = dataCenterFeignService.add(dsParamBuilderTypeSave.build());
            System.out.println("检查项类别新增成功");
        }
        return cidType;
    }

    @Override
    public ResponseVo detail(Map<String, Object> param) {
        //{"condition":{"checktypeNum":"1"},"dataObjectId":56,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":74}}
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(56);
        Map<String, String> condition = (Map<String, String>) param.get("condition");
        dsParamDsBuilder.buildCondition("checktypeNum", condition.get("checktypeNum"));
        dsParamDsBuilder.buildCondition("checkitemNum", condition.get("checkitemNum"));
        Map<String, Object> resultmap = new HashMap<>();
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoOld = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        if (responseVoOld.isSuccess() && responseVoOld.getMessage() == null) {
            List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoOld.getData().get("rtnData");
            resultmap = rtnData.get(0);
            if (rtnData.size() != 0 && rtnData != null) {
                String recodingModelId = (String) rtnData.get(0).get("recodingModelId");
                //查询记录方式 和 时间
                DSParamBuilder dsParamBuilder = new DSParamBuilder(25);
                dsParamBuilder.buildCondition("rid", "equals", recodingModelId);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding = dataCenterFeignService.retrieve(dsParamBuilder.build());
                if (responseVoRecoding.isSuccess() && responseVoRecoding.getMessage() == null) {
                    List<Map<String, Object>> rtnDataRecoding = (List<Map<String, Object>>) responseVoRecoding.getData().get("rtnData");
                    resultmap.put("recodingModel", (String) rtnDataRecoding.get(0).get("recodingModel"));
//                    resultmap.put("time", (Integer) rtnDataRecoding.get(0).get("time"));
                }
            }
        }
        return ResponseVo.success(resultmap);
    }

    @Override
    @Transactional
    public ResponseVo updateCheckType(Map<String, Object> param) {

        String modelId = (String) param.get("modelId");
        String cid = (String) param.get("cid");
        String checktypeNum = (String) param.get("checktypeNum");




        //查询检查项类别
        DSParamBuilder dsParamBuilder = new DSParamBuilder(12);
        dsParamBuilder.buildCondition("cid", "equals", cid);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckType = dataCenterFeignService.retrieve(dsParamBuilder.build());

        List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoIdcheckType.getData().get("rtnData");

        if (rtnData.size() != 0 && rtnData != null) {
            //修改
            //获取检查项类别cid
            cid = (String) param.get("cid");

            DSParamBuilder dsParamBuilderUpdate = new DSParamBuilder(12);
            Map dataUpdate = new HashMap();
            dataUpdate.put("cid", cid);
            dataUpdate.put("modelId", modelId);
            dataUpdate.put("checktypeNum", checktypeNum);
            dataUpdate.put("checktypeName", (String) param.get("checktypeName"));
            dataUpdate.put("checkOrder", (Double.parseDouble(param.get("checkOrder") + "")));
            dataUpdate.put("voice", (String) param.get("voice"));
            dsParamBuilderUpdate.buildCondition("cid", "equals", cid);
            dsParamBuilderUpdate.buildData(dataUpdate);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckTypeUpdate = dataCenterFeignService.update(dsParamBuilderUpdate.build());
            if (responseVoIdcheckTypeUpdate.isSuccess()) {
                return ResponseVo.success("修改成功!");
            }
        }
        return ResponseVo.fail("修改失败!");
    }

    @Override
    @Transactional
    public ResponseVo updateCheckItem(Map<String, Object> param) {
        //修改检查项
        String checkitemNum = (String) param.get("cid");
        String checkitemNum1 = (String) param.get("checkitemNum");

        //修改检查项
        JSONObject jsonObjectSaveItem = new JSONObject();
        jsonObjectSaveItem.put("dataObjectId", 11);


        //查询检查项
        DSParamBuilder dsParamBuilderItem2 = new DSParamBuilder(11);
        dsParamBuilderItem2.buildCondition("cid", "equals", checkitemNum);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckItem2 = dataCenterFeignService.retrieve(dsParamBuilderItem2.build());

        List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoIdcheckItem2.getData().get("rtnData");
        String cidcheckItem = "";
        String checkitemtypeId = "";
        String recodingModelId = "";
        if (rtnData.size() != 0 && rtnData != null) {
            //修改
            //获取检查项类别cid
            cidcheckItem = (String) rtnData.get(0).get("cid");
            checkitemtypeId = (String) rtnData.get(0).get("checkitemtypeId");
            String checkitemName = (String) rtnData.get(0).get("checkitemName");
            String guidance = (String) rtnData.get(0).get("guidance");
            String standard = (String) rtnData.get(0).get("standard");
            String solveMethod = (String) rtnData.get(0).get("solveMethod");
            String decisionMethod = (String) rtnData.get(0).get("decisionMethod");
            recodingModelId = (String) rtnData.get(0).get("recodingModelId");
            String time = (String) rtnData.get(0).get("time");
            String resultType = (String) rtnData.get(0).get("resultType");
            String minResult = (String) rtnData.get(0).get("minResult");
            String maxResult = (String) rtnData.get(0).get("maxResult");
            String voiceInfo = (String) rtnData.get(0).get("voiceInfo");

            DSParamBuilder dsParamBuilderUpdate = new DSParamBuilder(11);
            Map data = new HashMap();
            data.put("cid", (String) rtnData.get(0).get("cid"));
            data.put("checkitemtypeId", checkitemtypeId);
            data.put("checkitemName", (String) param.get("checkitemName"));
            data.put("checkitemNum", (String) param.get("checkitemNum"));
            data.put("standard", (String) param.get("standard"));
            data.put("solveMethod", (String) param.get("solveMethod"));
            data.put("decisionMethod", (String) param.get("decisionMethod"));
            if ((String) param.get("recodingModelId") == null || (String) param.get("recodingModelId") == "") {
                data.put("recodingModelId", "-1");
            } else {
                data.put("recodingModelId", (String) param.get("recodingModelId"));
            }
//                    data.put("recodingModel", (String) param.get("recodingModel"));
//                    data.put("time", (String) param.get("time"));
            data.put("resultType", (String) param.get("resultType"));
            data.put("minResult", (String) param.get("minResult"));
            data.put("maxResult", (String) param.get("maxResult"));
            data.put("voiceInfo", (String) param.get("voiceInfo"));
            data.put("guidance", (String) param.get("guidance"));
            data.put("checkOrder", param.get("checkItemOrder"));
            dsParamBuilderUpdate.buildCondition("cid", "equals", checkitemNum);
            dsParamBuilderUpdate.buildData(data);
            System.out.println(dsParamBuilderUpdate.build());
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckTypeUpdate = dataCenterFeignService.update(dsParamBuilderUpdate.build());
            return ResponseVo.success("修改成功!");
           /* if (responseVoIdcheckTypeUpdate.isSuccess()) {
                //修改记录方式  修改或者保存
                //{"dataObjectId":25,"data":{"rid":"1","recodingModel":"康查士","time":"20"},"condition":[{"key":"rid","condition":"equals","value":"1"}]}
                DSParamBuilder dsParamBuilderUpdateRecode = new DSParamBuilder(25);
                Map dataSave = new HashMap();
                dataSave.put("rid", (String) param.get("recodingModelId"));
                dataSave.put("recodingModel", (String) param.get("recodingModel"));
                dataSave.put("time", (Integer) param.get("time"));
                dsParamBuilderUpdateRecode.buildData(dataSave);
                dsParamBuilderUpdateRecode.buildCondition("rid", "equals", (String) param.get("recodingModelId"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckRecodeUpdate = dataCenterFeignService.update(dsParamBuilderUpdateRecode.build());
                if (responseVoIdcheckRecodeUpdate.isSuccess()) {
                    return ResponseVo.success("修改成功!");
                } else {
                    //新增
                    JSONObject jsonObjectSaveRecode = new JSONObject();
                    jsonObjectSaveRecode.put("dataObjectId", 25);
                    //检查工单项目ID  雪花ID
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseRecodeId = dataCenterFeignService.retrieveId(jsonObjectSaveRecode);
                    if (responseRecodeId.isSuccess()) {
                        //{"dataObjectId":25,"data":{"rid":"8","recodingModel":"88","time":"8"}}
                        DSParamBuilder dsParamBuilderSaveRecode = new DSParamBuilder(25);
                        Map dataUpdate = new HashMap();
                        dataUpdate.put("rid", (String) responseRecodeId.getData().get("rtnData"));
                        dataUpdate.put("recodingModel", (String) param.get("recodingModel"));
                        dataUpdate.put("time", (Integer) param.get("time"));
                        dsParamBuilderSaveRecode.buildData(dataUpdate);
                        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckRecodeSave = dataCenterFeignService.add(dsParamBuilderSaveRecode.build());
                        if (responseVoIdcheckRecodeSave.isSuccess()) {
                            return ResponseVo.success("修改成功!");
                        }
                    }
                }
            }*/

        }
        return ResponseVo.fail("检查项修改失败!");
    }

    @Override
    public ResponseVo searchCheckItem(Map<String, Object> param) {
        String checktypecid = (String) param.get("checktypecid");
        Map resultMap = new HashMap();
        //根据  checktypecid  查询所有的  检查项  11
        DSParamBuilder dsParamBuilder = new DSParamBuilder(11);
        dsParamBuilder.buildCondition("checkitemtypeId", "equals", checktypecid);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckItem = dataCenterFeignService.retrieve(dsParamBuilder.build());
        String recodingModelId = "";
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (retrieveCheckItem.isSuccess() && retrieveCheckItem.getMessage() == null) {
            List<Map<String, Object>> listCheckItem = (List<Map<String, Object>>) retrieveCheckItem.getData().get("rtnData");
            for (Map<String, Object> map : listCheckItem) {
                //替换字段
                Double checkOrder = (Double) map.get("checkOrder");
                map.remove("checkOrder");
                map.put("checkItemOrder", checkOrder);
                recodingModelId = (String) map.get("recodingModelId");
                if (recodingModelId != null && recodingModelId != "") {
                    //查询记录方式  时间
                    DSParamBuilder dsParamDsBuilder = new DSParamBuilder(25);
                    dsParamDsBuilder.buildCondition("rid", "equals", recodingModelId);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveRecoding = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
                    List<Map<String, Object>> listRecoding = (List<Map<String, Object>>) retrieveRecoding.getData().get("rtnData");
                    String recodingModel = (String) listRecoding.get(0).get("recodingModel");
                    Integer time = (Integer) listRecoding.get(0).get("time");
                    map.put("recodingModel", recodingModel);
                    map.put("time", time);
                    resultList.add(map);
                } else {
                    resultList.add(map);
                }
            }
        } else {
            return ResponseVo.success(resultMap, retrieveCheckItem.getMessage());
        }
        resultMap.put("rtnData", resultList);
        return ResponseVo.success(resultMap);
    }
}
