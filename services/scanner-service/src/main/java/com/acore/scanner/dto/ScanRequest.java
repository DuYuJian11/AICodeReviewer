package com.acore.scanner.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发起扫描请求
 *
 * @author acore
 */
@Data
public class ScanRequest {

    @NotNull(message = "PR ID 不能为空")
    private Long pullRequestId;

    /** 仓库 URL */
    private String repoUrl;

    /** 分支名称 */
    private String branch;
}