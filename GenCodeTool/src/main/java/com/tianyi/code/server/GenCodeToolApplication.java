package com.tianyi.code.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.tianyi.code.server.*.dao")
@EnableTransactionManagement//启动注解事物管理
//@EnableEurekaClient
public class GenCodeToolApplication {
	public static void main(String[] args) {
		SpringApplication.run(GenCodeToolApplication.class, args);
	}
}
