package com.tianyi.datacenter.inspect.service;

import com.tianyi.datacenter.common.vo.ResponseVo;

/**
 * Created by tianxujin on 2019/6/13 13:58
 */
public interface HelmetUniversalService {

    ResponseVo getPendingCars(String userId, String deviceNumber);

    ResponseVo getPendingWorks(String userId, String cid);

    ResponseVo startWork(String userId, String wid, String isQuick);

    ResponseVo endWork(String userId, String wid, String isQuick);

    ResponseVo startTask(String userId, String orderNo, String taskid, String deviceNumber);

    ResponseVo endTask(String userId, String orderNo, String taskid, String pass, String remark);

    ResponseVo createWorkOrder(String orderId, String helmetNos, String userNames, String companyName, String model, String deviceNo, String checkType, String expireDate, Integer shootlong);

    ResponseVo workStatus(String orderId);

    ResponseVo workStatusExpiry(String orderId);

    ResponseVo startCar(String userId, String wid, String isQuick, String deviceNumber);

    ResponseVo endCar(String userId, String wid, String isQuick);

    ResponseVo getContacts(String userId);

    ResponseVo searchOneOrderByDeviceNo(String companyName, String model, String deviceNo, String checkType);
}
