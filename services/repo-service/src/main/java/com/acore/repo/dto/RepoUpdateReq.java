package com.acore.repo.dto;

import lombok.Data;

/**
 * 仓库更新请求
 *
 * @author acore
 */
@Data
public class RepoUpdateReq {

    private String repoName;
    private String repoUrl;
    private String oauthToken;
    private Integer isActive;
}