package com.tianyi.datacenter.feign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 负载均衡启动类
 *
 * @author wenxinyan
 * @version 0.1
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class FeignServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignServerApplication.class, args);
    }

}
