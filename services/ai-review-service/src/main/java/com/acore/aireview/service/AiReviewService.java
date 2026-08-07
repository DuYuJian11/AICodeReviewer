package com.acore.aireview.service;

import com.acore.aireview.dto.ReviewCommentVO;
import com.acore.aireview.dto.ReviewRequest;
import com.acore.aireview.dto.ReviewResultResp;

import java.util.List;

/**
 * AI 审查服务接口
 *
 * @author acore
 */
public interface AiReviewService {

    /**
     * 执行 AI 代码审查
     */
    ReviewResultResp executeReview(ReviewRequest request);

    /**
     * 获取 PR 的所有审查评论
     */
    List<ReviewCommentVO> getReviewComments(Long pullRequestId);

    /**
     * 获取 PR 的审查摘要
     */
    ReviewResultResp getReviewSummary(Long pullRequestId);

    /**
     * 获取文件的审查评论
     */
    List<ReviewCommentVO> getFileReviewComments(Long pullRequestId, String filePath);
}