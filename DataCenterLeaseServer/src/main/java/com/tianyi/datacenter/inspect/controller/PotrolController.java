package com.tianyi.datacenter.inspect.controller;

import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Api(description = "巡检表查询", tags = {"Potrol"})
@RestController
@RequestMapping("inspect/potrol")
public class PotrolController {
    @Autowired
    private DataCenterFeignService dataCenterFeignService;

    //    @RequestMapping(value = "search", method = RequestMethod.POST)
    @PostMapping("/search")
    @ApiOperation("巡检表查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "请求示例:{\n" +
                    "  \"condition\": {\n" +
                    "    \"machinetypeId\": \"111\",\n" +
                    "    \"companyId\": \"111\",\n" +
                    "    \"modelId\": \"111\",\n" +
                    "    \"deviceNo\": \"111\"\n" +
                    "    \"stationName\": \"111\"\n" +
                    "  },\n" +
                    "  \"dataObjectId\": 75,\n" +
                    "  \"pageInfo\": {\n" +
                    "    \"page\": 1,\n" +
                    "    \"count\": 0,\n" +
                    "    \"pageSize\": 100,\n" +
                    "    \"total\": 42\n" +
                    "  }\n" +
                    "}"),
            @ApiImplicitParam(value = "返回示例:{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"没有查询到数据\",\n" +
                    "  \"data\": {\n" +
                    "    \"rtnData\": [\n" +
                    "\t{\n" +
                    "\tcompanyName:\"\",\n" +
                    "\ttypeName:\"\",\n" +
                    "\tmodelName:\"\",\n" +
                    "\tdeviceNo:\"\",\n" +
                    "\tstationName:\"\",\n" +
                    "\tchecktypeTotal:\"\",\n" +
                    "\tcheckitemTotal:\"\"\n" +
                    "}\n" +
                    "\t]\n" +
                    "  }\n" +
                    "}")
    })
    public ResponseVo search(@RequestBody Map<String, Object> params) {
        //获取公司ID  已有  companyName  获取 cid
        Map<String, String> param = (Map<String, String>) params.get("condition");
        String companyName = param.get("companyName");
        String deviceNo = param.get("deviceNo");
        String modelId = param.get("modelId");
        String machinetypeId = param.get("machinetypeId");//机种

        String companyId = null;
        DSParamBuilder dsParamBuilderCompany1 = new DSParamBuilder(1);
        dsParamBuilderCompany1.buildCondition("companyName", "equals", companyName);
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCompany = dataCenterFeignService.retrieve(dsParamBuilderCompany1.build());
        if (retrieveCompany.isSuccess() && retrieveCompany.getMessage() == null) {
            List<Map<String, String>> rtnData = (List<Map<String, String>>) retrieveCompany.getData().get("rtnData");
            if (rtnData.size() > 0 && rtnData != null) {
                companyId = rtnData.get(0).get("cid");
            }
        } else {
            companyId = "bucunzai";
        }
        //查询工位配置类别
        List<String> modelIdList = new ArrayList();
        //结果数组
        List<Map<String, String>> resultList = new ArrayList();

        List<Map<String, Object>> finalResult = new ArrayList();
        Map<String, Integer> pageInfo1 = new HashMap<>();

        //分页查询
        Map<String, Integer> pageInfo = (Map<String, Integer>) params.get("pageInfo");
        DSParamDsBuilder dsParamBuilderStation = new DSParamDsBuilder(75);
        if (param.get("stationName")!=null&&param.get("stationName")!=""){
            String stationName = param.get("stationName");
            dsParamBuilderStation.buildCondition("stationName", stationName);
        }
        dsParamBuilderStation.buildCondition("deviceNo", deviceNo);
        dsParamBuilderStation.buildCondition("modelId", modelId);
        dsParamBuilderStation.buildCondition("companyId", companyId);
        dsParamBuilderStation.buildCondition("machinetypeId", machinetypeId);
        dsParamBuilderStation.buildCondition("machinetypeId", machinetypeId);
        dsParamBuilderStation.buildPageInfo(pageInfo.get("page"), pageInfo.get("pageSize"));
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoStation = dataCenterFeignService.retrieve(dsParamBuilderStation.build());
        if (responseVoStation.isSuccess() && responseVoStation.getMessage() == null) {
            List<Map<String, String>> rtnDataStation = (List<Map<String, String>>) responseVoStation.getData().get("rtnData");
            for (Map<String, String> stringStringMap : rtnDataStation) {
                resultList.add(stringStringMap);
            }
        }


        //查询公司 工位 机型名称  根据机型ID查询机种名称
        for (Map<String, String> stationMap : resultList) {
            Map<String, Object> resultMap = new HashMap<>();
            String companyIdFromDataBase = stationMap.get("companyId");
            String stationIdFromDataBase = stationMap.get("stationId");
            String modelIdFromDataBase = stationMap.get("modelId");
            String deviceNoFromDataBase = stationMap.get("deviceNo");
            resultMap.put("deviceNo", deviceNoFromDataBase);

            //查询机型名称
            String modelName = null;
            String typeName = null;
            DSParamBuilder dsParamBuilderModelName = new DSParamBuilder(9);
            dsParamBuilderModelName.buildCondition("mid", "equals", modelIdFromDataBase);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoModelName = dataCenterFeignService.retrieve(dsParamBuilderModelName.build());
            if (responseVoModelName.isSuccess() && responseVoModelName.getMessage() == null) {
                List<Map<String, String>> rtnDataModelName = (List<Map<String, String>>) responseVoModelName.getData().get("rtnData");
                modelName = rtnDataModelName.get(0).get("modelName");
                machinetypeId = rtnDataModelName.get(0).get("machinetypeId");

                //查询机种名称
                DSParamBuilder dsParamBuildertypeName = new DSParamBuilder(8);
                dsParamBuildertypeName.buildCondition("mid", "equals", machinetypeId);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVotypeName = dataCenterFeignService.retrieve(dsParamBuildertypeName.build());
                if (responseVotypeName.isSuccess() && responseVotypeName.getMessage() == null) {
                    List<Map<String, String>> rtnDataTypeName = (List<Map<String, String>>) responseVotypeName.getData().get("rtnData");
                    typeName = rtnDataTypeName.get(0).get("typeName");
                }
            }
            resultMap.put("typeName", typeName);
            resultMap.put("modelName", modelName);

            //查询公司名称
            String companyName2 = null;
            DSParamBuilder dsParamBuildercompanyName = new DSParamBuilder(1);
            dsParamBuildercompanyName.buildCondition("cid", "equals", companyIdFromDataBase);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoCompanyName = dataCenterFeignService.retrieve(dsParamBuildercompanyName.build());
            if (responseVoCompanyName.isSuccess() && responseVoCompanyName.getMessage() == null) {
                List<Map<String, String>> rtnDataCompanyName = (List<Map<String, String>>) responseVoCompanyName.getData().get("rtnData");
                companyName2 = rtnDataCompanyName.get(0).get("companyName");
            }
            resultMap.put("companyName", companyName2);

            //查询工位名称
            String stationName = null;
            DSParamBuilder dsParamBuilderstationName = new DSParamBuilder(14);
            dsParamBuilderstationName.buildCondition("cid", "equals", stationIdFromDataBase);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVostationName = dataCenterFeignService.retrieve(dsParamBuilderstationName.build());
            if (responseVostationName.isSuccess() && responseVostationName.getMessage() == null) {
                List<Map<String, String>> rtnDatastationName = (List<Map<String, String>>) responseVostationName.getData().get("rtnData");
                stationName = rtnDatastationName.get(0).get("stationName");
            }
            resultMap.put("stationName", stationName);

            //查询巡检项类别数   巡检项数
            //{"condition":{"companyId":"1","modelId":"1","deviceNo":"1"},"dataObjectId":74,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":10}}
            Integer checkitemTotal = 0;
            Integer checktypeTotal = 0;
            DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(74);
            dsParamDsBuilder.buildCondition("companyId", companyIdFromDataBase);
            dsParamDsBuilder.buildCondition("modelId", modelIdFromDataBase);
            dsParamDsBuilder.buildCondition("deviceNo", deviceNoFromDataBase);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVocheckitemTotal = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
            if (responseVocheckitemTotal.isSuccess() && responseVocheckitemTotal.getMessage() == null) {
                List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVocheckitemTotal.getData().get("rtnData");
                //获取检查项数量，检查项类别数量
                checkitemTotal = (Integer) rtnData.get(0).get("checkitemTotal");
                checktypeTotal = (Integer) rtnData.get(0).get("checktypeTotal");
            }
            resultMap.put("checkitemTotal", checkitemTotal);
            resultMap.put("checktypeTotal", checktypeTotal);
            finalResult.add(resultMap);
        }

        ResponseVo responseVo = new ResponseVo();
        Map result = new HashMap();
        Map<String, Integer> pageInfoFromWeb = (Map<String, Integer>) params.get("pageInfo");
        if (resultList.size() == 0) {
            responseVo.setMessage("查询数据为空");
            pageInfo1.put("page", pageInfoFromWeb.get("page"));
            pageInfo1.put("pageSize", pageInfoFromWeb.get("pageSize"));
            pageInfo1.put("total", pageInfoFromWeb.get("total"));
            result.put("pageInfo", pageInfo1);
            List data = new ArrayList();
            result.put("rtnData", data);
            responseVo.setData(result);
        } else {
            result.put("rtnData", finalResult);
            result.put("pageInfo", responseVoStation.getData().get("pageInfo"));
            responseVo.setData(result);
        }
        return responseVo;
    }

    //    @RequestMapping(value = "check", method = RequestMethod.POST)
    @PostMapping("/check")
    @ApiOperation("巡检表检查项详情")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "请求示例:{\n" +
                    "  \"condition\": {\n" +
                    "    \"modelName\": \"132\",\n" +
                    "    \"checktypeTotal\": 1,\n" +
                    "    \"checkitemTotal\": 1,\n" +
                    "    \"companyName\": \"田一科技\",\n" +
                    "    \"typeName\": \"挖掘机\",\n" +
                    "    \"stationName\": \"台上检查\",\n" +
                    "    \"deviceNo\": \"789\"\n" +
                    "  }\n" +
                    "}"),
            @ApiImplicitParam(value = "返回示例:{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"没有查询到数据\",\n" +
                    "  \"data\": {\n" +
                    "    \"rtnData\": [\n" +
                    "      {\n" +
                    "        \"checktypeNum\": \"\",\n" +
                    "        \"checktypeName\": \"\",\n" +
                    "        \"voice\": \"\",\n" +
                    "        \"checkOrderType\": \"\",\n" +
                    "        \"checkitemNum\": \"\",\n" +
                    "        \"checkitemName\": \"\",\n" +
                    "        \"recodingModel\": \"\",\n" +
                    "        \"standard\": \"\",\n" +
                    "        \"voiceInfo\": \"\",\n" +
                    "        \"guidance\": \"\",\n" +
                    "        \"checkOrderItem\": \"\"\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}")
    })
    public ResponseVo checkitem(@RequestBody Map<String, Object> params) {
        //查询检查项类别和检查项
        Map<String, String> param = (Map<String, String>) params.get("condition");
        String companyName = param.get("companyName");
        String deviceNo = param.get("deviceNo");
        String modelName = param.get("modelName");
        String stationName = param.get("stationName");

        //查询机型id
        String modelId = null;
        DSParamBuilder dsParamBuilderModelName = new DSParamBuilder(9);
        dsParamBuilderModelName.buildCondition("modelName", "equals", modelName);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoModelName = dataCenterFeignService.retrieve(dsParamBuilderModelName.build());
        if (responseVoModelName.isSuccess() && responseVoModelName.getMessage() == null) {
            List<Map<String, String>> rtnDataModelName = (List<Map<String, String>>) responseVoModelName.getData().get("rtnData");
            modelId = rtnDataModelName.get(0).get("mid");
        }

        //查询公司id
        String companyId = null;
        DSParamBuilder dsParamBuildercompanyName = new DSParamBuilder(1);
        dsParamBuildercompanyName.buildCondition("companyName", "equals", companyName);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoCompanyName = dataCenterFeignService.retrieve(dsParamBuildercompanyName.build());
        if (responseVoCompanyName.isSuccess() && responseVoCompanyName.getMessage() == null) {
            List<Map<String, String>> rtnDataCompanyName = (List<Map<String, String>>) responseVoCompanyName.getData().get("rtnData");
            companyId = rtnDataCompanyName.get(0).get("cid");
        }

        //查询工位id
        String stationId = null;
        DSParamBuilder dsParamBuilderstationName = new DSParamBuilder(14);
        dsParamBuilderstationName.buildCondition("stationName", "equals", stationName);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVostationName = dataCenterFeignService.retrieve(dsParamBuilderstationName.build());
        if (responseVostationName.isSuccess() && responseVostationName.getMessage() == null) {
            List<Map<String, String>> rtnDatastationName = (List<Map<String, String>>) responseVostationName.getData().get("rtnData");
            stationId = rtnDatastationName.get(0).get("cid");
        }

        //根据公司id  机型id  机号id  工位id查询所有  检查项id
        Set<String> itemIdList = new HashSet<>();
        DSParamBuilder dsParamBuilderitemList = new DSParamBuilder(15);
        if (stationId!=null&&stationId!=""){
            dsParamBuilderitemList.buildCondition("stationId", "equals", stationId);
        }
        if (companyId!=null&&companyId!=""){
            dsParamBuilderitemList.buildCondition("companyId", "equals", companyId);
        }
        if (modelId!=null&&modelId!=""){
            dsParamBuilderitemList.buildCondition("modelId", "equals", modelId);
        }
        if (deviceNo!=null&&deviceNo!=""){
            dsParamBuilderitemList.buildCondition("deviceNo", "equals", deviceNo);
        }
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoitemList = dataCenterFeignService.retrieve(dsParamBuilderitemList.build());
        if (responseVoitemList.isSuccess() && responseVoitemList.getMessage() == null) {
            List<Map<String, String>> rtnDataitemList = (List<Map<String, String>>) responseVoitemList.getData().get("rtnData");
            for (Map<String, String> stringStringMap : rtnDataitemList) {
                itemIdList.add(stringStringMap.get("itemId"));
            }
        }

        //查询所有检查项
        List<Map<String, Object>> finalResult = new ArrayList();
        if (itemIdList.size() > 0 && itemIdList != null) {
            for (String itemId : itemIdList) {
                Map<String, Object> resultMap = new HashMap<>();
                DSParamBuilder dsParamBuilderitem = new DSParamBuilder(11);
                dsParamBuilderitem.buildCondition("cid", "equals", itemId);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoitem = dataCenterFeignService.retrieve(dsParamBuilderitem.build());
                if (responseVoitem.isSuccess() && responseVoitem.getMessage() == null) {
                    List<Map<String, Object>> rtnDataitemList = (List<Map<String, Object>>) responseVoitem.getData().get("rtnData");
                    for (Map<String, Object> stringStringMap : rtnDataitemList) {

                        String checkitemNum = (String) stringStringMap.get("checkitemNum");
                        String checkitemName = (String) stringStringMap.get("checkitemName");
                        String recodingModelId = (String) stringStringMap.get("recodingModelId");
                        String recodingModel = null;
                        DSParamBuilder dsParamBuilderRecoding = new DSParamBuilder(25);
                        dsParamBuilderRecoding.buildCondition("rid", "equals", recodingModelId);
                        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoRecoding = dataCenterFeignService.retrieve(dsParamBuilderRecoding.build());
                        if (responseVoRecoding.isSuccess() && responseVoRecoding.getMessage() == null) {
                            List<Map<String, String>> rtnDataitemRecoding = (List<Map<String, String>>) responseVoRecoding.getData().get("rtnData");
                            recodingModel = rtnDataitemRecoding.get(0).get("recodingModel");
                        }
                        String standard = (String) stringStringMap.get("standard");
                        String voiceInfo = (String) stringStringMap.get("voiceInfo");
                        String guidance = (String) stringStringMap.get("guidance");
                        Double checkOrderItem = (Double) stringStringMap.get("checkOrder");

                        resultMap.put("checkitemNum", checkitemNum);
                        resultMap.put("checkitemName", checkitemName);
                        resultMap.put("recodingModel", recodingModel);
                        resultMap.put("standard", standard);
                        resultMap.put("voiceInfo", voiceInfo);
                        resultMap.put("guidance", guidance);
                        resultMap.put("checkOrderItem", checkOrderItem);

                        //查询所有检查项类别
                        DSParamBuilder dsParamBuildertype = new DSParamBuilder(12);
                        dsParamBuildertype.buildCondition("cid", "equals", stringStringMap.get("checkitemtypeId"));
                        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVotype = dataCenterFeignService.retrieve(dsParamBuildertype.build());
                        if (responseVotype.isSuccess() && responseVotype.getMessage() == null) {
                            List<Map<String, Object>> rtnDatatype = (List<Map<String, Object>>) responseVotype.getData().get("rtnData");
                            for (Map<String, Object> map : rtnDatatype) {

                                String checktypeNum = (String) map.get("checktypeNum");
                                String checktypeName = (String) map.get("checktypeName");
                                String voice = (String) map.get("voice");
                                Double checkOrderType = (Double) map.get("checkOrder");

                                resultMap.put("checktypeNum", checktypeNum);
                                resultMap.put("checktypeName", checktypeName);
                                resultMap.put("voice", voice);
                                resultMap.put("checkOrderType", checkOrderType);
                            }
                        }
                        finalResult.add(resultMap);
                    }
                }
            }
            Collections.sort(finalResult, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Double name1 = (Double) o1.get("checkOrderItem");
                    Double name2 = (Double) o2.get("checkOrderItem");
                    return name1.compareTo(name2);
                }
            });
            Collections.sort(finalResult, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Double name1 = (Double) o1.get("checkOrderType");
                    Double name2 = (Double) o2.get("checkOrderType");
                    return name1.compareTo(name2);
                }
            });
            Map finalMap = new HashMap();
            finalMap.put("rtnData", finalResult);
            return ResponseVo.success(finalMap);
        } else {
            ResponseVo responseVo = new ResponseVo();
            responseVo.setMessage("查询数据为空!");
            return responseVo;
        }
    }

    //    @RequestMapping(value = "checktype", method = RequestMethod.POST)
    @PostMapping("/checktype")
    @ApiOperation("巡检表检查项详情")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "请求示例:{\n" +
                    "  \"condition\": {\n" +
                    "    \"modelName\": \"132\",\n" +
                    "    \"checktypeTotal\": 1,\n" +
                    "    \"checkitemTotal\": 1,\n" +
                    "    \"companyName\": \"田一科技\",\n" +
                    "    \"typeName\": \"挖掘机\",\n" +
                    "    \"stationName\": \"台上检查\",\n" +
                    "    \"deviceNo\": \"789\"\n" +
                    "  }\n" +
                    "}"),
            @ApiImplicitParam(value = "返回示例:{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"没有查询到数据\",\n" +
                    "  \"data\": {\n" +
                    "    \"rtnData\": [\n" +
                    "      {\n" +
                    "        \"checktypeNum\": \"\",\n" +
                    "        \"checktypeName\": \"\",\n" +
                    "        \"voice\": \"\",\n" +
                    "        \"checkOrderType\": \"\",\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }\n" +
                    "}")
    })
    public ResponseVo checktype(@RequestBody Map<String, Object> params) {
        //查询检查项类别和检查项
        Map<String, String> param = (Map<String, String>) params.get("condition");
        String companyName = param.get("companyName");
        String deviceNo = param.get("deviceNo");
        String modelName = param.get("modelName");
        String stationName = param.get("stationName");

        //查询机型id
        String modelId = null;
        DSParamBuilder dsParamBuilderModelName = new DSParamBuilder(9);
        dsParamBuilderModelName.buildCondition("modelName", "equals", modelName);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoModelName = dataCenterFeignService.retrieve(dsParamBuilderModelName.build());
        if (responseVoModelName.isSuccess() && responseVoModelName.getMessage() == null) {
            List<Map<String, String>> rtnDataModelName = (List<Map<String, String>>) responseVoModelName.getData().get("rtnData");
            modelId = rtnDataModelName.get(0).get("mid");
        }

        //查询公司id
        String companyId = null;
        DSParamBuilder dsParamBuildercompanyName = new DSParamBuilder(1);
        dsParamBuildercompanyName.buildCondition("companyName", "equals", companyName);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoCompanyName = dataCenterFeignService.retrieve(dsParamBuildercompanyName.build());
        if (responseVoCompanyName.isSuccess() && responseVoCompanyName.getMessage() == null) {
            List<Map<String, String>> rtnDataCompanyName = (List<Map<String, String>>) responseVoCompanyName.getData().get("rtnData");
            companyId = rtnDataCompanyName.get(0).get("cid");
        }

        //查询工位id
        String stationId = null;
        DSParamBuilder dsParamBuilderstationName = new DSParamBuilder(14);
        dsParamBuilderstationName.buildCondition("stationName", "equals", stationName);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVostationName = dataCenterFeignService.retrieve(dsParamBuilderstationName.build());
        if (responseVostationName.isSuccess() && responseVostationName.getMessage() == null) {
            List<Map<String, String>> rtnDatastationName = (List<Map<String, String>>) responseVostationName.getData().get("rtnData");
            stationId = rtnDatastationName.get(0).get("cid");
        }

        //根据公司id  机型id  机号id  工位id查询所有  检查项id
        List<String> itemIdList = new ArrayList<>();
        DSParamBuilder dsParamBuilderitemList = new DSParamBuilder(15);
        if (stationId!=null&&stationId!=""){
            dsParamBuilderitemList.buildCondition("stationId", "equals", stationId);
        }
        if (companyId!=null&&companyId!=""){
            dsParamBuilderitemList.buildCondition("companyId", "equals", companyId);
        }
        if (modelId!=null&&modelId!=""){
            dsParamBuilderitemList.buildCondition("modelId", "equals", modelId);
        }
        if (deviceNo!=null&&deviceNo!=""){
            dsParamBuilderitemList.buildCondition("deviceNo", "equals", deviceNo);
        }
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoitemList = dataCenterFeignService.retrieve(dsParamBuilderitemList.build());
        if (responseVoitemList.isSuccess() && responseVoitemList.getMessage() == null) {
            List<Map<String, String>> rtnDataitemList = (List<Map<String, String>>) responseVoitemList.getData().get("rtnData");
            for (Map<String, String> stringStringMap : rtnDataitemList) {
                itemIdList.add(stringStringMap.get("itemId"));
            }
        }

        //查询所有检查项
        Set<Map<String, Object>> resultSet = new HashSet<>();

        List<Map<String, Object>> finalResult = new ArrayList();
        if (itemIdList.size() > 0 && itemIdList != null) {
            for (String itemId : itemIdList) {
                Map<String, Object> resultMap = new HashMap<>();
                DSParamBuilder dsParamBuilderitem = new DSParamBuilder(11);
                dsParamBuilderitem.buildCondition("cid", "equals", itemId);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoitem = dataCenterFeignService.retrieve(dsParamBuilderitem.build());
                if (responseVoitem.isSuccess() && responseVoitem.getMessage() == null) {
                    List<Map<String, Object>> rtnDataitemList = (List<Map<String, Object>>) responseVoitem.getData().get("rtnData");
                    for (Map<String, Object> stringStringMap : rtnDataitemList) {

                        //查询所有检查项类别
                        DSParamBuilder dsParamBuildertype = new DSParamBuilder(12);
                        dsParamBuildertype.buildCondition("cid", "equals", stringStringMap.get("checkitemtypeId"));
                        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVotype = dataCenterFeignService.retrieve(dsParamBuildertype.build());
                        if (responseVotype.isSuccess() && responseVotype.getMessage() == null) {
                            List<Map<String, Object>> rtnDatatype = (List<Map<String, Object>>) responseVotype.getData().get("rtnData");
                            for (Map<String, Object> map : rtnDatatype) {

                                String checktypeNum = (String) map.get("checktypeNum");
                                String checktypeName = (String) map.get("checktypeName");
                                String voice = (String) map.get("voice");
                                Double checkOrderType = (Double) map.get("checkOrder");

                                resultMap.put("checktypeNum", checktypeNum);
                                resultMap.put("checktypeName", checktypeName);
                                resultMap.put("voice", voice);
                                resultMap.put("checkOrderType", checkOrderType);
                                resultSet.add(resultMap);
                            }
                        }

                    }
                }
            }
            for (Map<String, Object> map : resultSet) {
                finalResult.add(map);
            }
            Collections.sort(finalResult, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Double name1 = (Double) o1.get("checkOrderType");
                    Double name2 = (Double) o2.get("checkOrderType");
                    return name1.compareTo(name2);
                }
            });
            Map finalMap = new HashMap();
            finalMap.put("rtnData", finalResult);
            return ResponseVo.success(finalMap);
        } else {
            ResponseVo responseVo = new ResponseVo();
            responseVo.setMessage("查询数据为空!");
            return responseVo;
        }
    }

}


