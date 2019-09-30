package com.tianyi.datacenter.inspect.controller;

import com.github.liaochong.myexcel.core.DefaultExcelBuilder;
import com.github.liaochong.myexcel.utils.AttachmentExportUtil;
import com.tianyi.datacenter.common.framework.controller.SuperController;
import com.tianyi.datacenter.common.util.ExcelLogs;
import com.tianyi.datacenter.common.util.ExcelUtil;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import com.tianyi.datacenter.inspect.entity.CheckitemExport;
import com.tianyi.datacenter.inspect.entity.CheckitemImport;
import com.tianyi.datacenter.inspect.service.CheckItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by liulele
 */
@Api("检查项操作接口")
@RestController
@RequestMapping("inspect/checkitem")
public class CheckitemController extends SuperController {

    @Autowired
    private CheckItemService checkItemService;

    @Autowired
    private DataCenterFeignService dataCenterFeignService;

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseVo search(@RequestBody Map<String, Object> param) {


        //{"dataObjectId":21,"condition":{"typeName":"挖掘机","modelName":"PC200-8"},
        // "pageInfo":{"page":0,"pageSize":0,"total":7},"userId":"1","menuId":"1559715653583"}
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(21);
        dsParamDsBuilder.buildCondition("typeName", (String) param.get("typeName"));
        dsParamDsBuilder.buildCondition("modelName", (String) param.get("modelName"));
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckType = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        Map resultMap = new HashMap();
        if (responseVoIdcheckType.isSuccess() && responseVoIdcheckType.getMessage() == null) {
            List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoIdcheckType.getData().get("rtnData");
            for (Map<String, Object> rtnDatum : rtnData) {
                String cid = (String) rtnDatum.get("cid");//检查项类别ID
                //统计检查项数
                DSParamDsBuilder dsParamDsBuilderAll = new DSParamDsBuilder(57);
                dsParamDsBuilderAll.buildCondition("cid", cid);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckItemAll = dataCenterFeignService.retrieve(dsParamDsBuilderAll.build());
                if (responseVoIdcheckItemAll.isSuccess()) {
                    List<Map<String, Object>> rtnDataAll = (List<Map<String, Object>>) responseVoIdcheckItemAll.getData().get("rtnData");
                    Integer integer = (Integer) rtnDataAll.get(0).get("COUNT(*)");
                    rtnDatum.put("checkitemNum", integer);
                }
            }
            resultMap.put("rtnData", rtnData);
        }
        return ResponseVo.success(resultMap);
    }

    /**
     * 保存或者新增检查项检查项类别
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseVo update(@RequestBody Map<String, Object> param) {
        ResponseVo responseVo = checkItemService.saveCheckType(param);
        ResponseVo responseVoItem = checkItemService.saveCheckItem(param);
        if (responseVo.isSuccess() && responseVoItem.isSuccess()) {
            return ResponseVo.success("保存成功!");

        }
        return ResponseVo.fail("保存失败!");


    }

    //检查项类别保存修改
    @RequestMapping(value = "/savetype", method = RequestMethod.POST)
    public ResponseVo save(@RequestBody Map<String, Object> param) {
        ResponseVo responseVo = checkItemService.saveCheckType(param);
        return responseVo;
    }

    @RequestMapping(value = "/updatetype", method = RequestMethod.POST)
    public ResponseVo updatetype(@RequestBody Map<String, Object> param) {
        ResponseVo responseVo = checkItemService.updateCheckType(param);
        return responseVo;
    }


    @RequestMapping(value = "/searchitem", method = RequestMethod.POST)
    public ResponseVo searchitem(@RequestBody Map<String, Object> param) {
        ResponseVo responseVoItem = checkItemService.searchCheckItem(param);
        return responseVoItem;
    }

    /**
     * 保存修改检查项
     */
    @RequestMapping(value = "/saveitem", method = RequestMethod.POST)
    public ResponseVo saveCheckItem(@RequestBody Map<String, Object> param) {
        ResponseVo responseVoItem = checkItemService.saveCheckItem(param);
        return responseVoItem;
    }

    @RequestMapping(value = "/updateitem", method = RequestMethod.POST)
    public ResponseVo updateCheckItem(@RequestBody Map<String, Object> param) {
        ResponseVo responseVoItem = checkItemService.updateCheckItem(param);
        return responseVoItem;
    }

    /**
     * 检查项类别目录的删除
     */
    @RequestMapping(value = "/category ", method = RequestMethod.POST)
    public ResponseVo categoryDelete(@RequestBody Map<String, Object> param) {
        ResponseVo responseVo = checkItemService.saveCheckType(param);
        ResponseVo responseVoItem = checkItemService.saveCheckItem(param);
        if (responseVo.isSuccess() && responseVoItem.isSuccess()) {
            return ResponseVo.success("保存成功!");

        }
        return ResponseVo.fail("保存失败!");


    }


