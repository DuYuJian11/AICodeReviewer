package com.acore.scanner.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * SonarQube 配置属性
 *
 * @author acore
 */
@Data
@Component
@ConfigurationProperties(prefix = "sonarqube")
public class SonarQubeProperties {

    /** SonarQube 服务地址 */
    private String host = "http://localhost:9000";

    /** 管理员 Token */
    private String token;

    /** 扫描超时时间（分钟） */
    private int timeoutMinutes = 30;
}