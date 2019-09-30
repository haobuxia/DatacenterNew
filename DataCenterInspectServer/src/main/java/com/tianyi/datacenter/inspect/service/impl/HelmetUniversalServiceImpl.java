package com.tianyi.datacenter.inspect.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import com.tianyi.datacenter.inspect.service.HelmetUniversalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tianxujin on 2019/6/13 13:59
 */
@Service
public class HelmetUniversalServiceImpl implements HelmetUniversalService {

    @Autowired
    private DataCenterFeignService dataCenterFeignService;

    @Override
    public ResponseVo getPendingCars(String userId) {
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(28);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        if(responseVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
            return ResponseVo.success(list);
        }
        return ResponseVo.fail(responseVo.getMessage());
    }

    @Override
    public ResponseVo getPendingWorks(String userId, String cid) {
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(29);// 获取执行中的检查单
        dsParamDsBuilder.buildCondition("carId", cid);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        if(responseVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
            if(list == null || list.size() == 0) {// 没有执行中的工单，需要自动生成
                // 1、查询车辆信息
                DSParamBuilder carParamBuilder = new DSParamBuilder(10); // 查询车辆信息
                carParamBuilder.buildCondition("cid","equals",cid);
                com.tianyi.datacenter.feign.common.vo.ResponseVo carVo = dataCenterFeignService.retrieve(carParamBuilder.build());
                if(carVo.isSuccess()) {
                    List<Map<String, Object>> carList = (List<Map<String, Object>>) carVo.getData().get("rtnData");
                    if(carList.size()>0){
                        Map<String, Object> car = carList.get(0);
                        if(car.get("checkstatus")!=null && !"".equals(car.get("checkstatus").toString())) {
                            String checkstatus = car.get("checkstatus").toString();
                            // 车辆需要生成checkstatus的检查单
                            car.put("status",checkstatus);
                            carParamBuilder.buildData(car);
                            dataCenterFeignService.update(carParamBuilder.build());
                            // 1、判断【工位】有无异常项
                            DSParamDsBuilder problemParamDsBuilder = new DSParamDsBuilder(33);
                            problemParamDsBuilder.buildCondition("carId", cid)
                                    .buildCondition("checkstatus", checkstatus);
                            com.tianyi.datacenter.feign.common.vo.ResponseVo problemWorkVo = dataCenterFeignService.retrieve(problemParamDsBuilder.build());
                            if(problemWorkVo.isSuccess()) {
                                List<Map<String, Object>> problemWorkList = (List<Map<String, Object>>) problemWorkVo.getData().get("rtnData");
                                if (problemWorkList.size() > 0) {
                                    // 1.1、有异常项生成异常项的即可
                                    generateProblemTask(problemWorkList);
                                } else {
                                    // 1.2、无异常生成所有的
                                    // 获取工位所有检查分类列表
                                    DSParamDsBuilder workParamDsBuilder = new DSParamDsBuilder(32);
                                    workParamDsBuilder.buildCondition("carId", cid)
                                            .buildCondition("checkstatus", checkstatus);
                                    com.tianyi.datacenter.feign.common.vo.ResponseVo workVo = dataCenterFeignService.retrieve(workParamDsBuilder.build());
                                    if(workVo.isSuccess()) {
                                        List<Map<String, Object>> workList = (List<Map<String, Object>>) workVo.getData().get("rtnData");
                                        if (workList.size() > 0) {
                                            generateNormalTask(workList, cid);
                                        } else {
                                            return ResponseVo.fail(cid + ":请先配置车辆检查分类");
                                        }
                                    } else {
                                        return ResponseVo.fail(workVo.getMessage());
                                    }
                                }
                            } else {
                                return ResponseVo.fail(problemWorkVo.getMessage());
                            }

                            // 2 获取台上检查异常项关联项目（checkstatus=="入库检查"）
                            if(checkstatus=="入库检查") {
                                DSParamDsBuilder pwParamDsBuilder = new DSParamDsBuilder(33);
                                pwParamDsBuilder.buildCondition("carId", cid)
                                        .buildCondition("checkstatus", "台上检查");
                                com.tianyi.datacenter.feign.common.vo.ResponseVo problemWorkVo1 = dataCenterFeignService.retrieve(pwParamDsBuilder.build());
                                if(problemWorkVo1.isSuccess()) {
                                    List<Map<String, Object>> problemWorkList1 = (List<Map<String, Object>>) problemWorkVo1.getData().get("rtnData");
                                    if (problemWorkList1.size() > 0) {
                                        // 生成台上检查异常项
                                        generateProblemTask(problemWorkList1);
                                    }
                                }
                            } else {
                            }
                            return ResponseVo.fail(cid + ":车辆检查状态未设置错误");
                        }
                    } else {
                        return ResponseVo.fail(cid + ":车辆不存在");
                    }
                } else {
                    return ResponseVo.fail(carVo.getMessage());
                }
                // 以上没有返回工单数据时，需要查询数据库返回工单数据
                // 获取执行中的检查单
                DSParamDsBuilder resultParamDsBuilder = new DSParamDsBuilder(29);
                resultParamDsBuilder.buildCondition("carId", cid);
                com.tianyi.datacenter.feign.common.vo.ResponseVo resultVo = dataCenterFeignService.retrieve(resultParamDsBuilder.build());
                if(resultVo.isSuccess()) {
                    List<Map<String, Object>> result = (List<Map<String, Object>>) resultVo.getData().get("rtnData");
                    generateTask(result);// 加载待检查项目列表并组合到工单中
                    return ResponseVo.success(result);
                }
            } else {
                for(int i=list.size()-1;i>=0;i--) {
                    if(!("未检查".equals(list.get(i).get("state").toString())) && !("检查中".equals(list.get(i).get("state").toString()))) {
                        list.remove(i);
                    }
                }
                generateTask(list);// 加载待检查项目列表并组合到工单中
            }
            return ResponseVo.success(list);
        } else {
            return ResponseVo.fail(responseVo.getMessage());
        }
    }

