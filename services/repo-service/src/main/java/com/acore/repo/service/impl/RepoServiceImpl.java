package com.acore.repo.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.acore.shared.exception.BusinessException;
import com.acore.shared.exception.ErrorCode;
import com.acore.repo.dto.OAuthCallbackReq;
import com.acore.repo.dto.PullRequestVO;
import com.acore.repo.dto.RepoAddReq;
import com.acore.repo.dto.RepoUpdateReq;
import com.acore.repo.dto.RepoVO;
import com.acore.repo.entity.PullRequestEntity;
import com.acore.repo.entity.RepositoryEntity;
import com.acore.repo.repository.PullRequestMapper;
import com.acore.repo.repository.RepositoryMapper;
import com.acore.repo.service.RepoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 仓库服务实现
 *
 * @author acore
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepoServiceImpl implements RepoService {

    private final RepositoryMapper repositoryMapper;
    private final PullRequestMapper pullRequestMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RepoVO addRepo(Long userId, RepoAddReq req) {
        log.info("添加仓库: userId={}, platform={}, repoName={}", userId, req.getPlatform(), req.getRepoName());

        // 校验平台
        if (!"github".equals(req.getPlatform()) && !"gitlab".equals(req.getPlatform())) {
            throw new BusinessException(ErrorCode.PLATFORM_UNSUPPORTED);
        }

        // 检查是否已存在
        if (repositoryMapper.existsByRepoUrlAndUserId(req.getRepoUrl(), userId)) {
            throw new BusinessException(ErrorCode.REPO_EXISTS);
        }

        RepositoryEntity entity = new RepositoryEntity();
        entity.setUserId(userId);
        entity.setPlatform(req.getPlatform());
        entity.setRepoName(req.getRepoName());
        entity.setRepoUrl(req.getRepoUrl());
        entity.setOauthToken(req.getOauthToken());
        entity.setWebhookSecret(RandomUtil.randomString(32));
        entity.setIsActive(1);

        repositoryMapper.insert(entity);
        log.info("仓库添加成功: repoId={}", entity.getId());

        return RepoVO.fromEntity(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRepo(Long userId, Long repoId) {
        log.info("删除仓库: userId={}, repoId={}", userId, repoId);

        RepositoryEntity entity = repositoryMapper.findByIdAndUserId(repoId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPO_NOT_FOUND));

        repositoryMapper.deleteById(repoId);
        log.info("仓库删除成功: repoId={}", repoId);
    }

    @Override
    public List<RepoVO> listRepos(Long userId) {
        return repositoryMapper.findByUserId(userId).stream()
                .map(RepoVO::fromEntity)
                .toList();
    }

    @Override
    public RepoVO getRepo(Long userId, Long repoId) {
        RepositoryEntity entity = repositoryMapper.findByIdAndUserId(repoId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPO_NOT_FOUND));
        return RepoVO.fromEntity(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RepoVO updateRepo(Long userId, Long repoId, RepoUpdateReq req) {
        log.info("更新仓库: userId={}, repoId={}", userId, repoId);

        RepositoryEntity entity = repositoryMapper.findByIdAndUserId(repoId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPO_NOT_FOUND));

        if (req.getRepoName() != null) entity.setRepoName(req.getRepoName());
        if (req.getRepoUrl() != null) entity.setRepoUrl(req.getRepoUrl());
        if (req.getOauthToken() != null) entity.setOauthToken(req.getOauthToken());
        if (req.getIsActive() != null) entity.setIsActive(req.getIsActive());

        repositoryMapper.updateById(entity);
        log.info("仓库更新成功: repoId={}", repoId);

        return RepoVO.fromEntity(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RepoVO handleOAuthCallback(Long userId, OAuthCallbackReq req) {
        log.info("OAuth 回调: userId={}, platform={}", userId, req.getPlatform());
        // 实际项目中这里会调用 GitHub/GitLab API 换取 access_token
        // 此处简化实现，仅返回 mock 数据
        throw new BusinessException(ErrorCode.REPO_OAUTH_FAILED.getCode(), "OAuth 流程待实现");
    }

    @Override
    public List<PullRequestVO> listPullRequests(Long userId, Long repoId) {
        // 先验证仓库属于当前用户
        repositoryMapper.findByIdAndUserId(repoId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REPO_NOT_FOUND));

        return pullRequestMapper.findByRepositoryId(repoId).stream()
                .map(PullRequestVO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PullRequestVO syncPullRequest(Long repoId, Integer prNumber) {
        log.info("同步 PR: repoId={}, prNumber={}", repoId, prNumber);

        // 实际项目中会调用 GitHub/GitLab API 获取 PR 详情
        // 此处简化实现
        PullRequestEntity entity = pullRequestMapper.findByRepoIdAndPrNumber(repoId, prNumber)
                .orElseGet(() -> {
                    PullRequestEntity newEntity = new PullRequestEntity();
                    newEntity.setRepositoryId(repoId);
                    newEntity.setPrNumber(prNumber);
                    return newEntity;
                });

        // mock 数据
        entity.setTitle("PR #" + prNumber);
        entity.setBranchFrom("feature/" + prNumber);
        entity.setBranchTo("main");
        entity.setCommitSha("mock-sha-" + RandomUtil.randomString(8));
        entity.setStatus("open");

        if (entity.getId() == null) {
            pullRequestMapper.insert(entity);
        } else {
            pullRequestMapper.updateById(entity);
        }

        log.info("PR 同步成功: prId={}", entity.getId());
        return PullRequestVO.fromEntity(entity);
    }
}