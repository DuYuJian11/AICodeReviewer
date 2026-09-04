package com.acore.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API 网关 — 统一入口
 *
 * @author acore
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.acore.gateway"})
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}