    /**
     * 开始工单，将工单状态改成【执行中】
     * @param userId
     * @param wid
     * @return
     */
    @Override
    public ResponseVo startWork(String userId, String wid) {
        DSParamBuilder workParamBuilder = new DSParamBuilder(16); // 查询检查工单信息
        workParamBuilder.buildCondition("cid","equals",wid);
        Map<String, String> data = new HashMap<>();
        data.put("status", "检查中");
        workParamBuilder.buildData(data);
        com.tianyi.datacenter.feign.common.vo.ResponseVo resultVo = dataCenterFeignService.update(workParamBuilder.build());
        if(resultVo.isSuccess()) {
            return ResponseVo.success();
        } else {
            return ResponseVo.fail(resultVo.getMessage());
        }
    }

    /**
     * 结束工单，将工单状态改成【确认中】
     * @param userId
     * @param wid
     * @return
     */
    @Override
    public ResponseVo endWork(String userId, String wid) {
        DSParamBuilder workParamBuilder = new DSParamBuilder(16); // 查询车辆信息
        workParamBuilder.buildCondition("cid","equals",wid);
        Map<String, String> data = new HashMap<>();
        data.put("status", "确认中");
        workParamBuilder.buildData(data);
        com.tianyi.datacenter.feign.common.vo.ResponseVo resultVo = dataCenterFeignService.update(workParamBuilder.build());
        if(resultVo.isSuccess()) {
            return ResponseVo.success();
        } else {
            return ResponseVo.fail(resultVo.getMessage());
        }
    }

