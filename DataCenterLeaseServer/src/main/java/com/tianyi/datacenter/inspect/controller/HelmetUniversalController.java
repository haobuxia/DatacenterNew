package com.tianyi.datacenter.inspect.controller;

import com.tianyi.datacenter.common.framework.controller.SuperController;
import com.tianyi.datacenter.common.vo.RabbitMqVo;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.inspect.service.HelmetUniversalService;
import com.tianyi.datacenter.rabbitmq.MQProducer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tianxujin on 2019/5/13 10:56
 */
@Api("头盔对接相关接口")
@RestController
@RequestMapping("helmet")
public class HelmetUniversalController extends SuperController {

    @Autowired
    HelmetUniversalService helmetUniversalService;
    @Autowired
    private MQProducer mqProducer;
    @ApiOperation(value = "创建工单")
    @RequestMapping(value = "/workorder/create", method = RequestMethod.POST)
    public ResponseVo createWorkOrder(
            @ApiParam(name = "orderId", value = "工单id", required = false) @RequestParam(required=false) String orderId,
            @ApiParam(name = "helmetNos", value = "头盔编号", required = true) @RequestParam String helmetNos,
            @ApiParam(name = "userNames", value = "用户名", required = true) @RequestParam String userNames,
            @ApiParam(name = "companyName", value = "公司名称", required = true) @RequestParam String companyName,
            @ApiParam(name = "model", value = "机型编号", required = true) @RequestParam String model,
            @ApiParam(name = "deviceNo", value = "机号", required = true) @RequestParam String deviceNo,
            @ApiParam(name = "checkType", value = "检查类型", required = true) @RequestParam String checkType,
            @ApiParam(name = "expireDate", value = "过期时间", required = true) @RequestParam String expireDate,
            @ApiParam(name = "shootlong", value = "拍摄时长", required = false) @RequestParam(required=false) Integer shootlong) {
        ResponseVo responseVo = helmetUniversalService.createWorkOrder(orderId, helmetNos, userNames, companyName, model, deviceNo, checkType, expireDate, shootlong);
        return responseVo;
    }
    @ApiOperation(value = "根据机型机号查询最近的一条检查单数据")
    @RequestMapping(value = "/deviceorder", method = RequestMethod.POST)
    public ResponseVo searchOneOrderByDeviceNo(
            @ApiParam(name = "companyName", value = "公司名称", required = true) @RequestParam String companyName,
            @ApiParam(name = "model", value = "机型编号", required = true) @RequestParam String model,
            @ApiParam(name = "deviceNo", value = "机号", required = true) @RequestParam String deviceNo,
            @ApiParam(name = "checkType", value = "检查类型", required = true) @RequestParam String checkType){
        ResponseVo responseVo = helmetUniversalService.searchOneOrderByDeviceNo( companyName, model, deviceNo, checkType);
        return responseVo;
    }

    @ApiOperation(value = "查询工单状态")
    @RequestMapping(value = "/workorder/status/query", method = RequestMethod.POST)
    public ResponseVo workStatus(
            @ApiParam(name = "orderId", value = "工单id", required = true) @RequestParam String orderId) {
        ResponseVo responseVo = helmetUniversalService.workStatus(orderId);
        return responseVo;
    }

    @ApiOperation(value = "设置工单过期")
    @RequestMapping(value = "/workorder/status/expiry", method = RequestMethod.POST)
    public ResponseVo workStatusExpiry(
            @ApiParam(name = "orderId", value = "工单id", required = true) @RequestParam String orderId) {
        ResponseVo responseVo = helmetUniversalService.workStatusExpiry(orderId);
        return responseVo;
    }

    @ApiOperation(value = "查询待检车辆列表")
    @RequestMapping(value = "/cars", method = RequestMethod.POST)
    public ResponseVo cars(
            @ApiParam(name = "userId", value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "deviceNumber", value = "头盔编号", required = true) @RequestParam String deviceNumber) {
        ResponseVo responseVo = helmetUniversalService.getPendingCars(userId, deviceNumber);
        return responseVo;
    }

    @ApiOperation(value = "查询工单列表")
    @RequestMapping(value = "/works", method = RequestMethod.POST)
    public ResponseVo works(
            @ApiParam(name = "userId", value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "cid", value = "车辆id", required = true) @RequestParam String cid) {
        ResponseVo responseVo = helmetUniversalService.getPendingWorks(userId, cid);
        return responseVo;
    }
    @ApiOperation(value = "开始车辆检查")
    @RequestMapping(value = "/startcar", method = RequestMethod.POST)
    public ResponseVo startCar(
            @ApiParam(name = "userId", value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "wid", value = "工单id", required = true) @RequestParam String wid,
            @ApiParam(name = "isQuick", value = "是否快速检查", required = true) @RequestParam String isQuick,
            @ApiParam(name = "deviceNumber", value = "头盔编号", required = true) @RequestParam String deviceNumber) {
        ResponseVo responseVo = helmetUniversalService.startCar(userId, wid, isQuick, deviceNumber);
        return responseVo;
    }

