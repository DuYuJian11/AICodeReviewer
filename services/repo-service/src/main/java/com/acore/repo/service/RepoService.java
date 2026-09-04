package com.acore.repo.service;

import com.acore.repo.dto.OAuthCallbackReq;
import com.acore.repo.dto.PullRequestVO;
import com.acore.repo.dto.RepoAddReq;
import com.acore.repo.dto.RepoUpdateReq;
import com.acore.repo.dto.RepoVO;
import com.acore.repo.entity.PullRequestEntity;

import java.util.List;

/**
 * 仓库服务接口
 *
 * @author acore
 */
public interface RepoService {

    /**
     * 添加仓库
     */
    RepoVO addRepo(Long userId, RepoAddReq req);

    /**
     * 删除仓库
     */
    void deleteRepo(Long userId, Long repoId);

    /**
     * 获取用户仓库列表
     */
    List<RepoVO> listRepos(Long userId);

    /**
     * 获取仓库详情
     */
    RepoVO getRepo(Long userId, Long repoId);

    /**
     * 更新仓库
     */
    RepoVO updateRepo(Long userId, Long repoId, RepoUpdateReq req);

    /**
     * OAuth 回调处理
     */
    RepoVO handleOAuthCallback(Long userId, OAuthCallbackReq req);

    /**
     * 获取仓库的 PR 列表
     */
    List<PullRequestVO> listPullRequests(Long userId, Long repoId);

    /**
     * 同步 PR 信息（由 Webhook 触发）
     */
    PullRequestVO syncPullRequest(Long repoId, Integer prNumber);
}