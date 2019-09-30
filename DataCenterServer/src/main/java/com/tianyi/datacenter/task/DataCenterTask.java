package com.tianyi.datacenter.task;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.access.service.DataAccessService;
import com.tianyi.datacenter.resource.util.JsonTreeUtil;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by tianxujin on 2019/6/10 9:56
 */
@Service
public class DataCenterTask {

    @Autowired
    private DataAccessService dataAccessService;

    /**
     * 每30分钟执行一次
     */
    @Scheduled(cron="0 0/30 * * * ?")
    public void cornSetDeptAllList() {
        int dataObjectId = 2; // 维护哪个对象
        String idKey = "did";
        String pidKey = "fatherId";
        String fullPathName = "fullPath";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dataObjectId",dataObjectId);
        // update condition
        List<Map<String, Object>> paramList = new ArrayList<>();
        jsonObject.put("condition",paramList);
        Map<String, Integer> pageMap = new HashMap<>();
        pageMap.put("page", 0);
        pageMap.put("pageSize", 0);//全是0，代表不分页
        jsonObject.put("pageInfo",pageMap);
        try {
            ResponseVo responseVo = dataAccessService.queryData(jsonObject);
            if(responseVo.isSuccess()) {
                List<Map<String, Object>> deptList = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
                for (Map<String, Object> map : deptList) {
                    String fullPath = JsonTreeUtil.findFullPath(map,deptList, idKey, pidKey);
                    if(map.get(fullPathName)==null || !fullPath.equals(map.get(fullPathName).toString())) {
                        paramList.clear();
                        Map<String, Object> conditionMap = new HashMap<>();
                        conditionMap.put("key", idKey);
                        conditionMap.put("condition", "equals");
                        conditionMap.put("value", map.get(idKey));
                        paramList.add(conditionMap);
                        map.put("fullPath", fullPath);
                        jsonObject.put("data", map);
                        dataAccessService.updateData(jsonObject);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
