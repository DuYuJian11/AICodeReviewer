package com.acore.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JWT 配置属性
 *
 * @author acore
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** 签名密钥 */
    private String secret;

    /** 白名单路径 — 不需要认证即可访问 */
    private List<String> whitelist;
}