    @Override
    public ResponseVo startTask(String userId, String orderNo, String taskid) {
        DSParamBuilder userParamBuilder = new DSParamBuilder(3); // 查询用户信息
        userParamBuilder.buildCondition("uid","equals",userId);
        com.tianyi.datacenter.feign.common.vo.ResponseVo userVo = dataCenterFeignService.retrieve(userParamBuilder.build());
        String userName="";
        if(userVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) userVo.getData().get("rtnData");
            if(list == null || list.size() == 0) {
                return ResponseVo.fail("用户不存在！");
            } else {
                userName = list.get(0).get("name").toString();
            }
        } else {
            return ResponseVo.fail("用户不存在！");
        }
        DSParamBuilder workParamBuilder = new DSParamBuilder(17); // 更新任务状态
        workParamBuilder.buildCondition("cid","equals",taskid);
        Map<String, Object> data = new HashMap<>();
        data.put("status", "检查中");
        data.put("checker", userName);// userId
        data.put("checkStart", DateUtil.now());// 检查开始时间
        workParamBuilder.buildData(data);
        com.tianyi.datacenter.feign.common.vo.ResponseVo resultVo = dataCenterFeignService.update(workParamBuilder.build());
        if(resultVo.isSuccess()) {
            return ResponseVo.success();
        } else {
            return ResponseVo.fail(resultVo.getMessage());
        }
    }

    @Override
    public ResponseVo endTask(String userId, String orderNo, String taskid, String pass, String remark) {
        DSParamBuilder workParamBuilder = new DSParamBuilder(17); // 更新任务状态
        workParamBuilder.buildCondition("cid","equals",taskid);
        Map<String, Object> data = new HashMap<>();
        data.put("status", "确认中");
//        data.put("checker", userId);
        data.put("checkEnd", DateUtil.now());// 检查结束时间
        data.put("result", pass);
        data.put("checkValue", remark);
        workParamBuilder.buildData(data);
        com.tianyi.datacenter.feign.common.vo.ResponseVo resultVo = dataCenterFeignService.update(workParamBuilder.build());
        if(resultVo.isSuccess()) {
            return ResponseVo.success();
        } else {
            return ResponseVo.fail(resultVo.getMessage());
        }
    }

    @Override
    public ResponseVo getContacts(String userId) {
        List<String> contacts = new ArrayList<>();
        contacts.add("yanletao");// 测试管理员账号
        return ResponseVo.success(contacts);
    }

    /**
     * 加载生成的任务列表
     * @param result
     */
    private void generateTask(List<Map<String, Object>> result) {
        DSParamDsBuilder itemParamDsBuilder = new DSParamDsBuilder(30);
        for(Map<String, Object> work:result) {
            itemParamDsBuilder.buildCondition("checkOrderId", work.get("id"));
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo1 = dataCenterFeignService.retrieve(itemParamDsBuilder.build());
            if(responseVo1.isSuccess()) {
                List<Map<String, Object>> taskList = (List<Map<String, Object>>) responseVo1.getData().get("rtnData");
                work.put("tasks", taskList);
            }
        }
    }

    /**
     * 创建工位所有检查项目
     * @param workList
     * @param cid
     */
    private void generateNormalTask(List<Map<String, Object>> workList, String cid) {
        for(Map<String, Object> work : workList) {
            // car.cid carId,sc.stationId,sc.checktypeId,'未检查' status
            com.tianyi.datacenter.feign.common.vo.ResponseVo workIdVo = dataCenterFeignService.retrieveId(JSONObject.parseObject("{ \"dataObjectId\": 16}"));
            String workId = workIdVo.getData().get("rtnData").toString();
            work.put("cid", workId);
            DateTime now = DateTime.now();
            String createTimeStr = now.toString(DatePattern.NORM_DATETIME_FORMAT);
            work.put("createTime", createTimeStr);
            // 保存16检查工单
            DSParamBuilder workParamBuilder = new DSParamBuilder(16);
            workParamBuilder.buildData(work);

            dataCenterFeignService.add(workParamBuilder.build());
            // 同时生成检查工单的检查项明细17,从11检查项目列表查询
            DSParamBuilder itemParamBuilder = new DSParamBuilder(11);
            itemParamBuilder.buildCondition("checkitemtypeId","equals",work.get("checktypeId").toString());
            com.tianyi.datacenter.feign.common.vo.ResponseVo itemVo = dataCenterFeignService.retrieve(itemParamBuilder.build());
            if(itemVo.isSuccess()) {
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) itemVo.getData().get("rtnData");
                if (itemList.size() > 0) {
                    DSParamBuilder taskParamBuilder = new DSParamBuilder(17);
                    for(Map<String, Object> item : itemList) {
                        Map<String, Object> task = new HashMap<>();
                        com.tianyi.datacenter.feign.common.vo.ResponseVo taskIdVo = dataCenterFeignService.retrieveId(JSONObject.parseObject("{ \"dataObjectId\": 17}"));
                        String taskId = taskIdVo.getData().get("rtnData").toString();
                        task.put("cid", taskId);
                        task.put("checkorderId", workId);
                        task.put("status", "未检查");
                        task.put("itemId", item.get("cid").toString());
                        taskParamBuilder.buildData(task);
                        dataCenterFeignService.add(taskParamBuilder.build());
                    }
                } else {
                    //return ResponseVo.fail(cid + ":请先配置车辆检查项目");
                }
            } else {
                //return ResponseVo.fail(itemVo.getMessage());
            }
        }
        //return ResponseVo.success();
    }

    /**
     * 创建异常项关联的检查项
     * @param workList
     */
    private void generateProblemTask(List<Map<String, Object>> workList) {
        if (workList.size() > 0) {
            //ci.itemId,co.carId,co.stationId,co.checktypeId,'1' source
            List<String> tempWorkIds= new ArrayList<>();
            DSParamBuilder taskParamBuilder = new DSParamBuilder(17);
            for(Map<String, Object> work : workList) {
                String checktypeId = work.get("checktypeId").toString(); // 检查项分类ID,一个分类只生成一个检查单
                String workId = "";
                if(!tempWorkIds.contains(checktypeId)) {
                    tempWorkIds.add(checktypeId);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo workIdVo = dataCenterFeignService.retrieveId(JSONObject.parseObject("{ \"dataObjectId\": 16}"));
                    workId = workIdVo.getData().get("rtnData").toString();
                    work.put("cid", workId);
                    DateTime now = DateTime.now();
                    String createTimeStr = now.toString(DatePattern.NORM_DATETIME_FORMAT);
                    work.put("createTime", createTimeStr);
                    // 保存16检查工单
                    DSParamBuilder workParamBuilder = new DSParamBuilder(16);
                    workParamBuilder.buildData(work);
                    dataCenterFeignService.add(workParamBuilder.build());
                }
                // 同时生成检查工单的检查项明细17,根据itemId生成，这是异常情况
                Map<String, Object> task = new HashMap<>();
                com.tianyi.datacenter.feign.common.vo.ResponseVo taskIdVo = dataCenterFeignService.retrieveId(JSONObject.parseObject("{ \"dataObjectId\": 17}"));
                String taskId = taskIdVo.getData().get("rtnData").toString();
                task.put("cid", taskId);
                task.put("checkorderId", workId);
                task.put("status", "未检查");
                task.put("itemId", work.get("itemId").toString());
                task.put("source", work.get("source").toString());
                taskParamBuilder.buildData(task);
                dataCenterFeignService.add(taskParamBuilder.build());
            }
        }
    }

}
