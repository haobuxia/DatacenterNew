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
public class TianYuanConfig {
    //天远智能服务接口
    @Value("${tianyuan.intesrv.url}")
    private String tianYuanIntesrvUrl;
    @Value("${tianyuan.intesrv.prod.url}")
    private String tianYuanIntesrvProdUrl;
    @Value("${tianyuan.intesrv.loginName}")
    private String tianYuanIntesrvLoginName;
    @Value("${tianyuan.intesrv.password}")
    private String tianYuanIntesrvPassword;
    @Value("${tianyuan.intesrv.jwt}")
    private String tianYuanIntesrvJwt;

    public String getTianYuanIntesrvUrl() {
        return tianYuanIntesrvUrl;
    }

    public void setTianYuanIntesrvUrl(String tianYuanIntesrvUrl) {
        this.tianYuanIntesrvUrl = tianYuanIntesrvUrl;
    }

    public String getTianYuanIntesrvProdUrl() {
        return tianYuanIntesrvProdUrl;
    }

    public void setTianYuanIntesrvProdUrl(String tianYuanIntesrvProdUrl) {
        this.tianYuanIntesrvProdUrl = tianYuanIntesrvProdUrl;
    }

    public String getTianYuanIntesrvLoginName() {
        return tianYuanIntesrvLoginName;
    }

    public void setTianYuanIntesrvLoginName(String tianYuanIntesrvLoginName) {
        this.tianYuanIntesrvLoginName = tianYuanIntesrvLoginName;
    }

    public String getTianYuanIntesrvPassword() {
        return tianYuanIntesrvPassword;
    }

    public void setTianYuanIntesrvPassword(String tianYuanIntesrvPassword) {
        this.tianYuanIntesrvPassword = tianYuanIntesrvPassword;
    }

    public String getTianYuanIntesrvJwt() {
        return tianYuanIntesrvJwt;
    }

    public void setTianYuanIntesrvJwt(String tianYuanIntesrvJwt) {
        this.tianYuanIntesrvJwt = tianYuanIntesrvJwt;
    }
}
