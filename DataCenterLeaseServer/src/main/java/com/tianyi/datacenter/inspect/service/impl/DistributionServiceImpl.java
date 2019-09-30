package com.tianyi.datacenter.inspect.service.impl;

import com.github.liaochong.myexcel.utils.AttachmentExportUtil;
import com.tianyi.datacenter.common.util.ExcelLogs;
import com.tianyi.datacenter.common.util.ExcelUtil;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import com.tianyi.datacenter.inspect.service.DistributionService;
import com.tianyi.datacenter.inspect.service.HelmetUniversalService;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Service
public class DistributionServiceImpl implements DistributionService {
    @Autowired
    private HelmetUniversalService helmetUniversalService;
    @Autowired
    private DataCenterFeignService dataCenterFeignService;

    @Override
    @Transactional
    public ResponseVo saveAll(List<Map<String, Object>> param) {
        List resultList = new ArrayList();
        for (Map<String, Object> map : param) {
            String results = (String) map.get("results");
            if (results.equalsIgnoreCase("成功")) {
                resultList.add(map);
                return ResponseVo.success(resultList);
            }
            String companyName = (String) map.get("companyName");
            String stationName = (String) map.get("stationName");
            String modelName = (String) map.get("modelName");
            String deviceNo = (String) map.get("deviceNo");
            String helmetNos = (String) map.get("helmetNos");
            String userNames = (String) map.get("userNames");
            String endTime = (String) map.get("endTime");
            int shootlong = 0;
            if (map.get("shootlong") != null && !"".equals(map.get("shootlong").toString())) {
                shootlong = Integer.parseInt(map.get("shootlong").toString());
            }
            ResponseVo workOrder = helmetUniversalService.createWorkOrder("", helmetNos, userNames, companyName,
                    modelName, deviceNo, stationName, endTime, shootlong);
            if (workOrder.isSuccess()) {
                map.put("companyName", companyName);
                map.put("stationName", stationName);
                map.put("modelName", modelName);
                map.put("deviceNo", deviceNo);
                map.put("helmetNos", helmetNos);
                map.put("userNames", userNames);
                map.put("endTime", endTime);
                map.put("results", "成功");
                map.put("details", "");
            } else {
                map.put("companyName", companyName);
                map.put("stationName", stationName);
                map.put("modelName", modelName);
                map.put("deviceNo", deviceNo);
                map.put("helmetNos", helmetNos);
                map.put("userNames", userNames);
                map.put("endTime", endTime);
                map.put("results", "失败");
                map.put("details", workOrder.getMessage());
            }
            resultList.add(map);
        }
        return ResponseVo.success(resultList);
    }