    /**
     * 导出模板
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/exportmodel", method = RequestMethod.GET)
    public void exportmodel(HttpServletResponse response) {
        List<CheckitemImport> dataList = new ArrayList<>(1000);
        CheckitemImport checkitemImport = new CheckitemImport();
        dataList.add(0, checkitemImport);

        Workbook workbook = DefaultExcelBuilder.of(CheckitemImport.class).build(dataList);
        AttachmentExportUtil.export(workbook, "检查项批量处理模板", response);
    }

    //确认提交
    @RequestMapping(value = "/saveall", method = RequestMethod.POST)
    public ResponseVo saveall(@RequestBody List<Map<String, Object>> param) {
        ResponseVo responseVo = checkItemService.saveAll(param);
        return responseVo;
    }

    //修改的详情
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public ResponseVo detail(@RequestBody Map<String, Object> param) {
        ResponseVo detail = checkItemService.detail(param);
        return detail;
    }

    /**
     * 导出检查项
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/exportdata", method = RequestMethod.GET)
    //检查项编号
    public void exportdata(@PathParam("checktypeNum") String checktypeNum, HttpServletResponse response) {
        List<CheckitemExport> list = new ArrayList<>();

        Map<String, Object> map = new LinkedHashMap<>();
        //根据检查项类别编号查询检查项类别ID
        DSParamBuilder dsParamBuilder = new DSParamBuilder(12);
        dsParamBuilder.buildCondition("checktypeNum", "equals", checktypeNum);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckType = dataCenterFeignService.retrieve(dsParamBuilder.build());
        if (responseVoIdcheckType.isSuccess() && responseVoIdcheckType.getMessage() == null) {
            List<Map<String, Object>> rtnDatas = (List<Map<String, Object>>) responseVoIdcheckType.getData().get("rtnData");
            if (rtnDatas.size() != 0 && rtnDatas != null) {
                String cid = (String) rtnDatas.get(0).get("cid");
                String modelName = "";

                //根据检查项类别id查询检查项 和机型
                DSParamDsBuilder dsParamDsBuilderItem = new DSParamDsBuilder(48);
                dsParamDsBuilderItem.buildCondition("checktypecid", cid);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdItem = dataCenterFeignService.retrieve(dsParamDsBuilderItem.build());
                if (responseVoIdItem.isSuccess() && responseVoIdItem.getMessage() == null) {
                    List<Map<String, Object>> rtnDataItem = (List<Map<String, Object>>) responseVoIdItem.getData().get("rtnData");
                    if (rtnDataItem.size() != 0 && rtnDataItem != null) {

                        for (Map<String, Object> rtnData : rtnDataItem) {
                            CheckitemExport checkitemExport = new CheckitemExport();
                            checkitemExport.setModelName((String) rtnData.get("modelName"));
                            checkitemExport.setChecktypeNum((String) rtnData.get("checktypeNum"));
                            checkitemExport.setChecktypeName((String) rtnData.get("checktypeName"));
                            checkitemExport.setVoice((String) rtnData.get("voice"));
                            checkitemExport.setCheckTypeOrder((Double) rtnData.get("checkTypeOrder"));

                            checkitemExport.setCheckitemName((String) rtnData.get("checkitemName"));
                            checkitemExport.setCheckitemNum((String) rtnData.get("checkitemNum"));
                            checkitemExport.setStandard((String) rtnData.get("standard"));
                            checkitemExport.setSolveMethod((String) rtnData.get("solveMethod"));
                            checkitemExport.setDecisionMethod((String) rtnData.get("decisionMethod"));
                            checkitemExport.setRecodingModel((String) rtnData.get("recodingModel"));
//                            checkitemExport.setTime((Integer) rtnData.get("time"));

                            checkitemExport.setResultType((String) rtnData.get("resultType"));
                            checkitemExport.setMinResult((String) rtnData.get("minResult"));
                            checkitemExport.setMaxResult((String) rtnData.get("maxResult"));
                            checkitemExport.setVoiceInfo((String) rtnData.get("voiceInfo"));
                            checkitemExport.setGuidance((String) rtnData.get("guidance"));
                            checkitemExport.setCheckOrder((Double) rtnData.get("checkItemOrder"));
                            list.add(checkitemExport);
                        }
                    }
                }
            }
        }
        Workbook workbook = DefaultExcelBuilder.of(CheckitemExport.class).build(list);
        AttachmentExportUtil.export(workbook, "检查项批量导出", response);
    }

    @ApiOperation("实时状态变更")
    @RequestMapping(value = "/reltimestatus", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVo importdemo(@RequestBody Map<String, Object> map) {
        map.remove("results");
        map.remove("details");

        List<Map<String, Object>> resultList = new ArrayList<>();
        //判断机型是否存储，不存在返回文本描述  导入失败，导入中存在不匹配的机型
        String jixing = (String) map.get("modelName");
        if (jixing == null) {
            return ResponseVo.fail("请传入机型参数");
        }
        //查询数据库  不相等返回
        //{"condition":[],"dataObjectId":9,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":10}}
        DSParamBuilder dsParamBuilder = new DSParamBuilder(9);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(dsParamBuilder.build());
        String modelId = "";
        if (responseVo.isSuccess()) {
            List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
            if (rtnData.size() != 0 && rtnData != null) {
                for (int i = 0; i < rtnData.size(); i++) {
                    String modelName = (String) rtnData.get(i).get("modelName");
                    modelId = (String) rtnData.get(i).get("mid");
                    //返回结果不能这样封装想一想
                    if (jixing.equalsIgnoreCase(modelName)) {
                        map.put("results", "");
                        map.put("details", "");
                        break;
                    } else {
                        map.put("results", "失败");
                        map.put("details", "导入失败，导入中存在不匹配的机型");

                    }
                }
            }
        }
        /*DSParamBuilder dsParamBuilderRecoding1 = new DSParamBuilder(25);
        dsParamBuilderRecoding1.buildCondition("recodingModel", "equals", (String)map.get("recodingModel"));
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding1 = dataCenterFeignService.retrieve(dsParamBuilderRecoding1.build());
        if (responseVoRecoding1.isSuccess() && responseVoRecoding1.getMessage() == null) {
        }else {
            map.put("results", "失败");
            map.put("details", "您填写的记录方式与基础数据中配置的不相符,请重新填写!");
        }
        if ("失败".equals(map.get("results"))) {
            resultList.add(map);
            return ResponseVo.success(resultList);
        }*/


