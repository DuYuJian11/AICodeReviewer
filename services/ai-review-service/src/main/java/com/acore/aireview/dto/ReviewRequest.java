package com.acore.aireview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发起 AI 审查请求
 *
 * @author acore
 */
@Data
public class ReviewRequest {

    @NotNull(message = "PR ID 不能为空")
    private Long pullRequestId;

    /** 代码仓库 URL */
    private String repoUrl;

    /** 源分支 */
    private String branchFrom;

    /** 目标分支 */
    private String branchTo;

    /** 提交 SHA */
    private String commitSha;

    /** 代码变更内容（diff 格式） */
    @NotBlank(message = "代码变更内容不能为空")
    private String diffContent;
}