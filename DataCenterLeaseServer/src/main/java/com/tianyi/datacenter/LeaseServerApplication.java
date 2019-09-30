package com.tianyi.datacenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

/**
 * 授权管理启动类
 *
 * @author tianxujin
 * @version 0.1
 */
@SpringBootApplication
@EnableEurekaClient
@EnableTransactionManagement
@EnableCaching

//@MapperScan("com.tianyi.datacenter.*.dao")
//@ComponentScan(basePackages = {"com.tianyi.datacenter.*.entity"})
public class LeaseServerApplication {
    @Autowired
    private RestTemplateBuilder builder;

    public static void main(String[] args) {
        SpringApplication.run(LeaseServerApplication.class, args);
    }
    // 使用RestTemplateBuilder来实例化RestTemplate对象，spring默认已经注入了RestTemplateBuilder实例
    @Bean
    public RestTemplate restTemplate() {
        return builder.build();
    }

}
