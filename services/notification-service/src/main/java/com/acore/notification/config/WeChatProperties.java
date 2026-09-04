package com.acore.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 企业微信通知配置属性
 *
 * @author acore
 */
@Data
@Component
@ConfigurationProperties(prefix = "notification.wechat")
public class WeChatProperties {

    private boolean enabled = false;
    private String webhookUrl;
}
