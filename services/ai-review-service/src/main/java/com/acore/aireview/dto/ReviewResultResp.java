package com.acore.aireview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 审查结果响应
 *
 * @author acore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResultResp {

    /** 审查评论列表 */
    private List<ReviewCommentVO> comments;

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