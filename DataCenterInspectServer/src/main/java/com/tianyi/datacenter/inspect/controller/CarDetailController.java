package com.tianyi.datacenter.inspect.controller;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Api("车辆台账操作相关接口")
@RestController
@RequestMapping("inspect/carDetail")
public class CarDetailController {

    @Autowired
    private DataCenterFeignService dataCenterFeignService;


    @RequestMapping("add")
    public ResponseVo add(@RequestBody Map<String, String> param) {
        StringBuffer sb = new StringBuffer();
        StringBuffer sbSelect = new StringBuffer();
        //查询机型+机号不能重复
        String modelName = param.get("modelName");//机型名称
        String carId = param.get("cid");//机号
        String modelId = param.get("modelId");//机型ID
        sb.append(modelName);
        sb.append(carId);
        //{"condition":[],"dataObjectId":9,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":10}}
        DSParamBuilder dsParamBuilder = new DSParamBuilder(9);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(dsParamBuilder.build());
        if (responseVo.isSuccess()&&responseVo.getMessage()==null) {

            //根据机型ID查询车辆信息的ID
            //{"condition":[],"dataObjectId":10,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":10}}
            DSParamBuilder dsParamBuilderCar = new DSParamBuilder(10);
            dsParamBuilderCar.buildCondition("modelId", "equals", modelId);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoCar = dataCenterFeignService.retrieve(dsParamBuilderCar.build());
            if (responseVoCar.isSuccess()) {
                List<Map<String, Object>> rtnData = (List<Map<String, Object>>) responseVoCar.getData().get("rtnData");
                if (rtnData.size() != 0 && rtnData != null) {
                    for (Map<String, Object> rtnDatum : rtnData) {
                        sbSelect.append(modelName);
                        sbSelect.append(rtnDatum.get("deviceNo"));
                        if (sbSelect.toString().equals(sb.toString())) {
                            return ResponseVo.fail("机型+机号重复,请重新输入");
                        }
                        sbSelect = new StringBuffer();
                    }
                }

            }
        }
        //保存车辆信息
        //{"dataObjectId":10,"data":{}}
        //雪花算法
        JSONObject jsonObjectSaveItem = new JSONObject();
        jsonObjectSaveItem.put("dataObjectId", 10);
        //检查工单项目ID  雪花ID
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoIdCar = dataCenterFeignService.retrieveId(jsonObjectSaveItem);
        if (responseVoIdCar.isSuccess()) {
            DSParamBuilder dsParamBuilderSave = new DSParamBuilder(10);
            Map<String,String> data = new HashMap<>();

            data.put("cid",(String)responseVoIdCar.getData().get("rtnData"));
            data.put("deviceNo",param.get("cid"));
            data.put("producer",param.get("producer"));
            data.put("modelId",modelId);
            data.put("downTime",param.get("downTime"));
            data.put("deptName",param.get("deptName"));
            data.put("privateOrder",param.get("privateOrder"));
            data.put("status","已下线");
            data.put("checkstatus","台上检查");
            dsParamBuilderSave.buildData(data);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSave = dataCenterFeignService.add(dsParamBuilderSave.build());
            if (responseVoSave.isSuccess()&&responseVoSave.getMessage()==null) {
                return ResponseVo.success("保存成功");
            }else {
                return ResponseVo.fail("保存失败:"+responseVoSave.getMessage());
            }
        }
       return ResponseVo.fail("保存失败:"+responseVoIdCar.getMessage());
    }
}
