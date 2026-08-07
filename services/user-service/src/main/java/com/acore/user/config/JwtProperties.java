package com.acore.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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

    /** 过期时间（毫秒），默认 24 小时 */
    private long expiration = 86400000L;
}