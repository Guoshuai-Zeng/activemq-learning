package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

/**
 * ActiveMQ SpringBoot 应用入口
 */
@SpringBootApplication
@EnableJms  // 启用 JMS 注解支持
public class ActiveMqSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActiveMqSpringBootApplication.class, args);
    }
}
