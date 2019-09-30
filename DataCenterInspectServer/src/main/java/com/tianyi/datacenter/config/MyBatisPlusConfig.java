package com.tianyi.datacenter.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 使用这种方法,不用再启动类上写@Mapper 也不用配置pagehelper
 */
@Configuration
@MapperScan("com.tianyi.datacenter.inspect.dao")
public class MyBatisPlusConfig {
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
