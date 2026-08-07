package com.acore.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 邮件配置属性
 *
 * @author acore
 */
@Data
@Component
@ConfigurationProperties(prefix = "notification.mail")
public class MailProperties {

    private boolean enabled = false;
    private String host = "smtp.example.com";
    private int port = 587;
    private String username;
    private String password;
    private String from = "noreply@acore.com";
}

/**
 * 钉钉配置属性
 */
@Data
class DingTalkProperties {
    private boolean enabled = false;
    private String webhookUrl;
    private String secret;
}

/**
 * 企业微信配置属性
 */
@Data
class WeChatProperties {
    private boolean enabled = false;
    private String webhookUrl;
}