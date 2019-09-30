package com.tianyi.datacenter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 业务数据库配置对象
 *
 * @author tianxujin
 * 2019/02/28
 * @version 0.1
 **/
@Component
public class TianYiConfig {
    //天远智能服务接口
    @Value("${tianyi.intesrv.url}")
    private String tianYiIntesrvUrl;

    public String getTianYiIntesrvUrl() {
        return tianYiIntesrvUrl;
    }

    public void setTianYiIntesrvUrl(String tianYiIntesrvUrl) {
        this.tianYiIntesrvUrl = tianYiIntesrvUrl;
    }
}
