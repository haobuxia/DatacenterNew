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

    @Value("${tianyi.tableau.url}")
    private String tianYiTableauUrl;

    @Value("${tianyi.tableau.username}")
    private String tianYiTableauUsername;

    @Value("${tianyi.tableau.target_site}")
    private String tianYiTableauTargetSite;

    @Value("${tianyi.tableau.client_ip}")
    private String tianYiTableauClientIp;

    public String getTianYiTableauClientIp() {
        return tianYiTableauClientIp;
    }

    public void setTianYiTableauClientIp(String tianYiTableauClientIp) {
        this.tianYiTableauClientIp = tianYiTableauClientIp;
    }

    public String getTianYiTableauUsername() {
        return tianYiTableauUsername;
    }

    public void setTianYiTableauUsername(String tianYiTableauUsername) {
        this.tianYiTableauUsername = tianYiTableauUsername;
    }

    public String getTianYiTableauTargetSite() {
        return tianYiTableauTargetSite;
    }

    public void setTianYiTableauTargetSite(String tianYiTableauTargetSite) {
        this.tianYiTableauTargetSite = tianYiTableauTargetSite;
    }

    public String getTianYiTableauUrl() {
        return tianYiTableauUrl;
    }

    public void setTianYiTableauUrl(String tianYiTableauUrl) {
        this.tianYiTableauUrl = tianYiTableauUrl;
    }

    public String getTianYiIntesrvUrl() {
        return tianYiIntesrvUrl;
    }

    public void setTianYiIntesrvUrl(String tianYiIntesrvUrl) {
        this.tianYiIntesrvUrl = tianYiIntesrvUrl;
    }
}
