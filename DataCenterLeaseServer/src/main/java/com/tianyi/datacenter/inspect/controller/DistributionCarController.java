package com.tianyi.datacenter.inspect.controller;

import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.inspect.service.DistributionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Api(description = "配车管理", tags = {"Distribution"})
@RestController
@RequestMapping("inspect/distribution")
public class DistributionCarController {


    @Autowired
    private DistributionService distributionService;

    @GetMapping("/exportmodel")
    @ApiOperation("导出excel模板")
    public void exportmodel(HttpServletResponse response) {
        distributionService.exportModel(response);
    }
    @PostMapping("/import")
    @ApiOperation("导入excel")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "excel文档"),
            @ApiImplicitParam(value = "返回示例:{\"success\": true,\n" +
                    " \"message\": \"保存成功/保存失败\",\n" +
                    " \"code\": null,\n" +
                    " \"data\": [{\n" +
                    "\t\"companyName\":\"\",\n" +
                    "\t\"stationName\":\"\",\n" +
                    "\t\"modelName\":\"\",\n" +
                    "\t\"deviceNo\":\"\",\n" +
                    "\t\"helmetNos\":\"\",\n" +
                    "\t\"userNames\":\"\",\n" +
                    "\t\"endTime\":\"\",\n" +
                    "\t\"results\":\"新增/失败\",\n" +
                    "    \"details\":\"\"  （新增是\"\", 失败的是文本描述）\n" +
                    "},\n" +
                    "{…}]\n" +
                    "}")
    })
    @ResponseBody
    public ResponseVo importdemo(@RequestParam("file") MultipartFile file) {
        ResponseVo responseVo = distributionService.importFile(file);
        return responseVo;
    }

    @PostMapping("/reltimestatus")
    @ApiOperation("实时状态变更")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "请求示例:{\n" +
                    "    \"companyName\":\"\",\n" +
                    "\t\"stationName\":\"\",\n" +
                    "\t\"modelName\":\"\",\n" +
                    "\t\"deviceNo\":\"\",\n" +
                    "\t\"helmetNos\":\"\",\n" +
                    "\t\"userNames\":\"\",\n" +
                    "\t\"endTime\":\"\",\n" +
                    "\t\"results\":\"新增/失败\",\n" +
                    "    \"details\":\"\"  （新增是\"\", 失败的是文本描述）\t\n" +
                    "}"),
            @ApiImplicitParam(value = "返回示例:[{\"success\": true,\n" +
                    " \"message\": null,\n" +
                    " \"code\": null,\n" +
                    " \"data\": {\n" +
                    "\t\"companyName\":\"\",\n" +
                    "\t\"stationName\":\"\",\n" +
                    "\t\"modelName\":\"\",\n" +
                    "\t\"deviceNo\":\"\",\n" +
                    "\t\"helmetNos\":\"\",\n" +
                    "\t\"userNames\":\"\",\n" +
                    "\t\"endTime\":\"\",\n" +
                    "\t\"results\":\"新增/失败\",\n" +
                    "    \"details\":\"\"  （新增是\"\", 失败的是文本描述）\t\n" +
                    "}\n" +
                    "}]")
    })
    @ResponseBody
    public ResponseVo realtime(@RequestBody Map<String, Object> map){
        ResponseVo responseVo = distributionService.realtime(map);
        return responseVo;
    }

    @PostMapping("/saveall")
    @ApiOperation("保存提交")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "请求示例:[{\n" +
                    "\t\"companyName\":\"\",\n" +
                    "\t\"stationName\":\"\",\n" +
                    "\t\"modelName\":\"\",\n" +
                    "\t\"deviceNo\":\"\",\n" +
                    "\t\"helmetNos\":\"\",\n" +
                    "\t\"userNames\":\"\",\n" +
                    "\t\"endTime\":\"\",\n" +
                    "\t\"results\":\"新增/失败\",\n" +
                    "    \"details\":\"\"  （新增是\"\", 失败的是文本描述）\n" +
                    "},{...},...]"),
            @ApiImplicitParam(value = "返回示例:{\n" +
                    " \"success\": true,\n" +
                    " \"message\": \"保存成功/保存失败\",\n" +
                    " \"code\": null,\n" +
                    " \"data\": [{\n" +
                    "\t\"companyName\":\"\",\n" +
                    "\t\"stationName\":\"\",\n" +
                    "\t\"modelName\":\"\",\n" +
                    "\t\"deviceNo\":\"\",\n" +
                    "\t\"helmetNos\":\"\",\n" +
                    "\t\"userNames\":\"\",\n" +
                    "\t\"endTime\":\"\",\n" +
                    "\t\"results\":\"新增/失败\",\n" +
                    "    \"details\":\"\"  （新增是\"\", 失败的是文本描述）\n" +
                    "},{...},... ]}")
    })
    public ResponseVo saveall(@RequestBody List<Map<String, Object>> param) {
        ResponseVo responseVo = distributionService.saveAll(param);
        return responseVo;
    }
}
