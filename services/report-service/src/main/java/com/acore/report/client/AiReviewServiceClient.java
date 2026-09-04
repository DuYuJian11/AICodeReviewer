package com.acore.report.client;

import com.acore.report.dto.AiReviewSummaryResp;
import com.acore.shared.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * AI 审查服务 Feign 客户端
 *
 * <p>通过 Eureka 服务名调用 ai-review-service。</p>
 *
 * @author acore
 */
@FeignClient(name = "ai-review-service")
public interface AiReviewServiceClient {

    /**
     * 获取 PR 的审查摘要
     */
    @GetMapping("/ai-review/summary/{pullRequestId}")
    Result<AiReviewSummaryResp> getReviewSummary(@PathVariable("pullRequestId") Long pullRequestId);
}
