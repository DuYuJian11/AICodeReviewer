package com.acore.aireview.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI API 配置属性
 *
 * @author acore
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    /** 模型提供商：deepseek / openai */
    private String provider = "deepseek";

    /** API Key */
    private String apiKey;

    /** API 基础地址 */
    private String baseUrl;

    /** 模型名称 */
    private String model = "deepseek-chat";

    /** 最大 Token 数 */
    private int maxTokens = 4096;

    /** 请求超时（秒） */
    private int timeoutSeconds = 120;
}