package com.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication

@EnableTransactionManagement
public class XuechengContextApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(XuechengContextApiApplication.class, args);
    }

}
