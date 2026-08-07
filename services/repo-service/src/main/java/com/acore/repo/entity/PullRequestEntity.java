package com.acore.repo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * PR/MR 信息表实体
 *
 * @author acore
 */
@Data
@TableName("pull_request")
public class PullRequestEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属仓库 ID */
    private Long repositoryId;

    /** PR/MR 编号 */
    private Integer prNumber;

    /** 标题 */
    private String title;

    /** 描述 */
    private String description;

    /** 源分支 */
    private String branchFrom;

    /** 目标分支 */
    private String branchTo;

    /** 最新提交 SHA */
    private String commitSha;

    /** 作者 */
    private String author;

    /** 状态：open / closed / merged */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}