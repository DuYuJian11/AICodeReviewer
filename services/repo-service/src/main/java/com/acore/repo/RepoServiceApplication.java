package com.acore.repo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 仓库服务启动类
 *
 * @author acore
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.acore.repo", "com.acore.shared"})
public class RepoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RepoServiceApplication.class, args);
    }
}