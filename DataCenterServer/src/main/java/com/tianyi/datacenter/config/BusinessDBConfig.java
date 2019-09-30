package com.tianyi.datacenter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 业务数据库配置对象
 *
 * @author zhouwei
 * 2018/11/28 09:11
 * @version 0.1
 **/
@Component
public class BusinessDBConfig {
    @Value("${spring.datasource.business.password}")
    private String password = "";
    @Value("${spring.datasource.business.url}")
    private String url = "";
    @Value("${spring.datasource.business.username}")
    private String username = "";
    @Value("${spring.datasource.business.driver}")
    private String driver = "";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
}
