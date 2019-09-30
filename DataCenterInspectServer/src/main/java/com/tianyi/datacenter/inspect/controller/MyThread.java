
package com.tianyi.datacenter.inspect.controller;


import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyThread {
    private DataCenterFeignService dataCenterFeignService;

    private boolean stopMe = true;

    private int count = 1;

    public void stopMe() {
        stopMe = false;
    }

    @Scheduled(cron = "0/3 * * * * ?")//fixedDelay = 5000表示当方法执行完毕5000ms后，Spring scheduling会再次调用该方法
    public void run() {
        WebSocketServer wbs = new WebSocketServer();
        DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(69);
        ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
        List<Map<String, Object>> rtnData = (List<Map<String, Object>>) retrieve.getData().get("rtnData");
        Integer total = (Integer) rtnData.get(0).get("total");
//        String downtime = (String) rtnData.get(0).get("downtime");
        DSParamBuilder dsParamBuilder = new DSParamBuilder(70);
        ResponseVo retrieve1 = dataCenterFeignService.retrieve(dsParamBuilder.build());
        List<Map<String, Object>> list = (List<Map<String, Object>>) retrieve1.getData().get("rtnData");
        //第一次插入数值
        if (list == null || list.size() == 0) {
            DSParamBuilder dsParamBuilderInsert = new DSParamBuilder(70);
            Map data = new HashMap();
            data.put("carCount", 0);
            dsParamBuilderInsert.buildData(data);
            dsParamBuilderInsert.buildCondition("cid", "equals", "1");
            dataCenterFeignService.add(dsParamBuilderInsert.build());
        } else {
            //查询
            ResponseVo retrieveNew = dataCenterFeignService.retrieve(dsParamBuilder.build());
            List<Map<String, Object>> listNew = (List<Map<String, Object>>) retrieveNew.getData().get("rtnData");
            if (total > (Integer) listNew.get(0).get("carCount")) {
                System.out.println("change");
                String time=null;
                wbs.onMessage(time);
                //更新车辆总数
                DSParamBuilder dsParamBuilderInsert = new DSParamBuilder(70);
                Map data = new HashMap();
                data.put("carCount", total);
                dsParamBuilderInsert.buildData(data);
                dsParamBuilderInsert.buildCondition("cid", "equals", "1");
                dataCenterFeignService.add(dsParamBuilderInsert.build());
            }
        }
    }
}


