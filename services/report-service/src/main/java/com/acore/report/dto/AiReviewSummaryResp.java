package com.acore.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 审查服务返回的审查摘要（Feign 契约 DTO）
 *
 * <p>与 ai-review-service 的 {@code ReviewResultResp} 保持字段兼容。</p>
 *
 * @author acore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiReviewSummaryResp {

    /** 审查摘要 */
    private String summary;

    /** 使用的 AI 模型 */
    private String model;

    /** 总问题数 */
    private int totalIssues;

    /** 严重问题数 */
    private int criticalCount;

    /** 主要问题数 */
    private int majorCount;
}
