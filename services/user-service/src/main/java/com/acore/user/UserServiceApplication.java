package com.acore.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用户服务启动类
 *
 * @author acore
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.acore.user", "com.acore.shared"})
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}