    @ApiOperation(value = "结束车辆检查")
    @RequestMapping(value = "/endcar", method = RequestMethod.POST)
    public ResponseVo endCar(
            @ApiParam(name = "userId", value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "wid", value = "工单id", required = true) @RequestParam String wid,
            @ApiParam(name = "isQuick", value = "是否快速检查", required = true) @RequestParam String isQuick) {
        ResponseVo responseVo = helmetUniversalService.endCar(userId, wid, isQuick);
        return responseVo;
    }
    @ApiOperation(value = "开始工单")
    @RequestMapping(value = "/startwork", method = RequestMethod.POST)
    public ResponseVo startWork(
            @ApiParam(name = "userId", value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "wid", value = "工单id", required = true) @RequestParam String wid,
            @ApiParam(name = "isQuick", value = "是否快速检查", required = true) @RequestParam String isQuick) {
        ResponseVo responseVo = helmetUniversalService.startWork(userId, wid, isQuick);
        return responseVo;
    }

    @ApiOperation(value = "结束工单")
    @RequestMapping(value = "/endwork", method = RequestMethod.POST)
    public ResponseVo endWork(
            @ApiParam(name = "userId", value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "wid", value = "工单id", required = true) @RequestParam String wid,
            @ApiParam(name = "isQuick", value = "是否快速检查", required = true) @RequestParam String isQuick) {
        ResponseVo responseVo = helmetUniversalService.endWork(userId, wid, isQuick);
        return responseVo;
    }

    @ApiOperation(value = "开始检查项")
    @RequestMapping(value = "/starttask", method = RequestMethod.POST)
    public ResponseVo startTask(
            @ApiParam(name = "userId", value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "orderNo", value = "工单id", required = true) @RequestParam String orderNo,
            @ApiParam(name = "taskid", value = "任务id", required = true) @RequestParam String taskid,
            @ApiParam(name = "deviceNumber", value = "头盔编号", required = true) @RequestParam String deviceNumber) {
        ResponseVo responseVo = helmetUniversalService.startTask(userId, orderNo, taskid, deviceNumber);
        return responseVo;
    }

    @ApiOperation(value = "结束检查项")
    @RequestMapping(value = "/endtask", method = RequestMethod.POST)
    public ResponseVo endTask(
            @ApiParam(name = "userId", value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "orderNo", value = "工单id", required = true) @RequestParam String orderNo,
            @ApiParam(name = "taskid", value = "任务id", required = true) @RequestParam String taskid,
            @ApiParam(name = "pass", value = "是否通过", required = true) @RequestParam String pass,
            @ApiParam(name = "remark", value = "备注", required = false) @RequestParam(required = false) String remark) {
        ResponseVo responseVo = helmetUniversalService.endTask(userId, orderNo, taskid, pass, remark);
        return responseVo;
    }

    @ApiOperation(value = "查询头盔呼叫用户列表")
    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public ResponseVo contact(
            @ApiParam(name = "userId", value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "deviceNumber", value = "头盔编号", required = true) @RequestParam String deviceNumber) {
        ResponseVo responseVo = helmetUniversalService.getContacts(userId);
        return responseVo;
    }
    @ApiOperation(value = "查询头盔呼叫用户列表")
    @RequestMapping(value = "/rabbitmqtest", method = RequestMethod.POST)
    public ResponseVo rabbitmqtest() {
        Map<String, Object> params = new HashMap<String, Object>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String parse = simpleDateFormat.format(new Date());
        params.put("orderId", "orderId");
        params.put("updateTime", parse);
        params.put("userAccount", "");
        params.put("deviceNumber", "2300182");
        RabbitMqVo rabbitMqVo = new RabbitMqVo();
        rabbitMqVo.setsTime(new Date().getTime() + "");
        rabbitMqVo.setMessage(params);
        rabbitMqVo.setMessageId("1");
        rabbitMqVo.setRoutingKey("TYHelmet.Work.Order.Changed");
        mqProducer.sendDataToQueue("TYHelmet.Work.Order.Changed", rabbitMqVo);

        return ResponseVo.success(rabbitMqVo);
    }
}
