package com.acore.repo.controller;

import com.acore.repo.dto.OAuthCallbackReq;
import com.acore.repo.dto.PullRequestVO;
import com.acore.repo.dto.RepoAddReq;
import com.acore.repo.dto.RepoUpdateReq;
import com.acore.repo.dto.RepoVO;
import com.acore.repo.dto.WebhookPayload;
import com.acore.repo.service.RepoService;
import com.acore.shared.dto.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 仓库控制器
 *
 * @author acore
 */
@Slf4j
@RestController
@RequestMapping("/repo")
@RequiredArgsConstructor
public class RepoController {

    private final RepoService repoService;

    /**
     * 添加仓库
     */
    @PostMapping
    public Result<RepoVO> addRepo(@RequestHeader("X-User-Id") Long userId,
                                   @Valid @RequestBody RepoAddReq req) {
        return Result.success(repoService.addRepo(userId, req));
    }

    /**
     * 获取仓库列表
     */
    @GetMapping
    public Result<List<RepoVO>> listRepos(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(repoService.listRepos(userId));
    }

    /**
     * 获取仓库详情
     */
    @GetMapping("/{repoId}")
    public Result<RepoVO> getRepo(@RequestHeader("X-User-Id") Long userId,
                                   @PathVariable Long repoId) {
        return Result.success(repoService.getRepo(userId, repoId));
    }

    /**
     * 更新仓库
     */
    @PutMapping("/{repoId}")
    public Result<RepoVO> updateRepo(@RequestHeader("X-User-Id") Long userId,
                                      @PathVariable Long repoId,
                                      @Valid @RequestBody RepoUpdateReq req) {
        return Result.success(repoService.updateRepo(userId, repoId, req));
    }

    /**
     * 删除仓库
     */
    @DeleteMapping("/{repoId}")
    public Result<Void> deleteRepo(@RequestHeader("X-User-Id") Long userId,
                                    @PathVariable Long repoId) {
        repoService.deleteRepo(userId, repoId);
        return Result.success("删除成功", null);
    }

    /**
     * OAuth 回调
     */
    @PostMapping("/oauth/callback")
    public Result<RepoVO> oauthCallback(@RequestHeader("X-User-Id") Long userId,
                                         @Valid @RequestBody OAuthCallbackReq req) {
        return Result.success(repoService.handleOAuthCallback(userId, req));
    }

    /**
     * 获取仓库 PR 列表
     */
    @GetMapping("/{repoId}/pulls")
    public Result<List<PullRequestVO>> listPullRequests(@RequestHeader("X-User-Id") Long userId,
                                                         @PathVariable Long repoId) {
        return Result.success(repoService.listPullRequests(userId, repoId));
    }

    /**
     * Webhook：同步 PR 信息
     */
    @PostMapping("/{repoId}/webhook")
    public Result<PullRequestVO> webhook(@PathVariable Long repoId,
                                          @RequestBody WebhookPayload payload) {
        return Result.success(repoService.syncPullRequest(repoId, payload.getPrNumber()));
    }
}