    @Override
    public ResponseVo realtime(Map<String, Object> map) {
        String companyName = (String) map.get("companyName");
        String stationName = (String) map.get("stationName");
        String modelName = (String) map.get("modelName");
        String deviceNo = (String) map.get("deviceNo");
        String helmetNos = (String) map.get("helmetNos");
        String userNames = (String) map.get("userNames");
        String endTime = (String) map.get("endTime");

        map.put("companyName", companyName);
        map.put("stationName", stationName);
        map.put("modelName", modelName);
        map.put("deviceNo", deviceNo);
        map.put("helmetNos", helmetNos);
        map.put("userNames", userNames);
        map.put("endTime", endTime);
        if (companyName == null || companyName == "" ||
                modelName == null || modelName == "" ||
                deviceNo == null || deviceNo == "" ||
                stationName == null || stationName == null ||
                helmetNos == "" || helmetNos == null ||
                userNames == null || userNames == "" ||
                endTime == null) {
            map.put("results", "失败");
            map.put("details", "必填项为空,请更改后重新导入!");
        } else {
            //查询order表  判断是否重复
            //查巡公司 工位  机型
            String modelId = ""; // model
            DSParamBuilder modelParamBuilder = new DSParamBuilder(9); // 查询机型
            modelParamBuilder.buildCondition("modelName", "equals", modelName);
            com.tianyi.datacenter.feign.common.vo.ResponseVo modelVo = dataCenterFeignService.retrieve
                    (modelParamBuilder.build());
            if (modelVo.isSuccess()) {
                List<Map<String, Object>> modellist = (List<Map<String, Object>>) modelVo.getData().get("rtnData");
                if (modellist == null || modellist.size() == 0) {// 没有机型
                    return ResponseVo.fail("输入的数据必须和巡检批量导入的一致!");
                } else {
                    modelId = modellist.get(0).get("mid").toString();
                }
            }
            String companyId = ""; // companyName
            DSParamBuilder companyParamBuilder = new DSParamBuilder(1); // 查询公司
            companyParamBuilder.buildCondition("companyName", "equals", companyName);
            com.tianyi.datacenter.feign.common.vo.ResponseVo companyVo = dataCenterFeignService.retrieve
                    (companyParamBuilder.build());
            if (companyVo.isSuccess()) {
                List<Map<String, Object>> companylist = (List<Map<String, Object>>) companyVo.getData().get("rtnData");
                if (companylist == null || companylist.size() == 0) {// 没有公司
                    return ResponseVo.fail("输入的数据必须和巡检批量导入的一致!");
                } else {
                    companyId = companylist.get(0).get("cid").toString();
                }
            }
            String stationId = ""; // stationId
            DSParamBuilder stationParamBuilder = new DSParamBuilder(14);
            stationParamBuilder.buildCondition("stationName", "equals", stationName);
            com.tianyi.datacenter.feign.common.vo.ResponseVo stationVo = dataCenterFeignService.retrieve
                    (stationParamBuilder.build());
            if (stationVo.isSuccess()) {
                List<Map<String, Object>> stationlist = (List<Map<String, Object>>) stationVo.getData().get("rtnData");
                if (stationlist == null || stationlist.size() == 0) {// 没有公司
                    return ResponseVo.fail("输入的数据必须和巡检批量导入的一致!");
                } else {
                    stationId = stationlist.get(0).get("cid").toString();
                }
            }
            //查询 数据集  78
            if (helmetNos != null && helmetNos.length() > 0) {  //遍历头盔
                helmetNos = helmetNos.replaceAll("，", ",");
                String[] helmetNoArray = helmetNos.split(",");
                for (int i = 0; i < helmetNoArray.length; i++) {
                    DSParamDsBuilder ifareadyExist = new DSParamDsBuilder(78);
                    ifareadyExist.buildCondition("stationId", stationId);
                    ifareadyExist.buildCondition("companyId", companyId);
                    ifareadyExist.buildCondition("deviceNo", deviceNo);
                    ifareadyExist.buildCondition("modelId", modelId);
                    ifareadyExist.buildCondition("clientId", helmetNoArray[i]);
                    ifareadyExist.buildCondition("endTime", endTime);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo ifareadyExistVo = dataCenterFeignService
                            .retrieve(ifareadyExist.build());
                    if (ifareadyExistVo.isSuccess() && ifareadyExistVo.getMessage() == null) {
                        List<Map<String, Object>> ifareadyExistList = (List<Map<String, Object>>) ifareadyExistVo
                                .getData().get("rtnData");
                        String checkstatus = (String) ifareadyExistList.get(0).get("checkstatus");
                        if (checkstatus == "0") {
                            map.put("results", "重复");
                            map.put("details", "");
                        } else {
                            map.put("results", "新增");
                            map.put("details", "");
                        }
                    } else {
                        map.put("results", "新增");
                        map.put("details", "");
                    }
                }
            }
        }
        List<Map<String, Object>> resultList = new ArrayList<>();
        resultList.add(map);
        return ResponseVo.success(resultList);
    }

