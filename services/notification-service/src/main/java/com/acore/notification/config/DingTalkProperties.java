package com.acore.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 钉钉通知配置属性
 *
 * @author acore
 */
@Data
@Component
@ConfigurationProperties(prefix = "notification.dingtalk")
public class DingTalkProperties {

    private boolean enabled = false;
    private String webhookUrl;
    private String secret;
}
