package com.acore.aireview.controller;

import com.acore.aireview.dto.ReviewCommentVO;
import com.acore.aireview.dto.ReviewRequest;
import com.acore.aireview.dto.ReviewResultResp;
import com.acore.aireview.service.AiReviewService;
import com.acore.shared.dto.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 审查控制器
 *
 * @author acore
 */
@Slf4j
@RestController
@RequestMapping("/ai-review")
@RequiredArgsConstructor
public class AiReviewController {

    private final AiReviewService aiReviewService;

    /**
     * 执行 AI 代码审查
     */
    @PostMapping("/execute")
    public Result<ReviewResultResp> executeReview(@Valid @RequestBody ReviewRequest request) {
        return Result.success(aiReviewService.executeReview(request));
    }

    /**
     * 获取 PR 的审查摘要
     */
    @GetMapping("/summary/{pullRequestId}")
    public Result<ReviewResultResp> getReviewSummary(@PathVariable Long pullRequestId) {
        return Result.success(aiReviewService.getReviewSummary(pullRequestId));
    }

    /**
     * 获取 PR 的所有审查评论
     */
    @GetMapping("/comments/{pullRequestId}")
    public Result<List<ReviewCommentVO>> getReviewComments(@PathVariable Long pullRequestId) {
        return Result.success(aiReviewService.getReviewComments(pullRequestId));
    }

    /**
     * 获取文件的审查评论
     */
    @GetMapping("/comments/{pullRequestId}/file")
    public Result<List<ReviewCommentVO>> getFileReviewComments(
            @PathVariable Long pullRequestId,
            @RequestParam String filePath) {
        return Result.success(aiReviewService.getFileReviewComments(pullRequestId, filePath));
    }
}