package com.acore.aireview.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 审查评论实体
 *
 * @author acore
 */
@Data
@TableName("ai_review_comment")
public class AiReviewCommentEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联 PR ID */
    private Long pullRequestId;

    /** 文件路径 */
    private String filePath;

    /** 起始行号 */
    private Integer lineStart;

    /** 结束行号 */
    private Integer lineEnd;

    /** 严重级别：critical / major / minor / info */
    private String severity;

    /** 分类：bug / vulnerability / code_smell / style / performance */
    private String category;

    /** 评论标题 */
    private String title;

    /** 详细描述 */
    private String description;

    /** 修改建议 */
    private String suggestion;

    /** 使用的 AI 模型 */
    private String model;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}