        //检查项检查项类别编号查询数据库都没有的  返回新增  11 检查项  12检查项类别
        DSParamBuilder dsParamBuilder1 = new DSParamBuilder(11);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamBuilder1.build());
        if (retrieve.isSuccess() && retrieve.getMessage() != null) {
            map.put("results", "新增");
            map.put("details", "");
        }
        DSParamBuilder dsParamBuilder2 = new DSParamBuilder(12);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveType = dataCenterFeignService.retrieve(dsParamBuilder2.build());
        if (retrieveType.isSuccess() && retrieveType.getMessage() != null) {
            map.put("results", "新增");
            map.put("details", "");
        }


        //导入的
        String jianchaxiangNum = (String) map.get("checkitemNum");
        String jianchaxiangName = (String) map.get("checkitemName");
        String recodingModel = (String) map.get("recodingModel");
//        Integer time = Integer.parseInt(map.get("time") + "");
        String standard = (String) map.get("standard");
        String solvePlan = (String) map.get("solveMethod");
        String method = (String) map.get("decisionMethod");
        String resultType = (String) map.get("resultType");
        String minResult = (String) map.get("minResult");
        String maxResult = (String) map.get("maxResult");
        String voice = (String) map.get("voiceInfo");
        String guidance = (String) map.get("guidance");

        String checkTypeNum = (String) map.get("checktypeNum");
        String checkTypeName = (String) map.get("checktypeName");
        String typeVoice = (String) map.get("voice");

        //不是int或者double就失败
        //类别显示顺序  和  显示顺序  有一个不对类型都是失败
        Double checkOrderItem = 0.0;
        Double typeOrder = 0.0;
        try {
            checkOrderItem = Double.parseDouble(map.get("checkOrderItem") + "");
            typeOrder = Double.parseDouble(map.get("checkOrderType") + "");
        } catch (Exception e) {
            map.put("results", "失败");
            map.put("details", "导入失败，导入的显示顺序或者类别显示顺序类型不符合!");
        }
       //记录方式失败
        DSParamBuilder dsParamBuilderRecoding1 = new DSParamBuilder(25);
        dsParamBuilderRecoding1.buildCondition("recodingModel", "equals", recodingModel);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding1 = dataCenterFeignService.retrieve(dsParamBuilderRecoding1.build());
        if (recodingModel==null||recodingModel==""){
        }else {
            if (responseVoRecoding1.isSuccess() && responseVoRecoding1.getMessage() == null) {
                //查询到了记录方式   记录方式可以是空
                List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoRecoding1.getData().get("rtnData");
            } else {
                map.put("results", "失败");
                map.put("details", "您填写的记录方式与基础数据中配置的不相符,请重新填写!");
            }
        }
        //必填项失败
        if (jixing == null || jixing == "" || jianchaxiangNum == null || jianchaxiangNum == "" ||
                jianchaxiangName == null || jianchaxiangName == ""  ||
                checkOrderItem  == null || checkTypeNum == "" ||
                checkTypeNum == null || checkTypeName == null || checkTypeName == "" || typeOrder == null) {
            map.put("results", "失败");
            map.put("details", "必填项为空,请更改后重新导入!");
        }

        if ("失败".equals(map.get("results"))) {
            resultList.add(map);
            return ResponseVo.success(resultList);
        }


        //根据检查项类别编号查询检查项ID  没有就是新增    有加入检查项的比较中
        String checkitemtypeid = null;
        dsParamBuilder2.buildCondition("checktypeNum","equals",checkTypeNum);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveTypeCid = dataCenterFeignService.retrieve(dsParamBuilder2.build());
        if (retrieveTypeCid.isSuccess() && retrieveTypeCid.getMessage() == null) {
            List<Map<String, Object>> rtnData = (List<Map<String, Object>>) retrieveTypeCid.getData().get("rtnData");
            checkitemtypeid = (String) rtnData.get(0).get("cid");
        }else {
            map.put("results", "新增");
            map.put("details", "");
            resultList.add(map);
            return ResponseVo.success(resultList);
        }
    /*DSParamBuilder dsParamBuilderRecoding1 = new DSParamBuilder(25);
                dsParamBuilderRecoding1.buildCondition("recodingModel", "equals", recodingModel);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding1 = dataCenterFeignService.retrieve(dsParamBuilderRecoding1.build());
                if (responseVoRecoding1.isSuccess() && responseVoRecoding1.getMessage() == null) {
                }else {
                    map.put("results", "失败");
                    map.put("details", "您填写的记录方式与基础数据中配置的不相符,请重新填写!");
                    resultList.add(map);
                    break;
                }*/
        //根据检查项类别编号  查询检查项  没有就是新增
        DSParamBuilder dsParamBuildercheckitemBychecktype = new DSParamBuilder(11);
        dsParamBuildercheckitemBychecktype.buildCondition("checkitemNum","equals",jianchaxiangNum);
        dsParamBuildercheckitemBychecktype.buildCondition("checkitemtypeId","equals",checkitemtypeid);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrievecheckitemBychecktype = dataCenterFeignService.retrieve(dsParamBuildercheckitemBychecktype.build());
        if (retrievecheckitemBychecktype.isSuccess() && retrievecheckitemBychecktype.getMessage() != null) {
            map.put("results", "新增");
            map.put("details", "");
            resultList.add(map);
            return ResponseVo.success(resultList);
        }


        //根据检查项编号查询是否新增或者修改
        DSParamBuilder dsParamBuilderItem = new DSParamBuilder(11);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoItem = dataCenterFeignService.retrieve(dsParamBuilderItem.build());
        if (responseVoItem.isSuccess()&&responseVoItem.getMessage()==null) {
            List<Map<String, Object>> rtnDataItem = (List<Map<String, Object>>) responseVoItem.getData().get("rtnData");
            //条件为true是 有重复或者修改 为false就是新增
            if (rtnDataItem.size() != 0 && rtnDataItem != null) {
                //遍历检查项编号 每一个检查项和每一个导入的去对比 设置状态
                for (int i = 0; i < rtnDataItem.size(); i++) {
                    //查询的数据库中的
                    String checkitemNum = (String) rtnDataItem.get(i).get("checkitemNum");
                    if (jianchaxiangNum.equalsIgnoreCase(checkitemNum)) {
                        //写sql查询  没有匹配 是修改 都匹配是重复
                        //{"condition":{"checkitemNum":"1"},"dataObjectId":52,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":2}}
                        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(52);

                        dsParamDsBuilder.buildCondition("checkitemNum", jianchaxiangNum);
                        dsParamDsBuilder.buildCondition("checkitemName", jianchaxiangName);
                        dsParamDsBuilder.buildCondition("checkitemtypeId", checkitemtypeid);
                        //查询记录方式
                        //{"condition":[{"key":"recodingModel","condition":"equals","value":"1"}],"dataObjectId":25,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":4}}
                        DSParamBuilder dsParamBuilderRecoding = new DSParamBuilder(25);
                        dsParamBuilderRecoding.buildCondition("recodingModel", "equals", recodingModel);
                        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding = dataCenterFeignService.retrieve(dsParamBuilderRecoding.build());
                        if (recodingModel==null||recodingModel==""){
                            dsParamDsBuilder.buildCondition("recodingModelId", "");
                        }else {
                            if (responseVoRecoding.isSuccess() && responseVoRecoding.getMessage() == null) {
                                //查询到了记录方式   记录方式可以是空
                                List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoRecoding.getData().get("rtnData");
                               /* if (!rtnData.get(0).get("time").equals(time)) {
                                    map.put("results", "修改");
                                    map.put("details", "");
                                    resultList.add(map);
                                    break;
                                }*/
                                dsParamDsBuilder.buildCondition("recodingModelId", (String) rtnData.get(0).get("rid"));
                            } else {
                                map.put("results", "失败");
                                map.put("details", "您填写的记录方式与基础数据中配置的不相符,请重新填写!");
                                resultList.add(map);
                                break;
                            }
                        }
                        dsParamDsBuilder.buildCondition("standard", standard);
                        dsParamDsBuilder.buildCondition("solveMethod", solvePlan);
                        dsParamDsBuilder.buildCondition("decisionMethod", method);
                        dsParamDsBuilder.buildCondition("resultType", resultType);
                        dsParamDsBuilder.buildCondition("minResult", minResult);
                        dsParamDsBuilder.buildCondition("maxResult", maxResult);
                        dsParamDsBuilder.buildCondition("voiceInfo", voice);
                        dsParamDsBuilder.buildCondition("guidance", guidance);
                        dsParamDsBuilder.buildCondition("checkOrder", checkOrderItem);
                        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoAllItem = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
                        System.out.println(dsParamDsBuilder.build());
                        if (responseVoAllItem.isSuccess() && responseVoAllItem.getMessage() == null) {
                            List<Map<String, Object>> rtnDataAllItem = (List<Map<String, Object>>) responseVoAllItem.getData().get("rtnData");
                            if (rtnDataAllItem != null || rtnDataAllItem.size() != 0) {
                                //有重复的
                                map.put("results", "重复");
                                map.put("details", "");
                                //判断检查项类别是否重复
                                DSParamDsBuilder dsParamDsBuilderType = new DSParamDsBuilder(54);
                                dsParamDsBuilderType.buildCondition("checktypeNum", checkTypeNum);
                                dsParamDsBuilderType.buildCondition("checktypeName", checkTypeName);
                                dsParamDsBuilderType.buildCondition("voice", typeVoice);
                                dsParamDsBuilderType.buildCondition("checkOrder", typeOrder);
                                dsParamDsBuilderType.buildCondition("modelId", modelId);
                                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoAllType = dataCenterFeignService.retrieve(dsParamDsBuilderType.build());
                                if (responseVoAllType.isSuccess() && responseVoAllType.getMessage() == null) {
                                    List<Map<String, Object>> rtnDataAllType = (List<Map<String, Object>>) responseVoAllType.getData().get("rtnData");
                                    if (rtnDataAllType != null || rtnDataAllType.size() != 0) {
                                        map.put("results", "重复");
                                        map.put("details", "");
                                        resultList.add(map);
                                        break;
                                    }
                                } else {
                                    //遍历检查项类别编号  判断是新增还是修改
                                    boolean flag = false;
                                    DSParamBuilder dsParamBuilderType = new DSParamBuilder(12);
                                    dsParamBuilderType.buildCondition("modelId", "equals", modelId);
                                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoType = dataCenterFeignService.retrieve(dsParamBuilderType.build());
                                    if (responseVoType.isSuccess() && responseVoType.getMessage() == null) {
                                        List<Map<String, Object>> rtnDataType = (List<Map<String, Object>>) responseVoType.getData().get("rtnData");
                                        for (Map<String, Object> stringObjectMap : rtnDataType) {
                                            if (((String) stringObjectMap.get("checktypeNum")).equals(checkTypeNum)) {
                                                map.put("results", "修改");
                                                map.put("details", "");
                                                resultList.add(map);
                                                flag =true;
                                                break;
                                            }
                                        }
                                        if (flag){
                                            break;
                                        }
                                    }
                                    //检查项类别新增
                                    map.put("results", "新增");
                                    map.put("details", "");
                                    resultList.add(map);
                                    break;
                                }
                            }
                        } else {
                            map.put("results", "修改");
                            map.put("details", "");
                            resultList.add(map);
                            break;
                        }
                    }
                }

            }
        }
        if (map.get("results") == null || map.get("results") == "") {
            map.put("results", "新增");
            map.put("details", "");
            resultList.add(map);
        }
        return ResponseVo.success(resultList);
    }


    @ApiOperation("导入状态")
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVo importdemo(@RequestParam("file") MultipartFile file) {
        try {
            InputStream inputStream = null;
            List<Map> importExcel = new ArrayList<>();


            try {
                inputStream = file.getInputStream();
                ExcelLogs logs = new ExcelLogs();
                importExcel = ExcelUtil.importExcel(Map.class, inputStream, "yyyy/MM/dd HH:mm:ss", logs, 0);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (importExcel.size() == 0) {
                return ResponseVo.fail("表中没有数据");
            }
            List<Map<String, Object>> resultList = new ArrayList<>();
            //失败状态之机型+检查项类别编号+检查项编号  重复
            for (int i = 0; i < importExcel.size(); i++) {
                String first = "";
                String modelName = (String) importExcel.get(i).get("机型");
                String checktypeNum = (String) importExcel.get(i).get("检查项类别编号");
                String checkitemNum = (String) importExcel.get(i).get("检查项编号");
                //失败状态之机型+检查项类别编号+检查项编号  重复
                first = modelName+checktypeNum+checkitemNum;
                for (int j = i+1; j < importExcel.size(); j++) {
                    String second = "";
                    String modelName2 = (String) importExcel.get(j).get("机型");
                    String checktypeNum2 = (String) importExcel.get(j).get("检查项类别编号");
                    String checkitemNum2 = (String) importExcel.get(j).get("检查项编号");
                    second = modelName2+checktypeNum2+checkitemNum2;
                    if (first.equalsIgnoreCase(second)){
                        Map map1 = importExcel.get(j);
                        map1.put("results", "失败");
                        break;
                    }
                }
            }
            //导入的转换成map
            for (Map maps : importExcel) {

                Map<String, Object> map = new HashMap<>();
                //导入的
                String jianchaxiangNum = (String) maps.get("检查项编号");
                String jianchaxiangName = (String) maps.get("检查项名称");
                String recodingModel = (String) maps.get("记录方式");
//                Integer time = Integer.parseInt(maps.get("拍照时间间隔(s)") + "");
                String standard = (String) maps.get("判断标准");
                String solvePlan = (String) maps.get("解决方法");
                String method = (String) maps.get("判定方法");
                String resultType = (String) maps.get("结果类型");
                String minResult = (String) maps.get("结果区间(小)");
                String maxResult = (String) maps.get("结果区间(大)");
                String voice = (String) maps.get("检查项语音提示");
                String guidance = (String) maps.get("作业指导");
                String checkTypeNum = (String) maps.get("检查项类别编号");
                String checkTypeName = (String) maps.get("检查项类别名称");
                String typeVoice = (String) maps.get("类别语音提示");
                //判断机型是否存储，不存在返回文本描述  导入失败，导入中存在不匹配的机型
                String jixing = (String) maps.get("机型");
                Object checkOrderItem = (Object) maps.get("显示顺序");
                Object typeOrder = (Object) maps.get("类别显示顺序");



                map.put("modelName", jixing);
                map.put("checktypeNum", checkTypeNum);
                map.put("checktypeName", checkTypeName);
                map.put("checkOrderType", typeOrder);
                map.put("voice", typeVoice);
                map.put("checkitemNum", jianchaxiangNum);
                map.put("checkitemName", jianchaxiangName);
                map.put("recodingModel", recodingModel);
//                map.put("time", time);
                map.put("standard", standard);
                map.put("solveMethod", solvePlan);
                map.put("decisionMethod", method);
                map.put("resultType", resultType);
                map.put("minResult", minResult);
                map.put("maxResult", maxResult);
                map.put("voiceInfo", voice);
                map.put("guidance", guidance);
                map.put("checkOrderItem", checkOrderItem);
                //类别显示顺序  和  显示顺序  有一个不对类型都是失败  //不是int或者double就失败
                Double checkOrderItem1 = 0.0;
                Double typeOrder1 = 0.0;
                try {
                    checkOrderItem1 = Double.parseDouble((String) maps.get("显示顺序"));
                    typeOrder1 = Double.parseDouble((String) maps.get("类别显示顺序"));
                } catch (Exception e) {
                    map.put("results", "失败");
                    map.put("details", "导入失败，导入的显示顺序或者类别显示顺序类型不符合!");
                }

                //记录方式失败
                DSParamBuilder dsParamBuilderRecoding1 = new DSParamBuilder(25);
                dsParamBuilderRecoding1.buildCondition("recodingModel", "equals", recodingModel);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding1 = dataCenterFeignService.retrieve(dsParamBuilderRecoding1.build());
                if (recodingModel==null||recodingModel==""){
                }else {
                    if (responseVoRecoding1.isSuccess() && responseVoRecoding1.getMessage() == null) {
                        //查询到了记录方式   记录方式可以是空
                        List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoRecoding1.getData().get("rtnData");
                    } else {
                        map.put("results", "失败");
                        map.put("details", "您填写的记录方式与基础数据中配置的不相符,请重新填写!");
                    }
                }

                //必填项为空失败
                if (jixing == null || jixing == "" || jianchaxiangNum == null || jianchaxiangNum == "" ||
                        jianchaxiangName == null || jianchaxiangName == ""  ||
                        checkOrderItem  == null || checkTypeNum == "" ||
                        checkTypeNum == null || checkTypeName == null || checkTypeName == "" || typeOrder == null) {
                    map.put("results", "失败");
                    map.put("details", "必填项为空,请更改后重新导入!");
                }
                /*DSParamBuilder dsParamBuilderRecoding1 = new DSParamBuilder(25);
                dsParamBuilderRecoding1.buildCondition("recodingModel", "equals", recodingModel);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding1 = dataCenterFeignService.retrieve(dsParamBuilderRecoding1.build());
                if (responseVoRecoding1.isSuccess() && responseVoRecoding1.getMessage() == null) {
                }else {
                    map.put("results", "失败");
                    map.put("details", "您填写的记录方式与基础数据中配置的不相符,请重新填写!");
                    resultList.add(map);
                    break;
                }*/
                //机型+检查项类别编号+检查项编号重复
                if ("失败".equals(maps.get("results"))) {
                    map.put("results","失败");
                    map.put("details", "您填写的机型+检查项类别编号+检查项编号重复,请重新输入!");
                    resultList.add(map);
                    continue;
                }

                if ("失败".equals(map.get("results"))) {
                    resultList.add(map);
                    continue;
                }
                String modelId = "";
                //查询数据库  机型不存在返回失败
                DSParamBuilder dsParamBuilder = new DSParamBuilder(9);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(dsParamBuilder.build());
                if (responseVo.isSuccess()) {
                    List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
                    if (rtnData.size() != 0 && rtnData != null) {
                        for (int i = 0; i < rtnData.size(); i++) {
                            String modelName = (String) rtnData.get(i).get("modelName");
                            modelId = (String) rtnData.get(i).get("mid");
                            //返回结果不能这样封装想一想
                            if (jixing.equalsIgnoreCase(modelName)) {
                                map.put("results", "");
                                map.put("details", "");
                                break;
                            } else {
                                map.put("results", "失败");
                                map.put("details", "导入失败，导入中存在不匹配的机型");
                            }
                        }
                    }
                }
                if ("失败".equals(map.get("results"))) {
                    resultList.add(map);
                    continue;
                }


                //检查项检查项类别编号查询数据库都没有的  返回新增  11 检查项  12检查项类别
                DSParamBuilder dsParamBuilder1 = new DSParamBuilder(11);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamBuilder1.build());
                if (retrieve.isSuccess() && retrieve.getMessage() != null) {
                    map.put("results", "新增");
                    map.put("details", "");
                    resultList.add(map);
                    continue;
                }
                DSParamBuilder dsParamBuilder2 = new DSParamBuilder(12);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveType = dataCenterFeignService.retrieve(dsParamBuilder2.build());
                if (retrieveType.isSuccess() && retrieveType.getMessage() != null) {
                    map.put("results", "新增");
                    map.put("details", "");
                    resultList.add(map);
                    continue;
                }
                //根据检查项类别编号查询检查项ID  没有就是新增    有加入检查项的比较中
                String checkitemtypeid = null;
                dsParamBuilder2.buildCondition("checktypeNum","equals",checkTypeNum);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveTypeCid = dataCenterFeignService.retrieve(dsParamBuilder2.build());
                if (retrieveTypeCid.isSuccess() && retrieveTypeCid.getMessage() == null) {
                    List<Map<String, Object>> rtnData = (List<Map<String, Object>>) retrieveTypeCid.getData().get("rtnData");
                    if (rtnData.size()>0&&rtnData!=null){
                        checkitemtypeid = (String) rtnData.get(0).get("cid");
                    }
                }else {
                    map.put("results", "新增");
                    map.put("details", "");
                    resultList.add(map);
                    continue;
                }
                //根据检查项类别编号  查询检查项  没有就是新增
                DSParamBuilder dsParamBuildercheckitemBychecktype = new DSParamBuilder(11);
                dsParamBuildercheckitemBychecktype.buildCondition("checkitemNum","equals",jianchaxiangNum);
                dsParamBuildercheckitemBychecktype.buildCondition("checkitemtypeId","equals",checkitemtypeid);
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrievecheckitemBychecktype = dataCenterFeignService.retrieve(dsParamBuildercheckitemBychecktype.build());
                if (retrievecheckitemBychecktype.isSuccess() && retrievecheckitemBychecktype.getMessage() != null) {
                    map.put("results", "新增");
                    map.put("details", "");
                    resultList.add(map);
                    continue;
                }
                //根据检查项编号查询是否新增或者修改
                DSParamBuilder dsParamBuilderItem = new DSParamBuilder(11);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoItem = dataCenterFeignService.retrieve(dsParamBuilderItem.build());
                if (responseVoItem.isSuccess()) {
                    List<Map<String, Object>> rtnDataItem = (List<Map<String, Object>>) responseVoItem.getData().get("rtnData");
                    //条件为true是 有重复或者修改 为false就是新增
                    if (rtnDataItem.size() != 0 && rtnDataItem != null) {
                        //遍历检查项编号 每一个检查项和每一个导入的去对比 设置状态
                        for (int i = 0; i < rtnDataItem.size(); i++) {
                            //查询的数据库中的
                            String checkitemNum = (String) rtnDataItem.get(i).get("checkitemNum");
                            if (jianchaxiangNum.equalsIgnoreCase(checkitemNum)) {
                                //写sql查询  没有匹配 是修改 都匹配是重复
                                //{"condition":{"checkitemNum":"1"},"dataObjectId":52,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":2}}
                                DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(52);

                                dsParamDsBuilder.buildCondition("checkitemNum", jianchaxiangNum);
                                dsParamDsBuilder.buildCondition("checkitemName", jianchaxiangName);
                                dsParamDsBuilder.buildCondition("checkitemtypeId", checkitemtypeid);
                                //查询记录方式
                                //{"condition":[{"key":"recodingModel","condition":"equals","value":"1"}],"dataObjectId":25,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":4}}
                                DSParamBuilder dsParamBuilderRecoding = new DSParamBuilder(25);
                                dsParamBuilderRecoding.buildCondition("recodingModel", "equals", recodingModel);
                                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding = dataCenterFeignService.retrieve(dsParamBuilderRecoding.build());
                                if (recodingModel==null||recodingModel==""){
                                    dsParamDsBuilder.buildCondition("recodingModelId", "");
                                }else {
                                    if (responseVoRecoding.isSuccess() && responseVoRecoding.getMessage() == null) {
                                        //查询到了记录方式   记录方式可以是空
                                        List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoRecoding.getData().get("rtnData");
                                        /*if (!rtnData.get(0).get("time").equals(time)) {
                                            map.put("results", "修改");
                                            map.put("details", "");
                                            resultList.add(map);
                                            break;
                                        }*/
                                        dsParamDsBuilder.buildCondition("recodingModelId", (String) rtnData.get(0).get("rid"));
                                    } else {
                                        map.put("results", "失败");
                                        map.put("details", "您填写的记录方式与基础数据中配置的不相符,请重新填写!");
                                        resultList.add(map);
                                        break;
                                    }
                                }
                                dsParamDsBuilder.buildCondition("standard", standard);
                                dsParamDsBuilder.buildCondition("solveMethod", solvePlan);
                                dsParamDsBuilder.buildCondition("decisionMethod", method);
                                dsParamDsBuilder.buildCondition("resultType", resultType);
                                dsParamDsBuilder.buildCondition("minResult", minResult);
                                dsParamDsBuilder.buildCondition("maxResult", maxResult);
                                dsParamDsBuilder.buildCondition("voiceInfo", voice);
                                dsParamDsBuilder.buildCondition("guidance", guidance);
                                dsParamDsBuilder.buildCondition("checkOrder", checkOrderItem);
                                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoAllItem = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
                                System.out.println(dsParamDsBuilder.build());
                                if (responseVoAllItem.isSuccess() && responseVoAllItem.getMessage() == null) {
                                    List<Map<String, Object>> rtnDataAllItem = (List<Map<String, Object>>) responseVoAllItem.getData().get("rtnData");
                                    if (rtnDataAllItem != null || rtnDataAllItem.size() != 0) {
                                        /*//有重复的
                                        map.put("results", "重复");
                                        map.put("details", "");*/
                                        //判断检查项类别是否重复
                                        DSParamDsBuilder dsParamDsBuilderType = new DSParamDsBuilder(54);
                                        dsParamDsBuilderType.buildCondition("checktypeNum", checkTypeNum);
                                        dsParamDsBuilderType.buildCondition("checktypeName", checkTypeName);
                                        dsParamDsBuilderType.buildCondition("voice", typeVoice);
                                        dsParamDsBuilderType.buildCondition("checkOrder", typeOrder);
                                        dsParamDsBuilderType.buildCondition("modelId", modelId);
                                        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoAllType = dataCenterFeignService.retrieve(dsParamDsBuilderType.build());
                                        if (responseVoAllType.isSuccess() && responseVoAllType.getMessage() == null) {
                                            List<Map<String, Object>> rtnDataAllType = (List<Map<String, Object>>) responseVoAllType.getData().get("rtnData");
                                            if (rtnDataAllType != null || rtnDataAllType.size() != 0) {
                                                map.put("results", "重复");
                                                map.put("details", "");
                                                resultList.add(map);
                                                break;
                                            }
                                        } else {
                                            //遍历检查项类别编号  判断是新增还是修改
                                            boolean flag = false;
                                            DSParamBuilder dsParamBuilderType = new DSParamBuilder(12);
                                            dsParamBuilderType.buildCondition("modelId", "equals", modelId);
                                            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoType = dataCenterFeignService.retrieve(dsParamBuilderType.build());
                                            if (responseVoType.isSuccess() && responseVoType.getMessage() == null) {
                                                List<Map<String, Object>> rtnDataType = (List<Map<String, Object>>) responseVoType.getData().get("rtnData");
                                                for (Map<String, Object> stringObjectMap : rtnDataType) {
                                                    if (((String) stringObjectMap.get("checktypeNum")).equals(checkTypeNum)) {
                                                        map.put("results", "修改");
                                                        map.put("details", "");
                                                        resultList.add(map);
                                                        flag =true;
                                                        break;
                                                    }
                                                }
                                                if (flag){
                                                    break;
                                                }
                                            }
                                            //检查项类别新增
                                            map.put("results", "新增");
                                            map.put("details", "");
                                            resultList.add(map);
                                            break;
                                        }
                                    }
                                } else {
                                    map.put("results", "修改");
                                    map.put("details", "");
                                    resultList.add(map);
                                    break;
                                }
                            }
                        }

                    }
                }
                if (map.get("results") == null || map.get("results") == "") {
                    map.put("results", "新增");
                    map.put("details", "");
                    resultList.add(map);
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
            }catch (Exception e){
                return ResponseVo.success(resultList);
            }
            return ResponseVo.success(resultList);

        } catch (Exception e) {
            return ResponseVo.fail("导入失败");
        }
    }
}



