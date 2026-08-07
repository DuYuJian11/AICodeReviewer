package com.acore.repo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 添加仓库请求
 *
 * @author acore
 */
@Data
public class RepoAddReq {

    @NotBlank(message = "仓库平台不能为空")
    private String platform;

    @NotBlank(message = "仓库名称不能为空")
    private String repoName;

    @NotBlank(message = "仓库 URL 不能为空")
    private String repoUrl;

    private String oauthToken;
}