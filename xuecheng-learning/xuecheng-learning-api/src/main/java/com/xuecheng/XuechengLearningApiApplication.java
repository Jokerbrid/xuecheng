package com.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients

public class XuechengLearningApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(XuechengLearningApiApplication.class, args);
    }

}
