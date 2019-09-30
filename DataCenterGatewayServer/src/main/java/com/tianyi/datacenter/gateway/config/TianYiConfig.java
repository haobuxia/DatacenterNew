package com.tianyi.datacenter.gateway.config;

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

    @Value("${tianyi.leaseauthor.url}")
    private String leaseauthorUrl;

    @Value("${tianyi.autoertype}")
    private String autoertype;

    public String getAutoertype() {
        return autoertype;
    }

    public void setAutoertype(String autoertype) {
        this.autoertype = autoertype;
    }

    public String getLeaseauthorUrl() {
        return leaseauthorUrl;
    }

    public void setLeaseauthorUrl(String leaseauthorUrl) {
        this.leaseauthorUrl = leaseauthorUrl;
    }

    public String getTianYiIntesrvUrl() {
        return tianYiIntesrvUrl;
    }

    public void setTianYiIntesrvUrl(String tianYiIntesrvUrl) {
        this.tianYiIntesrvUrl = tianYiIntesrvUrl;
    }
}
