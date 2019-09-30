package com.tianyi.datacenter.inspect.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.liaochong.myexcel.utils.AttachmentExportUtil;
import com.tianyi.datacenter.common.util.ExcelLogs;
import com.tianyi.datacenter.common.util.ExcelUtil2;
import com.tianyi.datacenter.common.util.StringUtil;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import com.tianyi.datacenter.inspect.service.CheckItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Service
@Slf4j
public class CheckItemServiceImpl implements CheckItemService {
    public static final int imgocrmodelBackup = 18; // 图像识别备选
    public static final int defaultColumnWidth = 13; // 列宽
    public static final int voicetagBackup = 19; // 图像识别备选

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
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdtypeNum = dataCenterFeignService.retrieveId
                (jsonObjectSaveNoPass);
        if (responseVoIdtypeNum.isSuccess()) {
            //查询检查项类别
            DSParamBuilder dsParamBuilder = new DSParamBuilder(12);
            dsParamBuilder.buildCondition("checktypeNum", "equals", checktypeNum);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckType = dataCenterFeignService.retrieve
                    (dsParamBuilder.build());
            if (responseVoIdcheckType.isSuccess() && responseVoIdcheckType.getMessage() == null) {
                return ResponseVo.fail("检查项类别编号已存在,请修改");
                /* List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoIdcheckType.getData()
                .get("rtnData");
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
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckTypeUpdate =
                    dataCenterFeignService.update(dsParamBuilderUpdate.build());
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
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckTypeUpdate = dataCenterFeignService
                        .add(dsParamBuilderSave.build());
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
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckItem = dataCenterFeignService.retrieve
                (dsParamBuilderItem.build());
        if (responseVoIdcheckItem.isSuccess() && responseVoIdcheckItem.getMessage() == null) {
            return ResponseVo.fail("检查项编号重复,请修改");
        }

        //保存
        /**
         {"dataObjectId":11,"data":{"cid":"343","checktypeName":"1","modelId":"1","checkOrder":"1","voice":"2",
         "checktypeNum":"2"}}
         */
        DSParamBuilder dsParamBuilderSave = new DSParamBuilder(11);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckRecodeSave = dataCenterFeignService
                .retrieveId(dsParamBuilderSave.build());
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
        dataSave.put("recodingModelId", (String) param.get("recodingModelId"));
        // daSaveta.put("recodingModel", (String) param.get("recodingModel"));
        // daSaveta.put("time", (String) param.get("time"));
        dataSave.put("resultType", (String) param.get("resultType"));
        dataSave.put("minResult", (String) param.get("minResult"));
        dataSave.put("maxResult", (String) param.get("maxResult"));
        dataSave.put("voiceInfo", (String) param.get("voiceInfo"));
        dataSave.put("guidance", (String) param.get("guidance"));
        dataSave.put("checkOrder", (Double.parseDouble(param.get("checkItemOrder") + "")));
        dsParamBuilderSave.buildData(dataSave);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckTypeUpdate = dataCenterFeignService.add
                (dsParamBuilderSave.build());
        if (responseVoIdcheckTypeUpdate.isSuccess()) {
            if (param.get("recodingModelId") != null && param.get("recodingModelId") != "") {
                //修改记录方式
                //{"dataObjectId":25,"data":{"rid":"1","recodingModel":"康查士","time":"20"},"condition":[{"key":"rid",
                // "condition":"equals","value":"1"}]}
                DSParamBuilder dsParamBuilderSaveRecode = new DSParamBuilder(25);
                Map<String, Object> dataSave2 = new HashMap();
                dataSave2.put("rid", (String) param.get("recodingModelId"));
                dataSave2.put("recodingModel", (String) param.get("recodingModel"));
                dataSave2.put("time", (Integer) param.get("time"));
                dsParamBuilderSaveRecode.buildData(dataSave2);
                dsParamBuilderSaveRecode.buildCondition("rid", "equals", (String) param.get("recodingModelId"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckRecodeUpdate =
                        dataCenterFeignService.update(dsParamBuilderSaveRecode.build());
                if (responseVoIdcheckRecodeUpdate.isSuccess()) {
                    return ResponseVo.success("保存成功!");
                }
            }
        }
        return ResponseVo.success("保存成功!");
    }


    @Override
    @Transactional
    public ResponseVo saveAll(List<Map<String, Object>> param) {

        String checktypeNum1 = (String) param.get(0).get("checktypeNum");
        System.out.println(checktypeNum1);
        Set<String> set = new HashSet();

        Map<String, String> itemMap = new HashMap<>();
        Map<String, String> typeMap = new HashMap<>();

        for (int i = 0; i < param.size(); i++) {
            set.add((String) param.get(i).get("checktypeNum"));
        }
//        String cidType = "";
        //检查项类别的保存  获取每一个检查项类别的ID
        //保存检查项
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
            String deviceNo = (String) param.get(i).get("deviceNo");
            String checkitemNum = (String) param.get(i).get("checkitemNum");
            String stationName = (String) param.get(i).get("stationName");
            String voicetag = (String) param.get(i).get("voicetag");
            String imgocrName = (String) param.get(i).get("imgocrName");
            //保存配置表结束

            String oldCid = "";
            DSParamBuilder dsParamBuilderItemExist = new DSParamBuilder(11);
            dsParamBuilderItemExist.buildCondition("checkitemNum", "equals", checkitemNum);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoCheckItemExist = dataCenterFeignService
                    .retrieve(dsParamBuilderItemExist.build());
            if (responseVoCheckItemExist.isSuccess() && responseVoCheckItemExist.getMessage() == null) {
                //修改
                List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoCheckItemExist.getData()
                        .get("rtnData");
                if (rtnData.size() > 0) {
                    oldCid = (String) rtnData.get(0).get("cid");
                }
                DSParamBuilder dsParamBuilderItemUpdate = new DSParamBuilder(11);
                dsParamBuilderItemUpdate.buildCondition("cid", "equals", oldCid);
                Map<String, Object> itemSave = new HashMap<>();
                itemSave.put("checkitemNum", param.get(i).get("checkitemNum"));
                itemSave.put("checkitemtypeId", checkTypeId);
                //保存记录方式id
                DSParamBuilder dsParamBuilderRecoding = new DSParamBuilder(25);
                dsParamBuilderRecoding.buildCondition("recodingModel", "equals", (String) param.get(i).get
                        ("recodingModel"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding = dataCenterFeignService.retrieve
                        (dsParamBuilderRecoding.build());
                if (responseVoRecoding.isSuccess() && responseVoRecoding.getMessage() == null) {
                    //可能修改记录方式的时间
                    Integer time = (Integer) param.get(i).get("time");
                    List<Map<String, Object>> rtnDataRecoding = (List<Map<String, Object>>) responseVoRecoding
                            .getData().get("rtnData");
                    itemSave.put("recodingModelId", (String) rtnDataRecoding.get(0).get("rid"));
                } else {
                    itemSave.put("recodingModelId", "-1");
                }
                //                    保存图像识别  语音标签
                savevoicetagimgocr(param, i, voicetag, imgocrName, itemSave);
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
            } else {//新增
                DSParamBuilder dsParamBuilderItem = new DSParamBuilder(11);// 获取检查项类别ID
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoCheckItemCid = dataCenterFeignService
                        .retrieveId(dsParamBuilderItem.build());
                if (responseVoCheckItemCid.isSuccess() && responseVoCheckItemCid.getMessage() == null) {
                    DSParamBuilder dsParamBuilderItemSave = new DSParamBuilder(11);
                    Map<String, Object> itemSave = new HashMap<>();

                    itemSave.put("cid", responseVoCheckItemCid.getData().get("rtnData"));

                    itemSave.put("checkitemNum", param.get(i).get("checkitemNum"));
                    itemSave.put("checkitemtypeId", checkTypeId);
                    //保存记录方式id
                    DSParamBuilder dsParamBuilderRecoding = new DSParamBuilder(25);
                    dsParamBuilderRecoding.buildCondition("recodingModel", "equals", (String) param.get(i).get
                            ("recodingModel"));
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding = dataCenterFeignService
                            .retrieve(dsParamBuilderRecoding.build());
                    if (responseVoRecoding.isSuccess() && responseVoRecoding.getMessage() == null) {
                        List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoRecoding.getData()
                                .get("rtnData");
                        itemSave.put("recodingModelId", (String) rtnData.get(0).get("rid"));
                    } else {
                        itemSave.put("recodingModelId", "-1");
                    }
                    // 保存图像识别  语音标签
                    savevoicelagimgocr(param, i, voicetag, imgocrName, itemSave);

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
            }
            //保存配置表
            //获取机号 已有deviceNo
            //获取公司ID  已有  companyName  获取 cid
            String companyId = "";
            DSParamBuilder dsParamBuilderCompany1 = new DSParamBuilder(1);
            dsParamBuilderCompany1.buildCondition("companyName", "equals", param.get(i).get("companyName"));
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCompany = dataCenterFeignService.retrieve
                    (dsParamBuilderCompany1.build());
            if (retrieveCompany.isSuccess() && retrieveCompany.getMessage() == null) {
                List<Map<String, String>> rtnData = (List<Map<String, String>>) retrieveCompany.getData().get
                        ("rtnData");
                if (rtnData.size() > 0 && rtnData != null) {
                    companyId = rtnData.get(0).get("cid");
                }
            } else {
                //保存公司 并获取companyId
                DSParamBuilder dsParamBuildercompanySave = new DSParamBuilder(1);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrievecompanySave = dataCenterFeignService
                        .retrieveId(dsParamBuildercompanySave.build());
                if (retrievecompanySave.isSuccess() && retrievecompanySave.getMessage() == null) {
                    companyId = (String) retrievecompanySave.getData().get("rtnData");
                    Map data = new HashMap();
                    data.put("companyName", (String) param.get(i).get("companyName"));
                    data.put("cid", companyId);
                    dsParamBuildercompanySave.buildData(data);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo add = dataCenterFeignService.add
                            (dsParamBuildercompanySave.build());
                    if (add.isSuccess()) {
                        System.out.println(add.getMessage());
                    }
                }
            }
            //保存机种  并获取机种ID
            String machinetypeId = "";
            DSParamBuilder dsParamBuildermachinetype = new DSParamBuilder(8);
            dsParamBuildermachinetype.buildCondition("typeName", "equals", param.get(i).get("typeName"));
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrievemachinetype = dataCenterFeignService.retrieve
                    (dsParamBuildermachinetype.build());
            if (retrievemachinetype.isSuccess() && retrievemachinetype.getMessage() == null) {
                List<Map<String, String>> rtnData = (List<Map<String, String>>) retrievemachinetype.getData().get
                        ("rtnData");
                if (rtnData.size() > 0 && rtnData != null) {
                    machinetypeId = rtnData.get(0).get("mid");
                }
            } else {
                //保存机种 并获取
                DSParamBuilder dsParamBuildermachinetypeSave = new DSParamBuilder(8);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrievemachinetypeSave = dataCenterFeignService
                        .retrieveId(dsParamBuildermachinetypeSave.build());
                if (retrievemachinetypeSave.isSuccess() && retrievemachinetypeSave.getMessage() == null) {
                    machinetypeId = (String) retrievemachinetypeSave.getData().get("rtnData");
                    Map data = new HashMap();
                    data.put("typeName", (String) param.get(i).get("typeName"));
                    data.put("mid", machinetypeId);
                    data.put("typeNum", machinetypeId);
                    dsParamBuildermachinetypeSave.buildData(data);
                    dataCenterFeignService.add(dsParamBuildermachinetypeSave.build());
                }
            }

            //保存机型  并获取机型ID
            String modelId = "";
            DSParamBuilder dsParamBuildermodel = new DSParamBuilder(9);
            dsParamBuildermodel.buildCondition("modelName", "equals", param.get(i).get("modelName"));
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrievemodel = dataCenterFeignService.retrieve
                    (dsParamBuildermodel.build());
            if (retrievemodel.isSuccess() && retrievemodel.getMessage() == null) {
                List<Map<String, String>> rtnData = (List<Map<String, String>>) retrievemodel.getData().get("rtnData");
                if (rtnData.size() > 0 && rtnData != null) {
                    modelId = rtnData.get(0).get("mid");
                }
            } else {
                //保存机型 并获取modelId
                DSParamBuilder dsParamBuildermodelsave = new DSParamBuilder(9);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrievemodelsave = dataCenterFeignService
                        .retrieveId(dsParamBuildermodelsave.build());
                if (retrievemodelsave.isSuccess() && retrievemodelsave.getMessage() == null) {
                    modelId = (String) retrievemodelsave.getData().get("rtnData");
                    Map data = new HashMap();
                    data.put("modelName", (String) param.get(i).get("modelName"));
                    data.put("mid", modelId);
                    data.put("modelNum", modelId);
                    data.put("machinetypeId", machinetypeId);
                    dsParamBuildermodelsave.buildData(data);
                    dataCenterFeignService.add(dsParamBuildermodelsave.build());
                }
            }
            //获取 检查项ID  itemid  已有检查项编号  获取cid  此时检查项没有是新增
            String itemId = "";
            DSParamBuilder dsParamBuilderCheckItem = new DSParamBuilder(11);
            dsParamBuilderCheckItem.buildCondition("checkitemNum", "equals", checkitemNum);
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckItem = dataCenterFeignService.retrieve
                    (dsParamBuilderCheckItem.build());
            if (retrieveCheckItem.isSuccess() && retrieveCheckItem.getMessage() == null) {
                List<Map<String, String>> rtnData = (List<Map<String, String>>) retrieveCheckItem.getData().get
                        ("rtnData");
                if (rtnData.size() > 0 && rtnData != null) {
                    itemId = rtnData.get(0).get("cid");
                }
            }
            //获取工位Id  已有 工位名称  获取 cid
            String stationId = "";
            DSParamBuilder dsParamBuilderstationcheck = new DSParamBuilder(14);
            dsParamBuilderstationcheck.buildCondition("stationName", "equals", stationName);
            com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveStation = dataCenterFeignService.retrieve
                    (dsParamBuilderstationcheck.build());
            if (retrieveStation.isSuccess() && retrieveStation.getMessage() == null) {
                List<Map<String, String>> rtnData = (List<Map<String, String>>) retrieveStation.getData().get
                        ("rtnData");
                if (rtnData.size() > 0 && rtnData != null) {
                    stationId = rtnData.get(0).get("cid");
                }
            } else {
                //工位没有就新增
                DSParamBuilder dsParamBuilderstationcheckSid = new DSParamBuilder(14);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVocid = dataCenterFeignService.retrieveId
                        (dsParamBuilderstationcheckSid.build());
                if (responseVocid.isSuccess() && responseVocid.getMessage() == null) {
                    stationId = (String) responseVocid.getData().get("rtnData");
                }
                DSParamBuilder dsParamBuilderstationcheckSave = new DSParamBuilder(14);
                Map data = new HashMap();
                data.put("cid", stationId);
                data.put("stationName", stationName);
                dsParamBuilderstationcheckSave.buildData(data);
                dataCenterFeignService.add(dsParamBuilderstationcheckSave.build());
            }
            DSParamBuilder dsParamBuilderStationCheck = new DSParamBuilder(15);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoScid = dataCenterFeignService.retrieveId
                    (dsParamBuilderStationCheck.build());
            if (responseVoScid.isSuccess() && responseVoScid.getMessage() == null) {
                Map data = new HashMap();
                data.put("deviceNo", deviceNo);
                data.put("scid", (String) responseVoScid.getData().get("rtnData"));
                data.put("modelId", modelId);
                data.put("itemId", itemId);
                data.put("stationId", stationId);
                data.put("companyId", companyId);
                dsParamBuilderStationCheck.buildData(data);
                dataCenterFeignService.add(dsParamBuilderStationCheck.build());
            }
            param.get(i).put("results", "成功");
        }
        return ResponseVo.success(param);
    }

    private void savevoicetagimgocr(List<Map<String, Object>> param, int i, String voicetag, String imgocrName,
                                    Map<String, Object> itemSave) {
        if (param.get(i).get("imgocrName") == null || param.get(i).get("imgocrName").equals("")) {
            itemSave.put("imgocr", "-1");
        } else {
            DSParamBuilder dsParamDsBuilderimgocrName = new DSParamBuilder(80);
            dsParamDsBuilderimgocrName.buildCondition("name", "equals", imgocrName);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoimgocrName =
                    dataCenterFeignService.retrieve(dsParamDsBuilderimgocrName.build());
            if (responseVoimgocrName.isSuccess() && responseVoimgocrName.getMessage() == null) {
                List<Map<String, Object>> rtnDataimgocrName = (List<Map<String, Object>>)
                        responseVoimgocrName.getData().get("rtnData");
                if (rtnDataimgocrName != null || rtnDataimgocrName.size() != 0) {
                    itemSave.put("imgocr", (String) rtnDataimgocrName.get(0).get("mid"));
                }
            } else { //保存图像识别
                List imgocrModelList = new ArrayList();
                String replace = imgocrName.replace("，", ",");
                String[] imgocrNames = replace.split(",");
                for (String name : imgocrNames) {
                    dsParamDsBuilderimgocrName.buildCondition("name", "equals", name);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoimgocrName2 =
                            dataCenterFeignService.retrieve(dsParamDsBuilderimgocrName.build());
                    if (responseVoimgocrName2.isSuccess() && responseVoimgocrName2.getMessage() == null) {
                        List<Map<String, Object>> rtnDataimgocrName = (List<Map<String, Object>>)
                                responseVoimgocrName2.getData().get("rtnData");
                        if (rtnDataimgocrName != null || rtnDataimgocrName.size() != 0) {
                            imgocrModelList.add((String) rtnDataimgocrName.get(0).get("mid"));
                        }
                    }
                }
                itemSave.put("imgocr", imgocrModelList.toString()
                        .replace("[", "").replace("]", "").replace(" ", ""));
            }
        }
        //保存语音标签
        if (param.get(i).get("voicetag") == null || param.get(i).get("voicetag").equals("")) {
            itemSave.put("voicetag", "-1");
        } else {
            DSParamBuilder dsParamDsBuildervoicetag = new DSParamBuilder(85);
            dsParamDsBuildervoicetag.buildCondition("name", "equals", param.get(i).get("voicetag"));
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVovoicetag =
                    dataCenterFeignService.retrieve(dsParamDsBuildervoicetag.build());
            if (responseVovoicetag.isSuccess() && responseVovoicetag.getMessage() == null) {
                List<Map<String, Object>> rtnDatavoicetag = (List<Map<String, Object>>)
                        responseVovoicetag.getData().get("rtnData");
                if (rtnDatavoicetag != null || rtnDatavoicetag.size() != 0) {
                    itemSave.put("voicetag", (String) rtnDatavoicetag.get(0).get("tid"));
                }
            } else { //保存语音标签
                Map imgocrModel = new HashMap();
                List voicetagList = new ArrayList();
                String replace = voicetag.replace("，", ",");
                String[] voicetags = replace.split(",");
                for (String name : voicetags) {
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVovoicetagId =
                            dataCenterFeignService.retrieveId(dsParamDsBuildervoicetag.build());
                    String voicetagId = (String) responseVovoicetagId.getData().get("rtnData");
                    imgocrModel.put("tid", voicetagId);
                    imgocrModel.put("name", name);
                    dsParamDsBuildervoicetag.buildData(imgocrModel);
                    dataCenterFeignService.add(dsParamDsBuildervoicetag.build());
                    voicetagList.add(voicetagId);
                }
                itemSave.put("voicetag", voicetagList.toString().
                        replace("[", "").replace("]", "").replace(" ", ""));
            }
        }
    }

    private void savevoicelagimgocr(List<Map<String, Object>> param, int i, String voicetag, String imgocrName,
                                    Map<String, Object> itemSave) {
        savevoicetagimgocr(param, i, voicetag, imgocrName, itemSave);
    }

    private String getCheckTypeId(Map param) {

        String checktypeNum = (String) param.get("checktypeNum");
        String cidType = "";


        //检查项类别不一定  无论是新增还是修改都先删除再新增  如果是修改  首先获取老的  cidType
        //检查项类别{"condition":[{"key":"checktypeNum","condition":"equals","value":"bbb"}],"dataObjectId":12,
        // "pageInfo":{"page":1,"count":0,"pageSize":100,"total":36}}
        DSParamBuilder dsParamBuilderType = new DSParamBuilder(12);
        dsParamBuilderType.buildCondition("checktypeNum", "equals", checktypeNum);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoCheckType = dataCenterFeignService.retrieve
                (dsParamBuilderType.build());
        List<Map<String, Object>> rtnDataType = (List<Map<String, Object>>) responseVoCheckType.getData().get
                ("rtnData");
        if (rtnDataType != null && rtnDataType.size() > 0) {
            cidType = (String) rtnDataType.get(0).get("cid");
        }
        DSParamBuilder dsParamBuilderTypeSave = new DSParamBuilder(12);
        Map<String, Object> typeSave = new HashMap<>();
        if (cidType != null && cidType != "") { //存在就的修改
            //新增检查项类别
            //  修改   保存旧的检查项类别的ID
//            typeMap.put(checktypeNum, cidType);
            typeSave.put("cid", cidType);
            typeSave.put("checktypeName", param.get("checktypeName"));
            typeSave.put("voice", param.get("voice"));
            typeSave.put("checktypeNum", param.get("checktypeNum"));
            typeSave.put("checkOrder", param.get("checkOrderType"));
            dsParamBuilderTypeSave.buildData(typeSave);
            com.tianyi.datacenter.feign.common.vo.ResponseVo update = dataCenterFeignService.update
                    (dsParamBuilderTypeSave.build());
        } else {
            //不存在就的  新增保存新的检查项类别ID
            DSParamBuilder dsParamBuilder = new DSParamBuilder(12);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieveId
                    (dsParamBuilder.build());
            cidType = (String) responseVo.getData().get("rtnData");
//            typeMap.put(checktypeNum, checkTypeCid);
            typeSave.put("cid", cidType);
            typeSave.put("checktypeName", param.get("checktypeName"));
            typeSave.put("voice", param.get("voice"));
            typeSave.put("checktypeNum", param.get("checktypeNum"));
            typeSave.put("checkOrder", param.get("checkOrderType"));
            dsParamBuilderTypeSave.buildData(typeSave);
            com.tianyi.datacenter.feign.common.vo.ResponseVo add = dataCenterFeignService.add(dsParamBuilderTypeSave
                    .build());
            System.out.println("检查项类别新增成功");
        }
        return cidType;
    }

    //获取每一个检查项类别ID  通过新增修改  重复  失败保存检查项


    @Override
    public ResponseVo detail(Map<String, Object> param) {
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(56);
        Map<String, String> condition = (Map<String, String>) param.get("condition");
        dsParamDsBuilder.buildCondition("checktypeNum", condition.get("checktypeNum"));
        dsParamDsBuilder.buildCondition("checkitemNum", condition.get("checkitemNum"));
        Map<String, Object> resultmap = new HashMap<>();

        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoOld = dataCenterFeignService.retrieve
                (dsParamDsBuilder.build());
        if (responseVoOld.isSuccess() && responseVoOld.getMessage() == null) {
            List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoOld.getData().get("rtnData");
            resultmap = rtnData.get(0);
            if (rtnData.size() != 0 && rtnData != null) {
                String recodingModelId = (String) rtnData.get(0).get("recodingModelId");
                //查询记录方式 和 时间
                DSParamBuilder dsParamBuilder = new DSParamBuilder(25);
                dsParamBuilder.buildCondition("rid", "equals", recodingModelId);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding = dataCenterFeignService
                        .retrieve
                                (dsParamBuilder.build());
                if (responseVoRecoding.isSuccess() && responseVoRecoding.getMessage() == null) {
                    List<Map<String, Object>> rtnDataRecoding = (List<Map<String, Object>>) responseVoRecoding
                            .getData().get("rtnData");
                    resultmap.put("recodingModel", (String) rtnDataRecoding.get(0).get("recodingModel"));
                }
            }
        }
        resultmap.put("companyName", condition.get("companyName"));
        resultmap.put("typeName", condition.get("typeName"));
        resultmap.put("modelName", condition.get("modelName"));
        resultmap.put("deviceNo", condition.get("deviceNo"));
        resultmap.put("stationName", condition.get("stationName"));
        //查询图像识别和语音标签
        String imgocrName = condition.get("imgocrName");
        String voicetag = condition.get("voicetag");
        if (StringUtil.isEmpty(imgocrName)) {
            resultmap.put("imgocrName", "");
        } else {
            if (imgocrName.contains(",")) {
                String[] splitimgocrName = imgocrName.split(",");
                List<String> imgocrNameList = new ArrayList();
                for (String imgocrname : splitimgocrName) {
                    DSParamBuilder dsParamDsBuilderimgocrName = new DSParamBuilder(80);
                    dsParamDsBuilderimgocrName.buildCondition("mid", "equals", imgocrname);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoimgocrName =
                            dataCenterFeignService.retrieve(dsParamDsBuilderimgocrName.build());
                    if (responseVoimgocrName.isSuccess() && responseVoimgocrName.getMessage() == null) {
                        List<Map<String, String>> rtnData = (List<Map<String, String>>) responseVoimgocrName.getData()
                                .get("rtnData");
                        String name = rtnData.get(0).get("name");
                        imgocrNameList.add(name);
                    }
                }
                resultmap.put("imgocrName", imgocrNameList.toString().replace("[", "").replace("]", "")
                        .replace(" ", ""));
            } else {
                DSParamBuilder dsParamDsBuilderimgocrName = new DSParamBuilder(80);
                dsParamDsBuilderimgocrName.buildCondition("mid", "equals", imgocrName);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoimgocrName =
                        dataCenterFeignService.retrieve(dsParamDsBuilderimgocrName.build());
                if (responseVoimgocrName.isSuccess() && responseVoimgocrName.getMessage() == null) {
                    List<Map<String, String>> rtnData = (List<Map<String, String>>) responseVoimgocrName.getData()
                            .get("rtnData");
                    resultmap.put("imgocrName", rtnData.get(0).get("name"));
                }
            }
        }
        // 语音标签
        if (StringUtil.isEmpty(voicetag)) {
            resultmap.put("voicetag", "");
        } else {
            if (voicetag.contains(",")) {
                String[] splitvoicetag = voicetag.split(",");
                List<String> voicetagList = new ArrayList();
                for (String voicetagId : splitvoicetag) {
                    DSParamBuilder dsParamDsBuildervoicetag = new DSParamBuilder(85);
                    dsParamDsBuildervoicetag.buildCondition("tid", "equals", voicetagId);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVovoicetag =
                            dataCenterFeignService.retrieve(dsParamDsBuildervoicetag.build());
                    if (responseVovoicetag.isSuccess() && responseVovoicetag.getMessage() == null) {
                        List<Map<String, String>> rtnData = (List<Map<String, String>>) responseVovoicetag.getData()
                                .get("rtnData");
                        String name = rtnData.get(0).get("name");
                        voicetagList.add(name);
                    }
                }
                resultmap.put("voicetag", voicetagList.toString().replace("[", "").replace("]", "")
                        .replace(" ", ""));
            } else {
                DSParamBuilder dsParamDsBuilderimgocrName = new DSParamBuilder(80);
                dsParamDsBuilderimgocrName.buildCondition("mid", "equals", imgocrName);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoimgocrName =
                        dataCenterFeignService.retrieve(dsParamDsBuilderimgocrName.build());
                if (responseVoimgocrName.isSuccess() && responseVoimgocrName.getMessage() == null) {
                    List<Map<String, String>> rtnData = (List<Map<String, String>>) responseVoimgocrName.getData()
                            .get("rtnData");
                    resultmap.put("imgocrName", rtnData.get(0).get("name"));
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
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckType = dataCenterFeignService.retrieve
                (dsParamBuilder.build());

        List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoIdcheckType.getData().get
                ("rtnData");

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
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckTypeUpdate = dataCenterFeignService
                    .update(dsParamBuilderUpdate.build());
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

        //修改检查项
        JSONObject jsonObjectSaveItem = new JSONObject();
        jsonObjectSaveItem.put("dataObjectId", 11);

        //查询检查项
        DSParamBuilder dsParamBuilderItem = new DSParamBuilder(11);
        dsParamBuilderItem.buildCondition("cid", "equals", checkitemNum);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckItem = dataCenterFeignService.retrieve
                (dsParamBuilderItem.build());

        List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoIdcheckItem.getData().get
                ("rtnData");
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
            data.put("recodingModelId", (String) param.get("recodingModelId"));
//                    data.put("recodingModel", (String) param.get("recodingModel"));
//                    data.put("time", (String) param.get("time"));
            data.put("resultType", (String) param.get("resultType"));
            data.put("minResult", (String) param.get("minResult"));
            data.put("maxResult", (String) param.get("maxResult"));
            data.put("voiceInfo", (String) param.get("voiceInfo"));
            data.put("guidance", (String) param.get("guidance"));
            data.put("checkOrder", (Double.parseDouble(param.get("checkItemOrder") + "")));
            dsParamBuilderUpdate.buildCondition("cid", "equals", checkitemNum);
            dsParamBuilderUpdate.buildData(data);
            System.out.println(dsParamBuilderUpdate.build());
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckTypeUpdate = dataCenterFeignService
                    .update(dsParamBuilderUpdate.build());
            if (responseVoIdcheckTypeUpdate.isSuccess()) {
                //修改记录方式  修改或者保存
                //{"dataObjectId":25,"data":{"rid":"1","recodingModel":"康查士","time":"20"},"condition":[{"key":"rid",
                // "condition":"equals","value":"1"}]}
                DSParamBuilder dsParamBuilderUpdateRecode = new DSParamBuilder(25);
                Map dataSave = new HashMap();
                dataSave.put("rid", (String) param.get("recodingModelId"));
                dataSave.put("recodingModel", (String) param.get("recodingModel"));
                dataSave.put("time", (Integer) param.get("time"));
                dsParamBuilderUpdateRecode.buildData(dataSave);
                dsParamBuilderUpdateRecode.buildCondition("rid", "equals", (String) param.get("recodingModelId"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckRecodeUpdate =
                        dataCenterFeignService.update(dsParamBuilderUpdateRecode.build());
                if (responseVoIdcheckRecodeUpdate.isSuccess()) {
                    return ResponseVo.success("修改成功!");
                } else {
                    //新增
                    JSONObject jsonObjectSaveRecode = new JSONObject();
                    jsonObjectSaveRecode.put("dataObjectId", 25);
                    //检查工单项目ID  雪花ID
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseRecodeId = dataCenterFeignService
                            .retrieveId(jsonObjectSaveRecode);
                    if (responseRecodeId.isSuccess()) {
                        //{"dataObjectId":25,"data":{"rid":"8","recodingModel":"88","time":"8"}}
                        DSParamBuilder dsParamBuilderSaveRecode = new DSParamBuilder(25);
                        Map dataUpdate = new HashMap();
                        dataUpdate.put("rid", (String) responseRecodeId.getData().get("rtnData"));
                        dataUpdate.put("recodingModel", (String) param.get("recodingModel"));
                        dataUpdate.put("time", (Integer) param.get("time"));
                        dsParamBuilderSaveRecode.buildData(dataUpdate);
                        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckRecodeSave =
                                dataCenterFeignService.add(dsParamBuilderSaveRecode.build());
                        if (responseVoIdcheckRecodeSave.isSuccess()) {
                            return ResponseVo.success("修改成功!");
                        }
                    }
                }
            }

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
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckItem = dataCenterFeignService.retrieve
                (dsParamBuilder.build());
        String recodingModelId = "";
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (retrieveCheckItem.isSuccess() && retrieveCheckItem.getMessage() == null) {
            List<Map<String, Object>> listCheckItem = (List<Map<String, Object>>) retrieveCheckItem.getData().get
                    ("rtnData");
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
                    com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveRecoding = dataCenterFeignService
                            .retrieve(dsParamDsBuilder.build());
                    List<Map<String, Object>> listRecoding = (List<Map<String, Object>>) retrieveRecoding.getData()
                            .get("rtnData");
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

    @Override
    public void exportmodel(HttpServletResponse response) {
        DSParamBuilder dsParamBuilder = new DSParamBuilder(80);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamBuilder
                .build());
        List<Map<String, Object>> list = null;
        String[] imgocrmodels = null;
        if (retrieve.isSuccess() && retrieve.getMessage() == null) {
            list = (List<Map<String, Object>>) retrieve.getData().get("rtnData");
            imgocrmodels = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                imgocrmodels[i] = (String) list.get(i).get("name");
            }
        }
        DSParamBuilder dsParamBuilderRecoding = new DSParamBuilder(25);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveRecoding = dataCenterFeignService.retrieve(dsParamBuilderRecoding
                .build());
        List<Map<String, Object>> listRecoding = null;
        String[] recodingmodels = null;
        if (retrieveRecoding.isSuccess() && retrieveRecoding.getMessage() == null) {
            listRecoding = (List<Map<String, Object>>) retrieveRecoding.getData().get("rtnData");
            recodingmodels = new String[listRecoding.size()];
            for (int i = 0; i < listRecoding.size(); i++) {
                recodingmodels[i] = (String) listRecoding.get(i).get("recodingModel");
            }
        }
        Workbook workbook = XSSFSetDropDownAndHidden(imgocrmodels,recodingmodels);
        // 输出
        AttachmentExportUtil.export(workbook, "巡检表批量处理模板", response);

    }

    @Override
    public ResponseVo importExcel(MultipartFile file) {
        try {
            InputStream inputStream = null;
            List<Map> importExcel = new ArrayList<>();
            try {
                inputStream = file.getInputStream();
                ExcelLogs logs = new ExcelLogs();
                importExcel = ExcelUtil2.importExcel(Map.class, inputStream, "yyyy/MM/dd HH:mm:ss", logs, 0);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (importExcel.size() == 0) {
                return ResponseVo.fail("表中没有数据");
            }
            for (int i = 0; i < importExcel.size(); i++) {
                String companyName = (String) importExcel.get(i).get("公司");
                if (companyName == null) {
                    importExcel.remove(importExcel.get(i));
                    i--;
                }
            }
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Map maps : importExcel) {
                Map<String, Object> map = new HashMap<>();
                String companyName = (String) maps.get("公司");
                String typeName = (String) maps.get("资产种类");
                String modelName = (String) maps.get("型号");
                String deviceNo = (String) (maps.get("机号"));
                String stationName = (String) maps.get("工位");
                String checktypeNum = (String) maps.get("巡检项类别编号");
                String checktypeName = (String) maps.get("巡检项类别名称");
                String voice = (String) maps.get("类别语音提示");
                String checkitemNum = (String) maps.get("巡检项编号");
                String checkitemName = (String) maps.get("巡检项名称");
                String recodingModel = (String) maps.get("记录方式");
                String standard = (String) maps.get("判断标准");
                String voiceInfo = (String) maps.get("检查项语音提示");
                String guidance = (String) maps.get("作业指导");
                String imgocrName = (String) maps.get("图像识别模型");
                String voicetag = (String) maps.get("语音标签");

                //失败状态开始判断
                //不是int或者double就失败
                //类别显示顺序  和  显示顺序  有一个不对类型都是失败
                Double checkOrderItem = 0.0;
                Double checkOrderType = 0.0;
                try {
                    checkOrderType = Double.parseDouble((String) maps.get("类别显示顺序"));
                    map.put("checkOrderType", checkOrderType);
                } catch (Exception e) {
                    map.put("results", "失败");
                    map.put("details", "导入失败，导入的显示顺序或者类别显示顺序类型不符合!");
                    map.put("checkOrderType", maps.get("类别显示顺序"));
                }
                try {
                    checkOrderItem = Double.parseDouble((String) maps.get("显示顺序"));
                    map.put("checkOrderItem", checkOrderItem);
                } catch (Exception e) {
                    map.put("results", "失败");
                    map.put("details", "导入失败，导入的显示顺序或者类别显示顺序类型不符合!");
                    map.put("checkOrderItem", maps.get("显示顺序"));
                }
                map.put("companyName", companyName);
                map.put("typeName", typeName);
                map.put("modelName", modelName);
                map.put("deviceNo", deviceNo);
                map.put("stationName", stationName);
                map.put("checktypeNum", checktypeNum);
                map.put("checktypeName", checktypeName);
                map.put("voice", voice);
                map.put("imgocrName", imgocrName);
                map.put("checkitemNum", checkitemNum);
                map.put("checkitemName", checkitemName);
                map.put("recodingModel", recodingModel);
                map.put("standard", standard);
                map.put("voiceInfo", voiceInfo);
                map.put("guidance", guidance);
                map.put("voicetag", voicetag);
                if (companyName == null || companyName == "" ||
                        typeName == null || typeName == "" ||
                        modelName == null || modelName == "" ||
                        stationName == null || stationName == null ||
                        checktypeNum == "" || checktypeNum == null ||
                        checktypeName == null || checktypeName == "" ||
                        checkitemNum == null || checkitemNum == "" ||
                        checkitemName == null || checkitemName == "") {
                    map.put("results", "失败");
                    map.put("details", "必填项为空,请更改后重新导入!");
                }

                if ("失败".equals(map.get("results"))) {
                    resultList.add(map);
                    continue;
                }
                //记录方式失败
                DSParamBuilder dsParamBuilderRecoding = new DSParamBuilder(25);
                dsParamBuilderRecoding.buildCondition("recodingModel", "equals", recodingModel);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding = dataCenterFeignService
                        .retrieve
                                (dsParamBuilderRecoding.build());
                if (recodingModel == null || recodingModel == "") {
                } else {
                    if (responseVoRecoding.isSuccess() && responseVoRecoding.getMessage() == null) {
                    } else {
                        map.put("results", "失败");
                        map.put("details", "您填写的记录方式与基础数据中配置的不相符,请重新填写!");
                    }
                }
                if ("失败".equals(map.get("results"))) {
                    resultList.add(map);
                    continue;
                }
                //公司失败
                DSParamBuilder dsParamBuilder1 = new DSParamBuilder(11);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve
                        (dsParamBuilder1.build());
                if (retrieve.isSuccess() && retrieve.getMessage() != null) {
                    map.put("results", "新增");
                    map.put("details", "");
                }
                DSParamBuilder dsParamBuilder2 = new DSParamBuilder(12);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveType = dataCenterFeignService.retrieve
                        (dsParamBuilder2.build());
                if (retrieveType.isSuccess() && retrieveType.getMessage() != null) {
                    map.put("results", "新增");
                    map.put("details", "");
                }
                //判断工位检查配置是否存在
                String modelIdSelect = "";
                DSParamBuilder dsParamBuilderModel = new DSParamBuilder(9);
                dsParamBuilderModel.buildCondition("modelName", "equals", modelName);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveModel = dataCenterFeignService.retrieve
                        (dsParamBuilderModel.build());
                if (retrieveModel.isSuccess() && retrieveModel.getMessage() == null) {
                    List<Map<String, String>> rtnData = (List<Map<String, String>>) retrieveModel.getData().get
                            ("rtnData");
                    if (rtnData.size() > 0 && rtnData != null) {
                        modelIdSelect = rtnData.get(0).get("mid");
                    } else {
                        map.put("results", "新增");
                        map.put("details", "");
                    }
                }
                //获取 检查项ID  itemid  已有检查项编号  获取cid
                String itemId = "";
                DSParamBuilder dsParamBuilderCheckItem = new DSParamBuilder(11);
                dsParamBuilderCheckItem.buildCondition("checkitemNum", "equals", checkitemNum);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckItem = dataCenterFeignService.retrieve
                        (dsParamBuilderCheckItem.build());
                if (retrieveCheckItem.isSuccess() && retrieveCheckItem.getMessage() == null) {
                    List<Map<String, String>> rtnData = (List<Map<String, String>>) retrieveCheckItem.getData().get
                            ("rtnData");
                    if (rtnData.size() > 0 && rtnData != null) {
                        itemId = rtnData.get(0).get("cid");
                    } else {
                        map.put("results", "新增");
                        map.put("details", "");
                    }
                }
                //获取工位Id  已有 工位名称  获取 cid
                String stationId = "";
                DSParamBuilder dsParamBuilderstationcheck = new DSParamBuilder(14);
                dsParamBuilderstationcheck.buildCondition("stationName", "equals", stationName);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveStation = dataCenterFeignService.retrieve
                        (dsParamBuilderstationcheck.build());
                if (retrieveStation.isSuccess() && retrieveStation.getMessage() == null) {
                    List<Map<String, String>> rtnData = (List<Map<String, String>>) retrieveStation.getData().get
                            ("rtnData");
                    if (rtnData.size() > 0 && rtnData != null) {
                        stationId = rtnData.get(0).get("cid");
                    } else {
                        map.put("results", "新增");
                        map.put("details", "");
                    }
                }
                //获取公司ID  已有  companyName  获取 cid
                String companyId = "";
                DSParamBuilder dsParamBuilderCompany1 = new DSParamBuilder(1);
                dsParamBuilderCompany1.buildCondition("companyName", "equals", companyName);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCompany = dataCenterFeignService.retrieve
                        (dsParamBuilderCompany1.build());
                if (retrieveCompany.isSuccess() && retrieveCompany.getMessage() == null) {
                    List<Map<String, String>> rtnData = (List<Map<String, String>>) retrieveCompany.getData().get
                            ("rtnData");
                    if (rtnData.size() > 0 && rtnData != null) {
                        companyId = rtnData.get(0).get("cid");
                    } else {
                        map.put("results", "新增");
                        map.put("details", "");
                    }
                }
                if ("新增".equals(map.get("results"))) {
                    resultList.add(map);
                    continue;
                }
                //判断工位检查配置是否存在
                DSParamBuilder dsParamDsBuilder = new DSParamBuilder(15);
                if (deviceNo != null && deviceNo != "") {
                    dsParamDsBuilder.buildCondition("deviceNo", "equals", deviceNo);
                }
                dsParamDsBuilder.buildCondition("modelId", "equals", modelIdSelect);
                dsParamDsBuilder.buildCondition("itemId", "equals", itemId);
                dsParamDsBuilder.buildCondition("stationId", "equals", stationId);
                dsParamDsBuilder.buildCondition("companyId", "equals", companyId);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoStation = dataCenterFeignService.retrieve
                        (dsParamDsBuilder.build());
                if (responseVoStation.isSuccess() && responseVoStation.getMessage() == null) {
                    List<Map<String, String>> rtnData = (List<Map<String, String>>) responseVoStation.getData().get
                            ("rtnData");
                    String recodingModelId = "";
                    if (rtnData.size() > 0) {
                        //判断  项和类别的内容是不是重复  不是就是修改
                        dsParamBuilderCheckItem.buildCondition("checkitemName", "equals", checkitemName);
                        //根据记录方式查询他的ID
                        if (recodingModel != null && recodingModel != "") {
                            DSParamBuilder dsParamDsBuilderRecodingModel = new DSParamBuilder(25);
                            dsParamDsBuilderRecodingModel.buildCondition("recodingModel", "equals", recodingModel);
                            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecodingModel =
                                    dataCenterFeignService.retrieve(dsParamDsBuilderRecodingModel.build());
                            if (responseVoRecodingModel.isSuccess() && responseVoRecodingModel.getMessage() ==
                                    null) {
                                List<Map<String, Object>> rtnDataRecodingModel = (List<Map<String, Object>>)
                                        responseVoRecodingModel.getData().get("rtnData");
                                if (rtnDataRecodingModel != null || rtnDataRecodingModel.size() != 0) {
                                    recodingModelId = (String) rtnDataRecodingModel.get(0).get("rid");
                                    dsParamBuilderCheckItem.buildCondition("recodingModelId", "equals",
                                            recodingModelId);
                                }
                            }
                        } else {
                            dsParamBuilderCheckItem.buildCondition("recodingModelId", "equals", "");
                        }
                    }
                    String mId = "-2";
                    String tId = "-2";
                    if (rtnData.size() > 0) {
                        //  图像识别模型条件添加
                        if (imgocrName != null && imgocrName != "") {
                            DSParamBuilder dsParamDsBuilderimgocrName = new DSParamBuilder(80);
                            dsParamDsBuilderimgocrName.buildCondition("name", "equals", imgocrName);
                            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoimgocrName =
                                    dataCenterFeignService
                                            .retrieve(dsParamDsBuilderimgocrName.build());
                            if (responseVoimgocrName.isSuccess() && responseVoimgocrName.getMessage() == null) {
                                List<Map<String, Object>> rtnDataimgocrName = (List<Map<String, Object>>)
                                        responseVoimgocrName.getData().get("rtnData");
                                if (rtnDataimgocrName != null || rtnDataimgocrName.size() != 0) {
                                    mId = (String) rtnDataimgocrName.get(0).get("mid");
                                    dsParamBuilderCheckItem.buildCondition("imgocr", "equals", mId);
                                }
                            } else {
                                map.put("results", "修改");
                                map.put("details", "");
                                resultList.add(map);
                                continue;
                            }
                        } else {
                            dsParamBuilderCheckItem.buildCondition("imgocr", "equals", mId);
                        }
                        // 语音标签条件添加
                        if (voicetag != null && voicetag != "") {
                            DSParamBuilder dsParamDsBuildervoicetag = new DSParamBuilder(85);
                            dsParamDsBuildervoicetag.buildCondition("name", "equals", voicetag);
                            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVovoicetag =
                                    dataCenterFeignService
                                            .retrieve(dsParamDsBuildervoicetag.build());
                            if (responseVovoicetag.isSuccess() && responseVovoicetag.getMessage() == null) {
                                List<Map<String, Object>> rtnDatavoicetag = (List<Map<String, Object>>)
                                        responseVovoicetag
                                                .getData().get("rtnData");
                                if (rtnDatavoicetag != null || rtnDatavoicetag.size() != 0) {
                                    tId = (String) rtnDatavoicetag.get(0).get("tid");
                                    dsParamBuilderCheckItem.buildCondition("voicetag", "equals", tId);
                                }
                            } else {
                                map.put("results", "修改");
                                map.put("details", "");
                                resultList.add(map);
                                continue;
                            }
                        } else {
                            dsParamBuilderCheckItem.buildCondition("voicetag", "equals", tId);
                        }
                    }
                    if (standard != null) {
                        dsParamBuilderCheckItem.buildCondition("standard", "equals", standard);
                    }
                    if (voiceInfo != null) {
                        dsParamBuilderCheckItem.buildCondition("voiceInfo", "equals", voiceInfo);
                    }
                    if (guidance != null) {
                        dsParamBuilderCheckItem.buildCondition("guidance", "equals", guidance);
                    }
                    dsParamBuilderCheckItem.buildCondition("recodingModelId", "equals", recodingModelId);
                    dsParamBuilderCheckItem.buildCondition("checkOrder", "equals", checkOrderItem);
                    //查询条件加上检查项类别ID
                    DSParamBuilder dsParamBuilderType = new DSParamBuilder(12);
                    dsParamBuilderType.buildCondition("checktypeNum", "equals", checktypeNum);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoCheckType = dataCenterFeignService
                            .retrieve(dsParamBuilderType.build());
                    if (responseVoCheckType.isSuccess() && responseVoCheckType.getMessage() == null) {
                        List<Map<String, Object>> rtnDataType = (List<Map<String, Object>>) responseVoCheckType
                                .getData().get("rtnData");
                        dsParamBuilderCheckItem.buildCondition("checkitemtypeId", "equals", rtnDataType.get(0).get
                                ("cid"));
                    } else {
                        dsParamBuilderCheckItem.buildCondition("checkitemtypeId", "equals", "");
                    }
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoAllItem = dataCenterFeignService
                            .retrieve(dsParamBuilderCheckItem.build());
                    System.out.println(dsParamDsBuilder.build());
                    if (responseVoAllItem.isSuccess() && responseVoAllItem.getMessage() == null) {
                        List<Map<String, Object>> rtnDataAllItem = (List<Map<String, Object>>) responseVoAllItem
                                .getData().get("rtnData");
                        if (rtnDataAllItem != null || rtnDataAllItem.size() != 0) {
                            //判断项类别是不是内容一致
                            DSParamBuilder dsParamBuilderCheckType = new DSParamBuilder(12);
                            if (voice != "" && voice != null) {
                                dsParamBuilderCheckType.buildCondition("voice", "equals", voice);
                            }
                            dsParamBuilderCheckType.buildCondition("checktypeNum", "equals", checktypeNum);
                            dsParamBuilderCheckType.buildCondition("checktypeName", "equals", checktypeName);
                            dsParamBuilderCheckType.buildCondition("checkOrder", "equals", checkOrderType);
                            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoAllType =
                                    dataCenterFeignService.retrieve(dsParamBuilderCheckType.build());
                            if (responseVoAllType.isSuccess() && responseVoAllType.getMessage() == null) {
                                List<Map<String, Object>> rtnDataAllType = (List<Map<String, Object>>)
                                        responseVoAllType.getData().get("rtnData");
                                if (rtnDataAllType != null || rtnDataAllType.size() != 0) {
                                    map.put("results", "重复");
                                    map.put("details", "");
                                    resultList.add(map);
                                    continue;
                                }
                            } else {
                                map.put("results", "修改");
                                map.put("details", "");
                                resultList.add(map);
                                continue;
                            }
                        }
                    } else {
                        map.put("results", "修改");
                        map.put("details", "");
                        resultList.add(map);
                        continue;
                    }
                } else {
                    map.put("results", "新增");
                    map.put("details", "");
                    resultList.add(map);
                    continue;
                }
            }
            try {


                Collections.sort(resultList, new Comparator<Map<String, Object>>() {
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        Double name1 = (Double) o1.get("checkOrderType");
                        Double name2 = (Double) o2.get("checkOrderType");
                        return name1.compareTo(name2);
                    }

                });
                Collections.sort(resultList, new Comparator<Map<String, Object>>() {
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        Double name1 = (Double) o1.get("checkOrderItem");
                        Double name2 = (Double) o2.get("checkOrderItem");
                        return name1.compareTo(name2);
                    }
                });
            } catch (Exception e) {
                return ResponseVo.success(resultList);
            }
            return ResponseVo.success(resultList);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseVo.fail("导入失败");
        }
    }

    @Override
    public ResponseVo reltimestatus(Map<String, Object> map) {
        map.remove("results");
        map.remove("details");
        //导入的
        String companyName = (String) map.get("companyName");
        String typeName = (String) map.get("typeName");
        String modelName = (String) map.get("modelName");
        String deviceNo = (String) (map.get("deviceNo"));
        String stationName = (String) map.get("stationName");
        String checktypeNum = (String) map.get("checktypeNum");
        String checktypeName = (String) map.get("checktypeName");
        String voice = (String) map.get("voice");
        String checkitemNum = (String) map.get("checkitemNum");
        String checkitemName = (String) map.get("checkitemName");
        String recodingModel = (String) map.get("recodingModel");
        String standard = (String) map.get("standard");
        String voiceInfo = (String) map.get("voiceInfo");
        String guidance = (String) map.get("guidance");
        String imgocrName = (String) map.get("imgocrName");
        String voicetag = (String) map.get("voicetag");


        Double checkOrderItem = 0.0;
        Double checkOrderType = 0.0;
        try {
            checkOrderType = Double.parseDouble((String) (map.get("checkOrderType") + ""));
            map.put("checkOrderType", checkOrderType);
        } catch (Exception e) {
            map.put("results", "失败");
            map.put("details", "导入失败，导入的显示顺序或者类别显示顺序类型不符合!");
            map.put("checkOrderType", map.get("checkOrderType"));
        }
        try {
            checkOrderItem = Double.parseDouble((String) (map.get("checkOrderItem") + ""));
            map.put("checkOrderItem", checkOrderItem);
        } catch (Exception e) {
            map.put("results", "失败");
            map.put("details", "导入失败，导入的显示顺序或者类别显示顺序类型不符合!");
            map.put("checkOrderItem", map.get("checkOrderItem"));
        }
        map.put("companyName", companyName);
        map.put("typeName", typeName);
        map.put("modelName", modelName);
        map.put("deviceNo", deviceNo);
        map.put("stationName", stationName);
        map.put("checktypeNum", checktypeNum);
        map.put("checktypeName", checktypeName);
        map.put("voice", voice);
        map.put("checkitemNum", checkitemNum);
        map.put("checkitemName", checkitemName);
        map.put("recodingModel", recodingModel);
        map.put("standard", standard);
        map.put("voiceInfo", voiceInfo);
        map.put("guidance", guidance);
        map.put("imgocrName", imgocrName);
        map.put("voicetag", voicetag);

        if (companyName == null || companyName == "" ||
                typeName == null || typeName == "" ||
                modelName == null || modelName == "" ||
                stationName == null || stationName == null ||
                checktypeNum == "" || checktypeNum == null ||
                checktypeName == null || checktypeName == "" ||
                checkitemNum == null || checkitemNum == "" ||
                checkitemName == null || checkitemName == "") {
            map.put("results", "失败");
            map.put("details", "必填项为空,请更改后重新导入!");
        }


        if ("失败".equals(map.get("results"))) {
            List<Map<String, Object>> resultList = new ArrayList<>();
            resultList.add(map);
            return ResponseVo.success(resultList);
        }

        String modelId = "";

        //记录方式失败
        //查询记录方式
        //{"condition":[{"key":"recodingModel","condition":"equals","value":"1"}],"dataObjectId":25,
        // "pageInfo":{"page":1,"count":0,"pageSize":100,"total":4}}
        DSParamBuilder dsParamBuilderRecoding = new DSParamBuilder(25);
        dsParamBuilderRecoding.buildCondition("recodingModel", "equals", recodingModel);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding = dataCenterFeignService.retrieve
                (dsParamBuilderRecoding.build());
        if (recodingModel == null || recodingModel == "") {
        } else {
            if (responseVoRecoding.isSuccess() && responseVoRecoding.getMessage() == null) {
            } else {
                map.put("results", "失败");
                map.put("details", "您填写的记录方式与基础数据中配置的不相符,请重新填写!");
            }
        }
        if ("失败".equals(map.get("results"))) {
            List<Map<String, Object>> resultList = new ArrayList<>();
            resultList.add(map);
            return ResponseVo.success(resultList);
        }

        //失败状态判断结束

        //新增状态判断开始
        //检查项检查项类别编号查询数据库都没有的  返回新增  11 检查项  12检查项类别
        DSParamBuilder dsParamBuilder1 = new DSParamBuilder(11);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve
                (dsParamBuilder1.build());
        if (retrieve.isSuccess() && retrieve.getMessage() != null) {
            map.put("results", "新增");
            map.put("details", "");
        }
        DSParamBuilder dsParamBuilder2 = new DSParamBuilder(12);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveType = dataCenterFeignService.retrieve
                (dsParamBuilder2.build());
        if (retrieveType.isSuccess() && retrieveType.getMessage() != null) {
            map.put("results", "新增");
            map.put("details", "");
        }
        //判断工位检查配置是否存在
        //获取机号 已有deviceNo

        //获取 机型ID 已有 modelName 获取mid    9
        String modelIdSelect = "";
        DSParamBuilder dsParamBuilderModel = new DSParamBuilder(9);
        dsParamBuilderModel.buildCondition("modelName", "equals", modelName);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveModel = dataCenterFeignService.retrieve
                (dsParamBuilderModel.build());
        if (retrieveModel.isSuccess() && retrieveModel.getMessage() == null) {
            List<Map<String, String>> rtnData = (List<Map<String, String>>) retrieveModel.getData().get("rtnData");
            if (rtnData.size() > 0 && rtnData != null) {
                modelIdSelect = rtnData.get(0).get("mid");
            } else {
                map.put("results", "新增");
                map.put("details", "");
            }
        }
        //获取 检查项ID  itemid  已有检查项编号  获取cid
        String itemId = "";
        DSParamBuilder dsParamBuilderCheckItem = new DSParamBuilder(11);
        dsParamBuilderCheckItem.buildCondition("checkitemNum", "equals", checkitemNum);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckItem = dataCenterFeignService.retrieve
                (dsParamBuilderCheckItem.build());
        if (retrieveCheckItem.isSuccess() && retrieveCheckItem.getMessage() == null) {
            List<Map<String, String>> rtnData = (List<Map<String, String>>) retrieveCheckItem.getData().get
                    ("rtnData");
            if (rtnData.size() > 0 && rtnData != null) {
                itemId = rtnData.get(0).get("cid");
            } else {
                map.put("results", "新增");
                map.put("details", "");
            }
        }
        //获取工位Id  已有 工位名称  获取 cid
        String stationId = "";
        DSParamBuilder dsParamBuilderstationcheck = new DSParamBuilder(14);
        dsParamBuilderstationcheck.buildCondition("stationName", "equals", stationName);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveStation = dataCenterFeignService.retrieve
                (dsParamBuilderstationcheck.build());
        if (retrieveStation.isSuccess() && retrieveStation.getMessage() == null) {
            List<Map<String, String>> rtnData = (List<Map<String, String>>) retrieveStation.getData().get
                    ("rtnData");
            if (rtnData.size() > 0 && rtnData != null) {
                stationId = rtnData.get(0).get("cid");
            } else {
                map.put("results", "新增");
                map.put("details", "");
            }
        }
        //获取公司ID  已有  companyName  获取 cid
        String companyId = "";
        DSParamBuilder dsParamBuilderCompany1 = new DSParamBuilder(1);
        dsParamBuilderCompany1.buildCondition("companyName", "equals", companyName);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCompany = dataCenterFeignService.retrieve
                (dsParamBuilderCompany1.build());
        if (retrieveCompany.isSuccess() && retrieveCompany.getMessage() == null) {
            List<Map<String, String>> rtnData = (List<Map<String, String>>) retrieveCompany.getData().get
                    ("rtnData");
            if (rtnData.size() > 0 && rtnData != null) {
                companyId = rtnData.get(0).get("cid");
            } else {
                map.put("results", "新增");
                map.put("details", "");
            }
        }

        if ("新增".equals(map.get("results"))) {
            List<Map<String, Object>> resultList = new ArrayList<>();
            resultList.add(map);
            return ResponseVo.success(resultList);
        }
        //判断工位检查配置是否存在
        DSParamBuilder dsParamDsBuilder = new DSParamBuilder(15);
        if (deviceNo != null && deviceNo != "") {
            dsParamDsBuilder.buildCondition("deviceNo", "equals", deviceNo);
        }
        dsParamDsBuilder.buildCondition("modelId", "equals", modelIdSelect);
        dsParamDsBuilder.buildCondition("itemId", "equals", itemId);
        dsParamDsBuilder.buildCondition("stationId", "equals", stationId);
        dsParamDsBuilder.buildCondition("companyId", "equals", companyId);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoStation = dataCenterFeignService.retrieve
                (dsParamDsBuilder.build());
        if (responseVoStation.isSuccess() && responseVoStation.getMessage() == null) {
            List<Map<String, String>> rtnData = (List<Map<String, String>>) responseVoStation.getData().get
                    ("rtnData");
            String recodingModelId = "";
            if (rtnData.size() > 0) {
                //判断  项和类别的内容是不是重复  不是就是修改
                dsParamBuilderCheckItem.buildCondition("checkitemName", "equals", checkitemName);
                //根据记录方式查询他的ID
                if (recodingModel != null && recodingModel != "") {
                    DSParamBuilder dsParamDsBuilderRecodingModel = new DSParamBuilder(25);
                    dsParamDsBuilderRecodingModel.buildCondition("recodingModel", "equals", recodingModel);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecodingModel =
                            dataCenterFeignService
                                    .retrieve(dsParamDsBuilderRecodingModel.build());
                    if (responseVoRecodingModel.isSuccess() && responseVoRecodingModel.getMessage() == null) {
                        List<Map<String, Object>> rtnDataRecodingModel = (List<Map<String, Object>>)
                                responseVoRecodingModel.getData().get("rtnData");
                        if (rtnDataRecodingModel != null || rtnDataRecodingModel.size() != 0) {
                            recodingModelId = (String) rtnDataRecodingModel.get(0).get("rid");
                            dsParamBuilderCheckItem.buildCondition("recodingModelId", "equals", recodingModelId);
                        }
                    }
                }
            } else {
                dsParamBuilderCheckItem.buildCondition("recodingModelId", "equals", "");
            }
            String mId = "-2";//图像识别有修改使用-2作为查询条件 下同
            String tId = "-2";
            if (rtnData.size() > 0) {
                //  图像识别模型条件添加
                if (imgocrName != null && imgocrName != "") {
                    DSParamBuilder dsParamDsBuilderimgocrName = new DSParamBuilder(80);
                    dsParamDsBuilderimgocrName.buildCondition("name", "equals", imgocrName);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoimgocrName = dataCenterFeignService
                            .retrieve(dsParamDsBuilderimgocrName.build());
                    if (responseVoimgocrName.isSuccess() && responseVoimgocrName.getMessage() == null) {
                        List<Map<String, Object>> rtnDataimgocrName = (List<Map<String, Object>>)
                                responseVoimgocrName.getData().get("rtnData");
                        if (rtnDataimgocrName != null || rtnDataimgocrName.size() != 0) {
                            mId = (String) rtnDataimgocrName.get(0).get("mid");
                            dsParamBuilderCheckItem.buildCondition("imgocr", "equals", mId);
                        }
                    }
                } else {
                    dsParamBuilderCheckItem.buildCondition("imgocr", "equals", mId);
                }
                // 语音标签条件添加
                if (voicetag != null && voicetag != "") {
                    DSParamBuilder dsParamDsBuildervoicetag = new DSParamBuilder(85);
                    dsParamDsBuildervoicetag.buildCondition("name", "equals", voicetag);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVovoicetag = dataCenterFeignService
                            .retrieve(dsParamDsBuildervoicetag.build());
                    if (responseVovoicetag.isSuccess() && responseVovoicetag.getMessage() == null) {
                        List<Map<String, Object>> rtnDatavoicetag = (List<Map<String, Object>>) responseVovoicetag
                                .getData().get("rtnData");
                        if (rtnDatavoicetag != null || rtnDatavoicetag.size() != 0) {
                            tId = (String) rtnDatavoicetag.get(0).get("tid");
                            dsParamBuilderCheckItem.buildCondition("voicetag", "equals", mId);
                        }
                    }
                } else {
                    dsParamBuilderCheckItem.buildCondition("voicetag", "equals", tId);
                }
            }

            if (standard != "" && standard != null) {
                dsParamBuilderCheckItem.buildCondition("standard", "equals", standard);
            }
            if (voiceInfo != "" && voiceInfo != null) {
                dsParamBuilderCheckItem.buildCondition("voiceInfo", "equals", voiceInfo);
            }
            if (guidance != "" && guidance != null) {
                dsParamBuilderCheckItem.buildCondition("guidance", "equals", guidance);
            }
            dsParamBuilderCheckItem.buildCondition("recodingModelId", "equals", recodingModelId);
            dsParamBuilderCheckItem.buildCondition("checkOrder", "equals", checkOrderItem);
            //查询条件加上检查项类别ID
            DSParamBuilder dsParamBuilderType = new DSParamBuilder(12);
            dsParamBuilderType.buildCondition("checktypeNum", "equals", checktypeNum);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoCheckType = dataCenterFeignService.retrieve
                    (dsParamBuilderType.build());
            if (responseVoCheckType.isSuccess() && responseVoCheckType.getMessage() == null) {
                List<Map<String, Object>> rtnDataType = (List<Map<String, Object>>) responseVoCheckType.getData()
                        .get
                                ("rtnData");
                dsParamBuilderCheckItem.buildCondition("checkitemtypeId", "equals", rtnDataType.get(0).get("cid"));
            } else {
                dsParamBuilderCheckItem.buildCondition("checkitemtypeId", "equals", "");
            }
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoAllItem = dataCenterFeignService.retrieve
                    (dsParamBuilderCheckItem.build());
            System.out.println(dsParamDsBuilder.build());
            if (responseVoAllItem.isSuccess() && responseVoAllItem.getMessage() == null) {
                List<Map<String, Object>> rtnDataAllItem = (List<Map<String, Object>>) responseVoAllItem.getData()
                        .get("rtnData");
                if (rtnDataAllItem != null || rtnDataAllItem.size() != 0) {
                    //判断项类别是不是内容一致
                    DSParamBuilder dsParamBuilderCheckType = new DSParamBuilder(12);
                    if (voice != "" && voice != null) {
                        dsParamBuilderCheckType.buildCondition("voice", "equals", voice);
                    }
                    dsParamBuilderCheckType.buildCondition("checktypeNum", "equals", checktypeNum);
                    dsParamBuilderCheckType.buildCondition("checktypeName", "equals", checktypeName);
                    dsParamBuilderCheckType.buildCondition("checkOrder", "equals", checkOrderType);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoAllType = dataCenterFeignService
                            .retrieve(dsParamBuilderCheckType.build());
                    if (responseVoAllType.isSuccess() && responseVoAllType.getMessage() == null) {
                        List<Map<String, Object>> rtnDataAllType = (List<Map<String, Object>>) responseVoAllType
                                .getData().get("rtnData");
                        if (rtnDataAllType != null || rtnDataAllType.size() != 0) {
                            map.put("results", "重复");
                            map.put("details", "");
                        }
                    } else {
                        map.put("results", "修改");
                        map.put("details", "");
                    }
                } else {
                    map.put("results", "修改");
                    map.put("details", "");
                }
            } else {
                map.put("results", "修改");
                map.put("details", "");
            }
        } else {
            map.put("results", "新增");
            map.put("details", "");
        }
        List<Map<String, Object>> resultList = new ArrayList<>();
        resultList.add(map);
        return ResponseVo.success(resultList);
    }

    public static Workbook XSSFSetDropDownAndHidden(String[] imgocrmodels,String[] recodingmodels) {
        final int cellIdEnum = 11;
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet workbookSheet = workbook.createSheet("巡检表批量处理模板");
        workbookSheet.setDefaultColumnWidth(defaultColumnWidth);
        XSSFRow row;
        Map<String, Object[]> empinfo = new HashMap<>();
        empinfo.put("1", new Object[]{"公司", "资产种类", "型号", "机号", "工位", "巡检项类别编号", "巡检项类别名称",
                "类别语音提示", "类别显示顺序", "巡检项编号", "巡检项名称", "记录方式", "判断标准", "检查项语音提示", "作业指导", "图像识别模型",
                "语音标签", "显示顺序", "可选图像识别"});
        empinfo.put("2",new Object[]{});
        int rowId = 0;
        Set<String> strings = empinfo.keySet();
        for (String s : strings) {
            if ("2".equals(s)) {
                createFormulaList(workbook, workbookSheet, recodingmodels, cellIdEnum);
            }
            row = workbookSheet.createRow(rowId++);
            Object[] objects = empinfo.get(s);
            int cellId = 0;
            for (Object obj : objects) {
                XSSFCell cell = row.createCell(cellId++);
                if (obj.toString().equals("可选图像识别")) {
                    XSSFCellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue((String) obj);
                } else {
                    cell.setCellValue((String) obj);
                }
            }
        }
        for (int i = 0; i < imgocrmodels.length - 1; i++) {
            row = workbookSheet.createRow(rowId++);
            XSSFCell cell = row.createCell(imgocrmodelBackup);
            cell.setCellValue(imgocrmodels[i]);
        }
        return workbook;
    }

    public static void createFormulaList(Workbook workbook, XSSFSheet workbookSheet, String[] formulaString, Integer
            cellId) {
        // 创建sheet，写入枚举项
        Sheet hideSheet = workbook.createSheet("hiddenSheet");
        for (int i = 0; i < formulaString.length; i++) {
            hideSheet.createRow(i).createCell(0).setCellValue(formulaString[i]);
        }
        // 创建名称，可被其他单元格引用
        Name category1Name = workbook.createName();
        category1Name.setNameName("hidden");
        // 设置名称引用的公式
        // 使用像'A1：B1'这样的相对值会导致在Microsoft Excel中使用工作簿时名称所指向的单元格的意外移动，
        // 通常使用绝对引用，例如'$A$1:$B$1'可以避免这种情况。
        // 参考： http://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Name.html
        category1Name.setRefersToFormula("hiddenSheet!" + "$A$1:$A$" + formulaString.length);
        // 获取上文名称内数据
        DataValidationHelper helper = workbookSheet.getDataValidationHelper();
        DataValidationConstraint constraint = helper.createFormulaListConstraint("hidden");
        // 设置下拉框位置
        CellRangeAddressList addressList = new CellRangeAddressList(0, 600, cellId, cellId);
        DataValidation dataValidation = helper.createValidation(constraint, addressList);
        // 处理Excel兼容性问题
        if (dataValidation instanceof XSSFDataValidation) {
            // 数据校验
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }
        // 作用在目标sheet上
        workbookSheet.addValidationData(dataValidation);
        // 设置hiddenSheet隐藏
        workbook.setSheetHidden(1, true);
    }
}
