package com.tianyi.datacenter.inspect.service;

import com.tianyi.datacenter.common.vo.ResponseVo;

/**
 * Created by tianxujin on 2019/6/13 13:58
 */
public interface HelmetUniversalService {

    ResponseVo getPendingCars(String userId);

    ResponseVo getPendingWorks(String userId, String cid);

    ResponseVo startWork(String userId, String wid);

    ResponseVo endWork(String userId, String wid);

    ResponseVo startTask(String userId, String orderNo, String taskid);

    ResponseVo endTask(String userId, String orderNo, String taskid, String pass, String remark);

    ResponseVo getContacts(String userId);
}