    @Override
    public ResponseVo importFile(MultipartFile file) {
        List<Map<String, Object>> resultList;
        try {

            InputStream inputStream = null;
            /*inputStream = file.getInputStream();

            List<CarImport> read = DefaultExcelReader.of(CarImport.class).sheet(0).rowFilter(row -> row.getRowNum() >
             0).read(inputStream);
*/
            List<Map> importExcel = new ArrayList<>();
            inputStream = file.getInputStream();
            ExcelLogs logs = new ExcelLogs();
            importExcel = ExcelUtil.importExcel2(Map.class, inputStream, "yyyy-MM-dd HH:mm:ss", logs, 0);

            if (importExcel.size() == 0) {
                return ResponseVo.fail("表中没有数据");
            }
            /*for (int i = 0; i <importExcel.size() ; i++) {
                //删除null
                if (read.get(i).getCompanyName()==null){
                    read.remove(i);
                }
            }*/
            resultList = new ArrayList<>();
            Map<String, String> typeMap = new HashMap<>();
            //导入的转换成map
            for (Map maps : importExcel) {
                Map<String, Object> map = new HashMap<>();
                //导入的
                String companyName = (String) maps.get("公司");
                String stationName = (String) maps.get("工位");
                String modelName = (String) maps.get("型号");
                String deviceNo = (String) maps.get("机号");
                String helmetNos = (String) maps.get("头盔编号");
                String userNames = (String) maps.get("负责人");
                String endTime = (String) maps.get("结束时间");
                String shootlong = (String) maps.get("拍摄时长(s)");

                map.put("companyName", companyName);
                map.put("stationName", stationName);
                map.put("modelName", modelName);
                map.put("deviceNo", deviceNo);
                map.put("helmetNos", helmetNos);
                map.put("userNames", userNames);
                map.put("endTime", endTime);
                map.put("shootlong", shootlong);

                if (companyName == null || companyName == "" ||
                        modelName == null || modelName == "" ||
                        deviceNo == null || deviceNo == "" ||
                        stationName == null || stationName == null ||
                        helmetNos == "" || helmetNos == null ||
                        userNames == null || userNames == "" ||
                        endTime == null) {
                    map.put("results", "失败");
                    map.put("details", "必填项为空,请更改后重新导入!");
                    resultList.add(map);
                    continue;
                }
                //查询order表  判断是否重复
                //查巡公司 工位  机型
                String modelId = ""; // model
                DSParamBuilder modelParamBuilder = new DSParamBuilder(9); // 查询机型
                modelParamBuilder.buildCondition("modelName", "equals", modelName);
                com.tianyi.datacenter.feign.common.vo.ResponseVo modelVo = dataCenterFeignService.retrieve
                        (modelParamBuilder.build());
                if (modelVo.isSuccess()) {
                    List<Map<String, Object>> modellist = (List<Map<String, Object>>) modelVo.getData().get("rtnData");
                    if (modellist == null || modellist.size() == 0) {// 没有机型
                        return ResponseVo.fail("机型不存在");
                    } else {
                        modelId = modellist.get(0).get("mid").toString();
                    }
                }
                String companyId = ""; // companyName
                DSParamBuilder companyParamBuilder = new DSParamBuilder(1); // 查询公司
                companyParamBuilder.buildCondition("companyName", "equals", companyName);
                com.tianyi.datacenter.feign.common.vo.ResponseVo companyVo = dataCenterFeignService.retrieve
                        (companyParamBuilder.build());
                if (companyVo.isSuccess()) {
                    List<Map<String, Object>> companylist = (List<Map<String, Object>>) companyVo.getData().get
                            ("rtnData");
                    if (companylist == null || companylist.size() == 0) {// 没有公司
                        return ResponseVo.fail("公司不存在");
                    } else {
                        companyId = companylist.get(0).get("cid").toString();
                    }
                }
                String stationId = ""; // stationId
                DSParamBuilder stationParamBuilder = new DSParamBuilder(14);
                stationParamBuilder.buildCondition("stationName", "equals", stationName);
                com.tianyi.datacenter.feign.common.vo.ResponseVo stationVo = dataCenterFeignService.retrieve
                        (stationParamBuilder.build());
                if (stationVo.isSuccess()) {
                    List<Map<String, Object>> stationlist = (List<Map<String, Object>>) stationVo.getData().get
                            ("rtnData");
                    if (stationlist == null || stationlist.size() == 0) {// 没有公司
                        return ResponseVo.fail("检查类型不存在");
                    } else {
                        stationId = stationlist.get(0).get("cid").toString();
                    }
                }
                //查询 数据集  78
                if (helmetNos != null && helmetNos.length() > 0) {  //遍历头盔
                    helmetNos = helmetNos.replaceAll("，", ",");
                    String[] helmetNoArray = helmetNos.split(",");
                    for (int i = 0; i < helmetNoArray.length; i++) {
                        DSParamDsBuilder ifareadyExist = new DSParamDsBuilder(78);
                        ifareadyExist.buildCondition("stationId", stationId);
                        ifareadyExist.buildCondition("companyId", companyId);
                        ifareadyExist.buildCondition("deviceNo", deviceNo);
                        ifareadyExist.buildCondition("modelId", modelId);
                        ifareadyExist.buildCondition("clientId", helmetNoArray[i]);
                        ifareadyExist.buildCondition("endTime", endTime);
                        com.tianyi.datacenter.feign.common.vo.ResponseVo ifareadyExistVo = dataCenterFeignService
                                .retrieve(ifareadyExist.build());
                        if (ifareadyExistVo.isSuccess() && ifareadyExistVo.getMessage() == null) {
                            List<Map<String, Object>> ifareadyExistList = (List<Map<String, Object>>) ifareadyExistVo
                                    .getData().get("rtnData");
                            String checkstatus = (String) ifareadyExistList.get(0).get("checkstatus");
                            if (checkstatus == "0") {
                                map.put("results", "重复");
                                map.put("details", "");
                            } else {
                                map.put("results", "新增");
                                map.put("details", "");
                            }
                        } else {
                            map.put("results", "新增");
                            map.put("details", "");
                        }
                    }
                }
                resultList.add(map);
            }
        } catch (Exception e) {
            return ResponseVo.fail("导入失败");
        }
        return ResponseVo.success(resultList);
    }

