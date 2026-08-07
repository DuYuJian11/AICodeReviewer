package com.acore.scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 扫描服务启动类
 *
 * @author acore
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.acore.scanner", "com.acore.shared"})
public class ScannerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScannerServiceApplication.class, args);
    }
}