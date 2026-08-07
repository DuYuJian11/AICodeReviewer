package com.acore.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审查报告实体
 *
 * @author acore
 */
@Data
@TableName("review_report")
public class ReviewReport {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联 PR ID */
    private Long pullRequestId;

    /** 报告标题 */
    private String title;

    /** 报告摘要 */
    private String summary;

    /** 扫描结果 ID */
    private Long scanResultId;

    /** 扫描结果摘要 */
    private String scanSummary;

    /** AI 评论总数 */
    private Integer aiCommentCount;

    /** 严重问题数 */
    private Integer criticalCount;

    /** 主要问题数 */
    private Integer majorCount;

    /** 次要问题数 */
    private Integer minorCount;

    /** 质量门禁是否通过 */
    private Boolean qualityGatePassed;

    /** 最终结论：passed / failed / review_needed */
    private String conclusion;

    /** 报告状态：generating / completed / failed */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}