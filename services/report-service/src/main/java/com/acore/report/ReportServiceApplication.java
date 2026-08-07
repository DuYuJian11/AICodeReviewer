package com.acore.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 报告服务启动类
 *
 * @author acore
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.acore.report", "com.acore.shared"})
public class ReportServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportServiceApplication.class, args);
    }
}