package com.acore.repo.service;

import com.acore.shared.exception.BusinessException;
import com.acore.shared.exception.ErrorCode;
import com.acore.repo.dto.*;
import com.acore.repo.entity.RepositoryEntity;
import com.acore.repo.repository.PullRequestMapper;
import com.acore.repo.repository.RepositoryMapper;
import com.acore.repo.service.impl.RepoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 仓库服务单元测试
 *
 * @author acore
 */
@ExtendWith(MockitoExtension.class)
class RepoServiceTest {

    @Mock
    private RepositoryMapper repositoryMapper;

    @Mock
    private PullRequestMapper pullRequestMapper;

    @InjectMocks
    private RepoServiceImpl repoService;

    private RepositoryEntity mockRepo;

    @BeforeEach
    void setUp() {
        mockRepo = new RepositoryEntity();
        mockRepo.setId(1L);
        mockRepo.setUserId(1L);
        mockRepo.setPlatform("github");
        mockRepo.setRepoName("test-repo");
        mockRepo.setRepoUrl("https://github.com/test/test-repo");
        mockRepo.setIsActive(1);
    }

    @Nested
    @DisplayName("添加仓库")
    class AddRepo {

        @Test
        @DisplayName("添加成功")
        void shouldAddSuccessfully() {
            RepoAddReq req = new RepoAddReq();
            req.setPlatform("github");
            req.setRepoName("new-repo");
            req.setRepoUrl("https://github.com/test/new-repo");

            when(repositoryMapper.existsByRepoUrlAndUserId(anyString(), eq(1L))).thenReturn(false);
            when(repositoryMapper.insert(any(RepositoryEntity.class))).thenAnswer(invocation -> {
                RepositoryEntity entity = invocation.getArgument(0);
                entity.setId(2L);
                return 1;
            });

            RepoVO result = repoService.addRepo(1L, req);

            assertNotNull(result);
            assertEquals("new-repo", result.getRepoName());
            assertEquals("github", result.getPlatform());
            verify(repositoryMapper).insert(any(RepositoryEntity.class));
        }

        @Test
        @DisplayName("添加失败：平台不支持")
        void shouldFailWhenPlatformUnsupported() {
            RepoAddReq req = new RepoAddReq();
            req.setPlatform("bitbucket");
            req.setRepoName("test");
            req.setRepoUrl("https://bitbucket.org/test/test");

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> repoService.addRepo(1L, req));
            assertEquals(ErrorCode.PLATFORM_UNSUPPORTED.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("添加失败：仓库已存在")
        void shouldFailWhenRepoExists() {
            RepoAddReq req = new RepoAddReq();
            req.setPlatform("github");
            req.setRepoName("test-repo");
            req.setRepoUrl("https://github.com/test/test-repo");

            when(repositoryMapper.existsByRepoUrlAndUserId(anyString(), eq(1L))).thenReturn(true);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> repoService.addRepo(1L, req));
            assertEquals(400, ex.getCode());
        }
    }

    @Nested
    @DisplayName("查询仓库")
    class GetRepo {

        @Test
        @DisplayName("获取仓库列表")
        void shouldListRepos() {
            when(repositoryMapper.findByUserId(1L)).thenReturn(List.of(mockRepo));

            List<RepoVO> result = repoService.listRepos(1L);

            assertEquals(1, result.size());
            assertEquals("test-repo", result.get(0).getRepoName());
        }

        @Test
        @DisplayName("获取仓库详情成功")
        void shouldGetById() {
            when(repositoryMapper.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mockRepo));

            RepoVO result = repoService.getRepo(1L, 1L);

            assertNotNull(result);
            assertEquals("test-repo", result.getRepoName());
        }

        @Test
        @DisplayName("获取仓库详情失败：不存在")
        void shouldFailWhenNotFound() {
            when(repositoryMapper.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

            assertThrows(BusinessException.class, () -> repoService.getRepo(1L, 999L));
        }
    }

    @Nested
    @DisplayName("删除仓库")
    class DeleteRepo {

        @Test
        @DisplayName("删除成功")
        void shouldDeleteSuccessfully() {
            when(repositoryMapper.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mockRepo));

            repoService.deleteRepo(1L, 1L);

            verify(repositoryMapper).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("更新仓库")
    class UpdateRepo {

        @Test
        @DisplayName("更新成功")
        void shouldUpdateSuccessfully() {
            RepoUpdateReq req = new RepoUpdateReq();
            req.setRepoName("updated-repo");

            when(repositoryMapper.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mockRepo));

            RepoVO result = repoService.updateRepo(1L, 1L, req);

            assertEquals("updated-repo", result.getRepoName());
            verify(repositoryMapper).updateById(any(RepositoryEntity.class));
        }
    }
}