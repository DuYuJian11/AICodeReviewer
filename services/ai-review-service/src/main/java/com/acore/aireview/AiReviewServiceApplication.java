package com.acore.aireview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * AI 审查服务启动类
 *
 * @author acore
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.acore.aireview", "com.acore.shared"})
public class AiReviewServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiReviewServiceApplication.class, args);
    }
}