    @Override
    public void exportModel(HttpServletResponse response) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet workbookSheet = workbook.createSheet("配车管理模板");
        XSSFRow row;
        Map<String, Object[]> empinfo = new HashMap<>();
        empinfo.put("1", new Object[]{"公司", "工位", "型号", "机号", "头盔编号", "负责人", "结束时间", "拍摄时长(s)"});
        int rowId = 0;
        Set<String> strings = empinfo.keySet();
        for (String s : strings) {
            row = workbookSheet.createRow(rowId++);
            Object[] objects = empinfo.get(s);
            int cellId = 0;
            for (Object obj : objects) {
                XSSFCell cell = row.createCell(cellId++);
                cell.setCellValue((String) obj);
            }
        }
        //遍历规定结束日期格式化
        int rowIdFormat = 1;
        for (int i = 1; i < 200; i++) {
            row = workbookSheet.createRow(rowId++);
            //遍历到第7个单元格设置格式

            CellStyle cellStyleHm = workbook.createCellStyle();
            XSSFCreationHelper creationHelperHm = workbook.getCreationHelper();
            XSSFDataFormat dataFormat = creationHelperHm.createDataFormat();
            ((XSSFCellStyle) cellStyleHm).setDataFormat(dataFormat.getFormat("@"));
            for (int j = 0; j < 5; j++) {
                XSSFCell cellHm = row.createCell(j);
                cellHm.setCellStyle(cellStyleHm);
            }

            CellStyle cellStyle = workbook.createCellStyle();
            XSSFCreationHelper creationHelper = workbook.getCreationHelper();
            ((XSSFCellStyle) cellStyle).setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd " +
                    "hh:mm:ss"));

            XSSFCell cell = row.createCell(6);
            cell.setCellStyle(cellStyle);

            CellStyle cellStyleShootlong = workbook.createCellStyle();
            cellStyleShootlong.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
            XSSFCell cellShootlong = row.createCell(7);
            cellShootlong.setCellStyle(cellStyleShootlong);
        }
        AttachmentExportUtil.export(workbook, "配车管理模板", response);
    }
}
