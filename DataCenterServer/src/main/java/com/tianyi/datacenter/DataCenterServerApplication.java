package com.tianyi.datacenter;

import com.tianyi.datacenter.common.aop.ControllerAspect;
import com.tianyi.datacenter.config.BusinessDBConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


/**
 * 数据中心服务启动类
 *
 * @author wenxinyan
 * @version 0.1
 */
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableEurekaClient
@Import(ControllerAspect.class)
@EnableTransactionManagement
@EnableScheduling
@MapperScan("com.tianyi.datacenter.resource.dao")
public class DataCenterServerApplication {
    @Autowired
    private BusinessDBConfig businessDBConfig;

    public static void main(String[] args) {
        SpringApplication.run(DataCenterServerApplication.class, args);
    }

    @Bean(name="businessDataSource")
    public DataSource businessDataSource(){
        //springboot默认数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(businessDBConfig.getDriver());
        dataSource.setPassword(businessDBConfig.getPassword());
        dataSource.setUsername(businessDBConfig.getUsername());
        dataSource.setJdbcUrl(businessDBConfig.getUrl());
        return dataSource;
    }

    @Bean(name = "businessJdbcTemplate")
    //@Qualifier指定注入的Bean
    public JdbcTemplate businessJdbcTemplate(@Qualifier("businessDataSource")DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
}
