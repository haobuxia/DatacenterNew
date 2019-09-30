package com.tianyi.datacenter.inspect.controller;

import com.github.liaochong.myexcel.core.DefaultExcelBuilder;
import com.github.liaochong.myexcel.utils.AttachmentExportUtil;
import com.tianyi.datacenter.common.framework.controller.SuperController;
import com.tianyi.datacenter.common.util.ExcelLogs;
import com.tianyi.datacenter.common.util.ExcelUtil;
import com.tianyi.datacenter.common.util.ExcelUtil2;
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

    @ApiOperation("导出检查项")
    @RequestMapping(value = "/exportdata", method = RequestMethod.GET)
    public void exportdata(@PathParam("checktypeNum") String checktypeNum, HttpServletResponse response) {
        List<CheckitemExport> list = new ArrayList<>();

        Map<String, Object> map = new LinkedHashMap<>();
        //根据检查项类别编号查询检查项类别ID
        DSParamBuilder dsParamBuilder = new DSParamBuilder(12);
        dsParamBuilder.buildCondition("checktypeNum", "equals", checktypeNum);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckType = dataCenterFeignService.retrieve
                (dsParamBuilder.build());
        if (responseVoIdcheckType.isSuccess() && responseVoIdcheckType.getMessage() == null) {
            List<Map<String, Object>> rtnDatas = (List<Map<String, Object>>) responseVoIdcheckType.getData().get
                    ("rtnData");
            if (rtnDatas.size() != 0 && rtnDatas != null) {
                String cid = (String) rtnDatas.get(0).get("cid");
                String modelName = "";

                //根据检查项类别id查询检查项 和机型
                DSParamDsBuilder dsParamDsBuilderItem = new DSParamDsBuilder(48);
                dsParamDsBuilderItem.buildCondition("checktypecid", cid);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdItem = dataCenterFeignService.retrieve
                        (dsParamDsBuilderItem.build());
                if (responseVoIdItem.isSuccess() && responseVoIdItem.getMessage() == null) {
                    List<Map<String, Object>> rtnDataItem = (List<Map<String, Object>>) responseVoIdItem.getData()
                            .get("rtnData");
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
                            checkitemExport.setTime((Integer) rtnData.get("time"));

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


    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseVo search(@RequestBody Map<String, Object> param) {


        //{"dataObjectId":21,"condition":{"typeName":"挖掘机","modelName":"PC200-8"},
        // "pageInfo":{"page":0,"pageSize":0,"total":7},"userId":"1","menuId":"1559715653583"}
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(21);
        dsParamDsBuilder.buildCondition("typeName", (String) param.get("typeName"));
        dsParamDsBuilder.buildCondition("modelName", (String) param.get("modelName"));
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckType = dataCenterFeignService.retrieve
                (dsParamDsBuilder.build());
        Map resultMap = new HashMap();
        if (responseVoIdcheckType.isSuccess() && responseVoIdcheckType.getMessage() == null) {
            List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoIdcheckType.getData().get
                    ("rtnData");
            for (Map<String, Object> rtnDatum : rtnData) {
                String cid = (String) rtnDatum.get("cid");//检查项类别ID
                //统计检查项数
                DSParamDsBuilder dsParamDsBuilderAll = new DSParamDsBuilder(57);
                dsParamDsBuilderAll.buildCondition("cid", cid);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdcheckItemAll = dataCenterFeignService
                        .retrieve(dsParamDsBuilderAll.build());
                if (responseVoIdcheckItemAll.isSuccess()) {
                    List<Map<String, Object>> rtnDataAll = (List<Map<String, Object>>) responseVoIdcheckItemAll
                            .getData().get("rtnData");
                    Integer integer = (Integer) rtnDataAll.get(0).get("COUNT(*)");
                    rtnDatum.put("checkitemNum", integer);
                }
            }
            resultMap.put("rtnData", rtnData);
        }
        return ResponseVo.success(resultMap);
    }


    /**
     * 上面的接口暂时没有使用
     *
     * @param
     * @return
     */
    @ApiOperation("巡检项批量处理excel导出模板")
    @RequestMapping(value = "/exportmodel", method = RequestMethod.GET)
    public void exportmodel(HttpServletResponse response) {
        checkItemService.exportmodel(response);
    }

    @ApiOperation("确认提交")
    @RequestMapping(value = "/saveall", method = RequestMethod.POST)
    public ResponseVo saveall(@RequestBody List<Map<String, Object>> param) {
        ResponseVo responseVo = checkItemService.saveAll(param);
        return responseVo;
    }

    @ApiOperation("修改的详情")
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public ResponseVo detail(@RequestBody Map<String, Object> param) {
        ResponseVo detail = checkItemService.detail(param);
        return detail;
    }

    @ApiOperation("实时状态变更")
    @RequestMapping(value = "/reltimestatus", method = RequestMethod.POST)
    public ResponseVo reltimestatus(@RequestBody Map<String, Object> map) {
        ResponseVo responseVo = checkItemService.reltimestatus(map);
        return responseVo;
    }

    @ApiOperation("导入excel")
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ResponseVo importExcel(@RequestParam("file") MultipartFile file) {
        ResponseVo responseVo = checkItemService.importExcel(file);
        return responseVo;
    }
}



