package com.xuecheng.speedclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan("com.xuecheng.framework.domain.speed")//扫描实体类
@ComponentScan(basePackages={"com.xuecheng.api"})//扫描接口
@ComponentScan(basePackages={"com.xuecheng.speedclient"})
@ComponentScan(basePackages={"com.xuecheng.framework"})//扫描common下的所有类
public class SpeedSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpeedSpringApplication.class);
    }
}
