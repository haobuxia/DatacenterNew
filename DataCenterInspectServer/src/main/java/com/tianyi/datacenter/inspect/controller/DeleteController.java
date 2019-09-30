package com.tianyi.datacenter.inspect.controller;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Api("删除操作相关接口")
@RestController
@RequestMapping("inspect/delete")
public class DeleteController {
    @Autowired
    private DataCenterFeignService dataCenterFeignService;

    @RequestMapping("station")
    public ResponseVo station(@RequestBody Map<String,Object> param) {
        Map resultMap = new HashMap();
        JSONObject jsonObjectDeleteStation = new JSONObject();
        jsonObjectDeleteStation.put("dataObjectId", 14);
        jsonObjectDeleteStation.put("dataId", (String)param.get("cid"));
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoDeleteStation = dataCenterFeignService.checkReference(jsonObjectDeleteStation);
        if (responseVoDeleteStation.isSuccess() && responseVoDeleteStation.getMessage() == null) {
            String isRefer = (String) responseVoDeleteStation.getData().get("isRefer");
            if (isRefer.equalsIgnoreCase("0")){
                //删除数据
                //{"dataObjectId":14,"condition":[{"key":"cid","condition":"equals","value":"2019062200007"}]}
                DSParamBuilder dsParamBuilder = new DSParamBuilder(14);
                dsParamBuilder.buildCondition("cid","equals",(String)param.get("cid"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoDelete = dataCenterFeignService.delete(dsParamBuilder.build());
                if (responseVoDelete.isSuccess() && responseVoDelete.getMessage() == null) {
                    return ResponseVo.success(resultMap,"删除成功");
                }
            }else {
                return ResponseVo.fail("该数据被引用,不允许删除");
            }
        }
        return ResponseVo.fail("查询数据失败!");
    }
    @RequestMapping("machinetype")
    public ResponseVo machinetype(@RequestBody Map<String,Object> param) {
        Map resultMap = new HashMap();
        JSONObject jsonObjectDeleteStation = new JSONObject();
        jsonObjectDeleteStation.put("dataObjectId", 8);
        jsonObjectDeleteStation.put("dataId", (String)param.get("mid"));
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoDeleteStation = dataCenterFeignService.checkReference(jsonObjectDeleteStation);
        if (responseVoDeleteStation.isSuccess() && responseVoDeleteStation.getMessage() == null) {
            String isRefer = (String) responseVoDeleteStation.getData().get("isRefer");
            if (isRefer.equalsIgnoreCase("0")){
                //删除数据
                //{"dataObjectId":8,"condition":[{"key":"cid","condition":"equals","value":"2019062200007"}]}
                DSParamBuilder dsParamBuilder = new DSParamBuilder(8);
                dsParamBuilder.buildCondition("mid","equals",(String)param.get("mid"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoDelete = dataCenterFeignService.delete(dsParamBuilder.build());
                if (responseVoDelete.isSuccess() && responseVoDelete.getMessage() == null) {
                    return ResponseVo.success(resultMap,"删除成功");
                }
            }else {
                return ResponseVo.fail("该数据被引用,不允许删除");
            }
        }
        return ResponseVo.fail("查询数据失败!");
    }
    @RequestMapping("model")
    public ResponseVo model(@RequestBody Map<String,Object> param) {
        Map resultMap = new HashMap();
        JSONObject jsonObjectDeleteStation = new JSONObject();
        jsonObjectDeleteStation.put("dataObjectId", 9);
        jsonObjectDeleteStation.put("dataId", (String)param.get("mid"));
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoDeleteStation = dataCenterFeignService.checkReference(jsonObjectDeleteStation);
        if (responseVoDeleteStation.isSuccess() && responseVoDeleteStation.getMessage() == null) {
            String isRefer = (String) responseVoDeleteStation.getData().get("isRefer");
            if (isRefer.equalsIgnoreCase("0")){
                //删除数据
                //{"dataObjectId":8,"condition":[{"key":"cid","condition":"equals","value":"2019062200007"}]}
                DSParamBuilder dsParamBuilder = new DSParamBuilder(9);
                dsParamBuilder.buildCondition("mid","equals",(String)param.get("mid"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoDelete = dataCenterFeignService.delete(dsParamBuilder.build());
                if (responseVoDelete.isSuccess() && responseVoDelete.getMessage() == null) {
                    return ResponseVo.success(resultMap,"删除成功");
                }
            }else {
                return ResponseVo.fail("该数据被引用,不允许删除");
            }
        }
        return ResponseVo.fail("查询数据失败!");
    }
    @RequestMapping("recodingmodel")
    public ResponseVo recodingmodel(@RequestBody Map<String,Object> param) {
        Map resultMap = new HashMap();
        JSONObject jsonObjectDeleteStation = new JSONObject();
        jsonObjectDeleteStation.put("dataObjectId", 25);
        jsonObjectDeleteStation.put("dataId", (String)param.get("rid"));
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoDeleteStation = dataCenterFeignService.checkReference(jsonObjectDeleteStation);
        if (responseVoDeleteStation.isSuccess() && responseVoDeleteStation.getMessage() == null) {
            String isRefer = (String) responseVoDeleteStation.getData().get("isRefer");
            if (isRefer.equalsIgnoreCase("0")){
                //删除数据
                //{"dataObjectId":8,"condition":[{"key":"cid","condition":"equals","value":"2019062200007"}]}
                DSParamBuilder dsParamBuilder = new DSParamBuilder(25);
                dsParamBuilder.buildCondition("rid","equals",(String)param.get("rid"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoDelete = dataCenterFeignService.delete(dsParamBuilder.build());
                if (responseVoDelete.isSuccess() && responseVoDelete.getMessage() == null) {
                    return ResponseVo.success(resultMap,"删除成功");
                }
            }else {
                return ResponseVo.fail("该数据被引用,不允许删除");
            }
        }
        return ResponseVo.fail("查询数据失败!");
    }
    @ApiOperation("检查项类别目录删除")
    @RequestMapping("checktype")
    public ResponseVo checktype(@ApiParam("cid : ") @RequestBody Map<String,Object> param) {
        Map resultMap = new HashMap();
        JSONObject jsonObjectDeleteStation = new JSONObject();
        jsonObjectDeleteStation.put("dataObjectId", 12);
        jsonObjectDeleteStation.put("dataId", (String)param.get("cid"));
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoDeleteStation = dataCenterFeignService.checkReference(jsonObjectDeleteStation);
        if (responseVoDeleteStation.isSuccess() && responseVoDeleteStation.getMessage() == null) {
            String isRefer = (String) responseVoDeleteStation.getData().get("isRefer");
            if (isRefer.equalsIgnoreCase("0")){
                //删除数据
                //{"dataObjectId":8,"condition":[{"key":"cid","condition":"equals","value":"2019062200007"}]}
                DSParamBuilder dsParamBuilder = new DSParamBuilder(12);
                dsParamBuilder.buildCondition("cid","equals",(String)param.get("cid"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoDelete = dataCenterFeignService.delete(dsParamBuilder.build());
                if (responseVoDelete.isSuccess() && responseVoDelete.getMessage() == null) {
                    return ResponseVo.success(resultMap,"删除成功");
                }
            }else {
                return ResponseVo.fail("该数据被引用,不允许删除");
            }
        }
        return ResponseVo.fail("查询数据失败!");
    }
    @ApiOperation("检查项类删除")
    @RequestMapping("checkitem")
    public ResponseVo checkitem(@RequestBody Map<String,Object> param) {
        Map resultMap = new HashMap();
        JSONObject jsonObjectDeleteStation = new JSONObject();
        jsonObjectDeleteStation.put("dataObjectId", 11);
        jsonObjectDeleteStation.put("dataId", (String)param.get("cid"));
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoDeleteStation = dataCenterFeignService.checkReference(jsonObjectDeleteStation);
        if (responseVoDeleteStation.isSuccess() && responseVoDeleteStation.getMessage() == null) {
            String isRefer = (String) responseVoDeleteStation.getData().get("isRefer");
            if (isRefer.equalsIgnoreCase("0")){
                //删除数据
                //{"dataObjectId":8,"condition":[{"key":"cid","condition":"equals","value":"2019062200007"}]}
                DSParamBuilder dsParamBuilder = new DSParamBuilder(11);
                dsParamBuilder.buildCondition("cid","equals",(String)param.get("cid"));
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoDelete = dataCenterFeignService.delete(dsParamBuilder.build());
                if (responseVoDelete.isSuccess() && responseVoDelete.getMessage() == null) {
                    return ResponseVo.success(resultMap,"删除成功");
                }
            }else {
                return ResponseVo.fail("该数据被引用,不允许删除");
            }
        }
        return ResponseVo.fail("查询数据失败!");
    }
}
