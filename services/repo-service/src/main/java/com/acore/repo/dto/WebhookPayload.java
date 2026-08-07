package com.acore.repo.dto;

import lombok.Data;

/**
 * Webhook 请求体
 *
 * @author acore
 */
@Data
public class WebhookPayload {

    private Integer prNumber;
    private String action;
    private String commitSha;
}