package com.tianyi.datacenter.inspect.controller;

import com.tianyi.datacenter.common.framework.controller.SuperController;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.inspect.service.HelmetUniversalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by tianxujin on 2019/5/13 10:56
 */
@Api("头盔对接相关接口")
@RestController
@RequestMapping("helmet")
public class HelmetUniversalController extends SuperController {

    @Autowired
    HelmetUniversalService helmetUniversalService;

    @ApiOperation(value = "查询待检车辆列表")
    @RequestMapping(value = "/cars", method = RequestMethod.POST)
    public ResponseVo cars(
            @ApiParam(name = "userId", value = "用户id", required = true) @RequestParam String userId) {
        ResponseVo responseVo = helmetUniversalService.getPendingCars(userId);
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
        return ResponseVo.success();
    }

    @ApiOperation(value = "结束车辆检查")
    @RequestMapping(value = "/endcar", method = RequestMethod.POST)
    public ResponseVo endCar(
            @ApiParam(name = "userId", value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "wid", value = "工单id", required = true) @RequestParam String wid,
            @ApiParam(name = "isQuick", value = "是否快速检查", required = true) @RequestParam String isQuick) {
        return ResponseVo.success();
    }

    @ApiOperation(value = "开始工单")
    @RequestMapping(value = "/startwork", method = RequestMethod.POST)
    public ResponseVo startWork(
            @ApiParam(name = "userId", value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "wid", value = "工单id", required = true) @RequestParam String wid) {
        ResponseVo responseVo = helmetUniversalService.startWork(userId, wid);
        return responseVo;
    }

    @ApiOperation(value = "结束工单")
    @RequestMapping(value = "/endwork", method = RequestMethod.POST)
    public ResponseVo endWork(
            @ApiParam(name = "userId", value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "wid", value = "工单id", required = true) @RequestParam String wid) {
        ResponseVo responseVo = helmetUniversalService.endWork(userId, wid);
        return responseVo;
    }

    @ApiOperation(value = "开始检查项")
    @RequestMapping(value = "/starttask", method = RequestMethod.POST)
    public ResponseVo startTask(
            @ApiParam(name = "userId", value = "用户id", required = true) @RequestParam String userId,
            @ApiParam(name = "orderNo", value = "工单id", required = true) @RequestParam String orderNo,
            @ApiParam(name = "taskid", value = "任务id", required = true) @RequestParam String taskid) {
        ResponseVo responseVo = helmetUniversalService.startTask(userId, orderNo, taskid);
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


}
