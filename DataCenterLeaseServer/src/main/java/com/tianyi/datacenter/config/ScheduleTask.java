package com.tianyi.datacenter.config;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import java.util.*;
/**
 * @author liulele ${Date}
 * @version 0.1
 **/
@Configuration
@Component
@EnableScheduling
public class ScheduleTask {
    @Autowired
    private TianYiConfig tianYiIntesrvImgocrmodelVideoUrl;
    @Autowired
    private DataCenterFeignService dataCenterFeignService;

    public void uploadTask() {
        Map<String, String> header = new HashMap<>();
        String param = "{}";
        String result2 = HttpRequest.post(tianYiIntesrvImgocrmodelVideoUrl
                .getTianYiIntesrvImgocrmodelVideoUrl() + "/interface/video/getQueryModelList")
                .body(param)
                .execute().body();
        System.out.println(result2+"11111111111111");
        JSONObject jo = JSONObject.parseObject(result2);
        if (jo.get("data")==null){
            return;
        }
        String data1 = (String) jo.get("data");
        JSONObject jsonObject = JSONObject.parseObject(data1);
        List<Map<String, Object>> list = (List<Map<String, Object>>) jsonObject.get("modellist");
        DSParamBuilder dsParamBuilderSave = new DSParamBuilder(80);
        for (Map<String, Object> map : list) {//存储图像识别模型
            String name = (String) map.get("name");
            Integer id = (Integer) map.get("id");
            Map data = new HashMap();
            data.put("name", name);
            data.put("mid", id + "");
            dsParamBuilderSave.buildData(data);
            dataCenterFeignService.add(dsParamBuilderSave.build());
        }     //执行时间
    }
}