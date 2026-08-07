package com.acore.repo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 仓库信息表实体
 *
 * @author acore
 */
@Data
@TableName("repository")
public class RepositoryEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属用户 ID */
    private Long userId;

    /** 平台：github / gitlab */
    private String platform;

    /** 仓库名称 */
    private String repoName;

    /** 仓库完整 URL */
    private String repoUrl;

    /** OAuth 访问令牌 */
    private String oauthToken;

    /** Webhook 密钥 */
    private String webhookSecret;

    /** 是否启用：0-禁用 1-启用 */
    private Integer isActive;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}