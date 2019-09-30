package com.tianyi.datacenter.inspect.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.constants.OrderConstant;
import com.tianyi.datacenter.common.util.TydeviceLocationUtil;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import com.tianyi.datacenter.inspect.service.HelmetUniversalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private TydeviceLocationUtil tydeviceLocationUtil;
    @Override
    public ResponseVo getPendingCars(String userId, String deviceNumber) {
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(28);
        dsParamDsBuilder.buildCondition("clientId", deviceNumber);// 头盔号
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        if(responseVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
            for (int i = list.size() - 1; i>=0; i--) {
                Map<String, Object> car = list.get(i);
                // 过滤已过期车辆
                if(!StrUtil.isEmptyIfStr(car.get("expireDate"))) {
                    try {
                        DateTime now = DateTime.now();
                        String expireDate = car.get("expireDate").toString();
                        DateTime expireDateTime = DateUtil.parse(expireDate, DatePattern.NORM_DATETIME_FORMAT);
                        if(expireDateTime.isBefore(now)) {// 已过期
                            // 更新大工单过期信息
                            DSParamBuilder orderParamBuilder = new DSParamBuilder(71); // 查询大工单信息
                            orderParamBuilder.buildCondition("oid","equals",car.get("id"));
                            Map<String, String> data = new HashMap<>();
                            data.put("checkstatus", OrderConstant.ORDER_EXPIRE);//0未检查1检查中2确认中3已完成4已过期
                            orderParamBuilder.buildData(data);
                            com.tianyi.datacenter.feign.common.vo.ResponseVo resultVo = dataCenterFeignService.update(orderParamBuilder.build());
                            list.remove(i);
                            continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 过滤已绑定的车辆信息
                if(!StrUtil.isEmptyIfStr(car.get("clientId"))) {
                    if(!deviceNumber.equals(car.get("clientId").toString())) {
                        list.remove(i);
                        continue;
                    }
                }
                // 查询车辆定位信息
                try {
                    Map<String, Object> deviceMap = new HashMap<>();
                    deviceMap.put("deviceID", car.get("name"));
                    deviceMap.put("deviceModel", car.get("modelName"));
                    Map<String, Object> locationInfo = tydeviceLocationUtil.getDeviceLocationInfo(deviceMap);
                    car.put("lat", locationInfo.get("lat"));
                    car.put("lon", locationInfo.get("lon"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
            if(list == null || list.size() == 0) {// 没有执行中的工单，则返回空数组

            } else {
                for(int i=list.size()-1;i>=0;i--) {
                    if(!(OrderConstant.ORDER_UNCHECK.equals(list.get(i).get("state").toString())) && !(OrderConstant.ORDER_CHECKING.equals(list.get(i).get("state").toString()))) {
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
     * @param isQuick
     * @return
     */
    @Override
    public ResponseVo startWork(String userId, String wid, String isQuick) {
        DSParamBuilder workParamBuilder = new DSParamBuilder(16); // 查询检查工单信息
        workParamBuilder.buildCondition("cid","equals",wid);
        com.tianyi.datacenter.feign.common.vo.ResponseVo existVo = dataCenterFeignService.retrieve(workParamBuilder.build());
        String status= OrderConstant.ORDER_UNCHECK;
        if(existVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) existVo.getData().get("rtnData");
            if (list == null || list.size() == 0) {// 没有执行中的工单
            } else {
                status=list.get(0).get("status").toString();
            }
        }
        Map<String, String> data = new HashMap<>();
        data.put("status", OrderConstant.ORDER_CHECKING);
        data.put("isQuick", isQuick);
        if(OrderConstant.ORDER_UNCHECK.equals(status)) {
            data.put("checkStart", DateUtil.now());
        }
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
     * @param isQuick
     * @return
     */
    @Override
    public ResponseVo endWork(String userId, String wid, String isQuick) {
        DSParamBuilder workParamBuilder = new DSParamBuilder(16); // 查询车辆信息
        workParamBuilder.buildCondition("cid","equals",wid);
        Map<String, String> data = new HashMap<>();
        data.put("status", OrderConstant.ORDER_CONFIRM);
        data.put("checkEnd", DateUtil.now());
//        data.put("isQuick", isQuick);
        workParamBuilder.buildData(data);
        com.tianyi.datacenter.feign.common.vo.ResponseVo resultVo = dataCenterFeignService.update(workParamBuilder.build());
        if(resultVo.isSuccess()) {
            return ResponseVo.success();
        } else {
            return ResponseVo.fail(resultVo.getMessage());
        }
    }

    @Override
    public ResponseVo startTask(String userId, String orderNo, String taskid, String deviceNumber) {
        /*DSParamBuilder userParamBuilder = new DSParamBuilder(3); // 查询用户信息
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
        }*/
        // 获取头盔绑定的用户信息
        DSParamDsBuilder orderUserBuilder = new DSParamDsBuilder(77); // 查询大工单信息
        orderUserBuilder.buildCondition("coiid",taskid);
        com.tianyi.datacenter.feign.common.vo.ResponseVo orderUserVo = dataCenterFeignService.retrieve(orderUserBuilder.build());
        String orderUser = "";
        if(orderUserVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) orderUserVo.getData().get("rtnData");
            if (list == null || list.size() == 0) {// 没有用户
            } else {
                orderUser = list.get(0).get("producer").toString();
            }
        }
        DSParamBuilder workParamBuilder = new DSParamBuilder(17); // 更新任务状态
        workParamBuilder.buildCondition("cid","equals",taskid);
        Map<String, Object> data = new HashMap<>();
        data.put("status", OrderConstant.ORDER_CHECKING);
        data.put("checker", orderUser);// userId
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
        data.put("status", OrderConstant.ORDER_CONFIRM);
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
    @Transactional
    public ResponseVo createWorkOrder(String orderId1, String helmetNos, String userNames, String companyName, String model, String deviceNo, String checkType, String expireDate, Integer shootlong) {
        //1、判断工单是否存在
        DSParamBuilder workParamBuilder = new DSParamBuilder(71); // 查询工单信息
        com.tianyi.datacenter.feign.common.vo.ResponseVo orderIdVo = dataCenterFeignService.retrieveId(JSONObject.parseObject("{ \"dataObjectId\": 71}"));
        String orderId = orderIdVo.getData().get("rtnData").toString();
        workParamBuilder.buildCondition("oid","equals",orderId);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(workParamBuilder.build());
        if(responseVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
            if (list == null || list.size() == 0) {// 没有执行中的工单，需要自动生成
                DSParamBuilder orderUserParamBuilder = new DSParamBuilder(76);
                if(helmetNos!=null && helmetNos.length()>0) {
                    helmetNos = helmetNos.replaceAll("，",",");
                    String[] helmetNoArray = helmetNos.split(",");
                    if(userNames!=null && userNames.length()>0) {
                        userNames = userNames.replaceAll("，",",");
                        String[] userNameArray = userNames.split(",");
                        if(helmetNoArray.length == userNameArray.length) {
                            for(int i=0; i <helmetNoArray.length; i++) {
                                com.tianyi.datacenter.feign.common.vo.ResponseVo orderuserIdVo = dataCenterFeignService.retrieveId(JSONObject.parseObject("{ \"dataObjectId\": 76}"));
                                String orderuserId = orderuserIdVo.getData().get("rtnData").toString();
                                Map<String, Object> orderUserData = new HashMap<>();
                                orderUserData.put("ouid", orderuserId);
                                orderUserData.put("orderId", orderId);
                                orderUserData.put("clientId", helmetNoArray[i]);
                                orderUserData.put("checker", userNameArray[i]);
                                orderUserParamBuilder.buildData(orderUserData);
                                dataCenterFeignService.add(orderUserParamBuilder.build());
                            }
                        } else {
                            return ResponseVo.fail("头盔号和用户数量不匹配");
                        }
                    }
                }
                Map<String, Object> data = new HashMap<>();
                data.put("oid", orderId); //工单ID
//                data.put("clientId", helmetNo); //头盔编号
                if(!"".equals(deviceNo)){
                    data.put("deviceNo", deviceNo); //机号
                }
//                data.put("producer", userName); //检查人
                data.put("checkstatus", "0"); //状态 0未检查1检查中2已完成3已过期

                String modelId = ""; // model
                DSParamBuilder modelParamBuilder = new DSParamBuilder(9); // 查询机型
                modelParamBuilder.buildCondition("modelName","equals", model);
                com.tianyi.datacenter.feign.common.vo.ResponseVo modelVo = dataCenterFeignService.retrieve(modelParamBuilder.build());
                if(modelVo.isSuccess()) {
                    List<Map<String, Object>> modellist = (List<Map<String, Object>>) modelVo.getData().get("rtnData");
                    if (modellist == null || modellist.size() == 0) {// 没有机型
                        return ResponseVo.fail("机型不存在");
                    } else {
                        modelId = modellist.get(0).get("mid").toString();
                    }
                } else {
                    return ResponseVo.fail("机型不存在");
                }
                data.put("modelId", modelId); //机型
                String companyId = ""; // companyName
                DSParamBuilder companyParamBuilder = new DSParamBuilder(1); // 查询公司
                companyParamBuilder.buildCondition("companyName","equals", companyName);
                com.tianyi.datacenter.feign.common.vo.ResponseVo companyVo = dataCenterFeignService.retrieve(companyParamBuilder.build());
                if(companyVo.isSuccess()) {
                    List<Map<String, Object>> companylist = (List<Map<String, Object>>) companyVo.getData().get("rtnData");
                    if (companylist == null || companylist.size() == 0) {// 没有公司
                        return ResponseVo.fail("公司不存在");
                    } else {
                        companyId = companylist.get(0).get("cid").toString();
                    }
                } else {
                    return ResponseVo.fail("公司不存在");
                }
                data.put("companyId", companyId); //公司
                String stationId = ""; // checkType
                DSParamBuilder stationParamBuilder = new DSParamBuilder(14); // 查询机型
                stationParamBuilder.buildCondition("stationName","equals", checkType);
                com.tianyi.datacenter.feign.common.vo.ResponseVo stationVo = dataCenterFeignService.retrieve(stationParamBuilder.build());
                if(stationVo.isSuccess()) {
                    List<Map<String, Object>> stationlist = (List<Map<String, Object>>) stationVo.getData().get("rtnData");
                    if (stationlist == null || stationlist.size() == 0) {// 没有公司
                        return ResponseVo.fail("检查类型不存在");
                    } else {
                        stationId = stationlist.get(0).get("cid").toString();
                    }
                } else {
                    return ResponseVo.fail("检查类型不存在");
                }
                data.put("stationId", stationId); // 工位
                data.put("expireDate", expireDate);// 过期时间
                if(shootlong!=null) {
                    data.put("shootlong", shootlong); // 拍摄时长单位秒
                } else {
                    data.put("shootlong", 0); // 拍摄时长单位秒
                }
                workParamBuilder.buildData(data);
                com.tianyi.datacenter.feign.common.vo.ResponseVo addOrderVo = dataCenterFeignService.add(workParamBuilder.build());
                DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(72);// 获取工位配置的检查项类别ID列表
                dsParamDsBuilder.buildCondition("modelId", modelId)
                        .buildCondition("deviceNo", deviceNo)
                        .buildCondition("stationId", stationId)
                        .buildCondition("companyId", companyId);
                com.tianyi.datacenter.feign.common.vo.ResponseVo checkTypeVo = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
                if(checkTypeVo.isSuccess()) {
                    List<Map<String, Object>> checkTypeList = (List<Map<String, Object>>) checkTypeVo.getData().get("rtnData");
                    if (checkTypeList == null || checkTypeList.size() == 0) {//
                        DSParamDsBuilder dsParamDsBuilder1 = new DSParamDsBuilder(72);// 获取工位配置的检查项类别ID列表
                        dsParamDsBuilder1.buildCondition("modelId", modelId)
                                .buildCondition("deviceNo", "")
                                .buildCondition("stationId", stationId)
                                .buildCondition("companyId", companyId);
                        com.tianyi.datacenter.feign.common.vo.ResponseVo checkTypeVo1 = dataCenterFeignService.retrieve(dsParamDsBuilder1.build());
                        if(checkTypeVo1.isSuccess()) {
                            List<Map<String, Object>> checkTypeList1 = (List<Map<String, Object>>) checkTypeVo1.getData().get("rtnData");
                            if (checkTypeList1 == null || checkTypeList1.size() == 0) {//
                                return ResponseVo.fail("请先导入【" + checkType + "】的检查项目");
                            } else {
                                generateCheckOrder(checkTypeList1, orderId, modelId, deviceNo, stationId, companyId, "");
                            }
                        }
                    } else {// 生成检查单
                        generateCheckOrder(checkTypeList, orderId, modelId, deviceNo, stationId, companyId, "");
                    }
                }
            } else {//存在提示已创建
                return ResponseVo.fail("工单已创建："+orderId);
            }
        }
        Map<String, Object> orderIdMap = new HashMap<>();
        orderIdMap.put("orderId", orderId);
        return ResponseVo.success(orderIdMap,"工单创建成功");
    }

    @Override
    public ResponseVo workStatus(String orderId) {
        //1、判断工单是否存在
        DSParamBuilder workParamBuilder = new DSParamBuilder(71); // 查询工单信息
        workParamBuilder.buildCondition("oid","equals",orderId);
        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieve(workParamBuilder.build());
        ResponseVo resultVo = ResponseVo.fail("查询失败");
        if(responseVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
            if (list == null || list.size() == 0) {// 没有执行中的工单
                resultVo = ResponseVo.fail(responseVo.getMessage());
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put("orderId", orderId);
                result.put("checkstatus", list.get(0).get("checkstatus"));
                resultVo = ResponseVo.success(result);
            }
        } else {
            resultVo = ResponseVo.fail(responseVo.getMessage());
        }
        return resultVo;
    }

    @Override
    public ResponseVo workStatusExpiry(String orderId) {
        DSParamBuilder workParamBuilder = new DSParamBuilder(71); // 更新工单状态
        workParamBuilder.buildCondition("oid","equals",orderId);
        Map<String, Object> data = new HashMap<>();
        data.put("status", OrderConstant.ORDER_CONFIRMED);
        workParamBuilder.buildData(data);
        com.tianyi.datacenter.feign.common.vo.ResponseVo resultVo = dataCenterFeignService.update(workParamBuilder.build());
        if(resultVo.isSuccess()) {
            return ResponseVo.success();
        } else {
            return ResponseVo.fail(resultVo.getMessage());
        }
    }

    @Override
    public ResponseVo startCar(String userId, String wid, String isQuick, String deviceNumber) {
        DSParamBuilder orderParamBuilder = new DSParamBuilder(71); // 查询大工单信息
        orderParamBuilder.buildCondition("oid","equals",wid);
        com.tianyi.datacenter.feign.common.vo.ResponseVo existVo = dataCenterFeignService.retrieve(orderParamBuilder.build());
        String checkstatus = OrderConstant.ORDER_UNCHECK;
        String clientId = "";
        String checker = "";
        if(existVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) existVo.getData().get("rtnData");
            if (list == null || list.size() == 0) {// 没有执行中的工单
            } else {
                checkstatus = list.get(0).get("checkstatus").toString();
                if(list.get(0).get("clientId") != null) {
                    clientId = list.get(0).get("clientId").toString();
                }
                if(list.get(0).get("producer") != null) {
                    checker = list.get(0).get("producer").toString();
                }
            }
        }
        if (!OrderConstant.ORDER_UNCHECK.equals(checkstatus) && !clientId.equals("") && !clientId.equals(deviceNumber)) {
            return ResponseVo.fail("工单已绑定用户"+checker);
        }
        Map<String, String> data = new HashMap<>();
        data.put("checkstatus", OrderConstant.ORDER_CHECKING);//0未检查1检查中2已完成3已过期
        data.put("isQuick", isQuick);
        // 获取头盔绑定的用户信息
        DSParamBuilder orderUserBuilder = new DSParamBuilder(76); // 查询大工单信息
        orderUserBuilder.buildCondition("orderId","equals",wid);
        orderUserBuilder.buildCondition("clientId","equals",deviceNumber);
        com.tianyi.datacenter.feign.common.vo.ResponseVo orderUserVo = dataCenterFeignService.retrieve(orderUserBuilder.build());
        String orderUser = "";
        if(orderUserVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) orderUserVo.getData().get("rtnData");
            if (list == null || list.size() == 0) {// 没有用户
            } else {
                orderUser = list.get(0).get("checker").toString();
            }
        }
        if (OrderConstant.ORDER_UNCHECK.equals(checkstatus)) {
            data.put("checkStart", DateUtil.now());
            data.put("producer", orderUser);
            data.put("clientId", deviceNumber);
        }
        orderParamBuilder.buildData(data);
        com.tianyi.datacenter.feign.common.vo.ResponseVo resultVo = dataCenterFeignService.update(orderParamBuilder.build());
        if(resultVo.isSuccess()) {
            return ResponseVo.success();
        } else {
            return ResponseVo.fail(resultVo.getMessage());
        }
    }

    @Override
    public ResponseVo endCar(String userId, String wid, String isQuick) {
        DSParamBuilder orderParamBuilder = new DSParamBuilder(71); // 查询大工单信息
        orderParamBuilder.buildCondition("oid","equals",wid);
        Map<String, String> data = new HashMap<>();
        if("1".equals(isQuick)) {
            data.put("checkstatus", OrderConstant.ORDER_CONFIRM);//0未检查1检查中2确认中3已完成4已过期
        } else {
            DSParamDsBuilder isQuickCountParam = new DSParamDsBuilder(79);
            isQuickCountParam.buildCondition("oid", wid);
            com.tianyi.datacenter.feign.common.vo.ResponseVo isQuickCountVo = dataCenterFeignService.retrieve(isQuickCountParam.build());
            if(isQuickCountVo.isSuccess()) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) isQuickCountVo.getData().get("rtnData");
                if (list == null || list.size() == 0) {// 没有快速检查
                    data.put("checkstatus", OrderConstant.ORDER_CONFIRMED);//0未检查1检查中2确认中3已完成4已过期
                } else {
                    int isQuickCount = Integer.parseInt(list.get(0).get("isQuickCount").toString());
                    if(isQuickCount > 0) {
                        data.put("checkstatus", OrderConstant.ORDER_CONFIRM);//0未检查1检查中2确认中3已完成4已过期
                    } else {// 没有快速检查
                        data.put("checkstatus", OrderConstant.ORDER_CONFIRMED);//0未检查1检查中2确认中3已完成4已过期
                    }
                }
            }
        }
        data.put("checkEnd", DateUtil.now());
//        data.put("isQuick", isQuick);
        orderParamBuilder.buildData(data);
        com.tianyi.datacenter.feign.common.vo.ResponseVo resultVo = dataCenterFeignService.update(orderParamBuilder.build());
        if(resultVo.isSuccess()) {
            return ResponseVo.success();
        } else {
            return ResponseVo.fail(resultVo.getMessage());
        }
    }

    @Override
    public ResponseVo getContacts(String userId) {
        DSParamBuilder dsParamBuilder = new DSParamBuilder(84);
        dsParamBuilder.buildCondition("userId","equals",userId);
        com.tianyi.datacenter.feign.common.vo.ResponseVo userContactsVo= dataCenterFeignService.retrieve
                (dsParamBuilder
                .build());
        List<String> contactIds = new ArrayList();
        if (userContactsVo.isSuccess()&&userContactsVo.getMessage()==null){
            List<Map<String, Object>> userContactsList = (List<Map<String, Object>>) userContactsVo.getData().get("rtnData");
            for (Map<String, Object> map : userContactsList) {
                contactIds.add((String)map.get("contactId"));
            }
        }
        List<String> contacts = new ArrayList();
        for (String contactId : contactIds) {
            DSParamBuilder dsParamBuilderContact = new DSParamBuilder(3);
            dsParamBuilderContact.buildCondition("uid","equals",contactId);
            com.tianyi.datacenter.feign.common.vo.ResponseVo ContactsVo= dataCenterFeignService.retrieve
                    (dsParamBuilderContact.build());
            List<Map<String, Object>> contactsList = (List<Map<String, Object>>) ContactsVo.getData().get("rtnData");
            for (Map<String, Object> map : contactsList) {
                contacts.add((String)map.get("account"));
            }
        }
        return ResponseVo.success(contacts);
    }

    @Override
    public ResponseVo searchOneOrderByDeviceNo(String companyName, String model, String deviceNo, String checkType) {
        DSParamDsBuilder searchOneOrder = new DSParamDsBuilder(81);
        searchOneOrder.buildCondition("companyName",companyName);
        searchOneOrder.buildCondition("model",model);
        searchOneOrder.buildCondition("deviceNo",deviceNo);
        searchOneOrder.buildCondition("checkType",checkType);
        com.tianyi.datacenter.feign.common.vo.ResponseVo searchOneVo = dataCenterFeignService.retrieve(searchOneOrder.build());
        if (searchOneVo.isSuccess()&&searchOneVo.getMessage()==null){
            List<Map<String, Object>> data = (List<Map<String, Object>>) searchOneVo.getData().get("rtnData");
            return ResponseVo.success(data.get(0));
        }
        return ResponseVo.success();
    }

    private void generateCheckOrder(List<Map<String, Object>> workList, String oid, String modelId, String deviceNo, String stationId, String companyId, String userName) {
        for(Map<String, Object> work : workList) {
            com.tianyi.datacenter.feign.common.vo.ResponseVo workIdVo = dataCenterFeignService.retrieveId(JSONObject.parseObject("{ \"dataObjectId\": 16}"));
            String workId = workIdVo.getData().get("rtnData").toString();
            work.put("oid", oid);
            work.put("cid", workId);
            work.put("status", OrderConstant.ORDER_UNCHECK);
            DateTime now = DateTime.now();
            String createTimeStr = now.toString(DatePattern.NORM_DATETIME_FORMAT);
            work.put("createTime", createTimeStr);
            // 保存16检查工单
            DSParamBuilder workParamBuilder = new DSParamBuilder(16);
            workParamBuilder.buildData(work);
            dataCenterFeignService.add(workParamBuilder.build());
            // 同时生成检查工单的检查项明细17,从73检查项目列表查询
            DSParamDsBuilder itemParamBuilder = new DSParamDsBuilder(73);
            itemParamBuilder.buildCondition("modelId", modelId)
                    .buildCondition("deviceNo", deviceNo)
                    .buildCondition("stationId", stationId)
                    .buildCondition("companyId", companyId);
            itemParamBuilder.buildCondition("checkTypeId",work.get("checktypeId").toString());
            com.tianyi.datacenter.feign.common.vo.ResponseVo itemVo = dataCenterFeignService.retrieve(itemParamBuilder.build());
            if(itemVo.isSuccess()) {
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) itemVo.getData().get("rtnData");
                if (itemList.size() > 0) {
                    DSParamBuilder taskParamBuilder = new DSParamBuilder(17);
                    for(Map<String, Object> task : itemList) {
                        com.tianyi.datacenter.feign.common.vo.ResponseVo taskIdVo = dataCenterFeignService.retrieveId(JSONObject.parseObject("{ \"dataObjectId\": 17}"));
                        String taskId = taskIdVo.getData().get("rtnData").toString();
                        task.put("cid", taskId);
                        task.put("checkorderId", workId);
                        task.put("status", OrderConstant.ORDER_UNCHECK);
//                        task.put("checker", userName);
                        taskParamBuilder.buildData(task);
                        dataCenterFeignService.add(taskParamBuilder.build());
                    }
                } else {
                    DSParamDsBuilder itemParamBuilder1 = new DSParamDsBuilder(73);
                    itemParamBuilder1.buildCondition("modelId", modelId)
                            .buildCondition("stationId", stationId)
                            .buildCondition("companyId", companyId);
                    itemParamBuilder1.buildCondition("checkTypeId",work.get("checktypeId").toString());
                    com.tianyi.datacenter.feign.common.vo.ResponseVo itemVo1 = dataCenterFeignService.retrieve(itemParamBuilder1.build());
                    if(itemVo1.isSuccess()) {
                        List<Map<String, Object>> itemList1 = (List<Map<String, Object>>) itemVo1.getData().get("rtnData");
                        if (itemList1.size() > 0) {
                            DSParamBuilder taskParamBuilder = new DSParamBuilder(17);
                            for (Map<String, Object> task : itemList1) {
                                com.tianyi.datacenter.feign.common.vo.ResponseVo taskIdVo = dataCenterFeignService.retrieveId(JSONObject.parseObject("{ \"dataObjectId\": 17}"));
                                String taskId = taskIdVo.getData().get("rtnData").toString();
                                task.put("cid", taskId);
                                task.put("checkorderId", workId);
                                task.put("status", OrderConstant.ORDER_UNCHECK);
//                        task.put("checker", userName);
                                taskParamBuilder.buildData(task);
                                dataCenterFeignService.add(taskParamBuilder.build());
                            }
                        }
                    }
                    //return ResponseVo.fail(cid + ":请先配置车辆检查项目");
                }
            } else {
                //return ResponseVo.fail(itemVo.getMessage());
            }
        }
        //return ResponseVo.success();
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
                        task.put("status", OrderConstant.ORDER_UNCHECK);
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
                task.put("status", OrderConstant.ORDER_UNCHECK);
                task.put("itemId", work.get("itemId").toString());
                task.put("source", work.get("source").toString());
                taskParamBuilder.buildData(task);
                dataCenterFeignService.add(taskParamBuilder.build());
            }
        }
    }

}
