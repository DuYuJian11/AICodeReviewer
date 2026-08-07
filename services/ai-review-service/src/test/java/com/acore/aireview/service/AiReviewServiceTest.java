package com.acore.aireview.service;

import com.acore.aireview.config.AiProperties;
import com.acore.aireview.dto.ReviewCommentVO;
import com.acore.aireview.dto.ReviewRequest;
import com.acore.aireview.dto.ReviewResultResp;
import com.acore.aireview.repository.AiReviewCommentMapper;
import com.acore.aireview.service.impl.AiReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AI 审查服务单元测试
 *
 * @author acore
 */
@ExtendWith(MockitoExtension.class)
class AiReviewServiceTest {

    @Mock
    private AiReviewCommentMapper commentMapper;

    @Mock
    private AiProperties aiProperties;

    @InjectMocks
    private AiReviewServiceImpl aiReviewService;

    @BeforeEach
    void setUp() {
        when(aiProperties.getModel()).thenReturn("deepseek-chat");
        when(aiProperties.getBaseUrl()).thenReturn("https://api.deepseek.com");
        when(aiProperties.getApiKey()).thenReturn("sk-test-key");
    }

    @Nested
    @DisplayName("执行 AI 审查")
    class ExecuteReview {

        @Test
        @DisplayName("审查成功：发现 TODO 问题")
        void shouldFindTodoIssue() {
            ReviewRequest request = new ReviewRequest();
            request.setPullRequestId(100L);
            request.setDiffContent("public void test() {\n  // TODO: implement\n  System.out.println(\"hello\");\n}");

            when(commentMapper.insert(any())).thenReturn(1);

            ReviewResultResp result = aiReviewService.executeReview(request);

            assertNotNull(result);
            assertTrue(result.getTotalIssues() > 0);
            verify(commentMapper, atLeastOnce()).insert(any());
        }

        @Test
        @DisplayName("审查成功：发现 System.out 问题")
        void shouldFindSystemOutIssue() {
            ReviewRequest request = new ReviewRequest();
            request.setPullRequestId(101L);
            request.setDiffContent("System.out.println(\"debug\");");

            when(commentMapper.insert(any())).thenReturn(1);

            ReviewResultResp result = aiReviewService.executeReview(request);

            assertNotNull(result);
            assertTrue(result.getTotalIssues() > 0);
        }

        @Test
        @DisplayName("审查成功：发现事务问题")
        void shouldFindTransactionalIssue() {
            ReviewRequest request = new ReviewRequest();
            request.setPullRequestId(102L);
            request.setDiffContent("@Transactional\npublic void save() {}");

            when(commentMapper.insert(any())).thenReturn(1);

            ReviewResultResp result = aiReviewService.executeReview(request);

            assertNotNull(result);
            assertTrue(result.getTotalIssues() > 0);
        }

        @Test
        @DisplayName("审查成功：无问题的代码")
        void shouldReturnEmptyForCleanCode() {
            ReviewRequest request = new ReviewRequest();
            request.setPullRequestId(103L);
            request.setDiffContent("public void test() {\n  int a = 1;\n  int b = 2;\n}");

            when(commentMapper.insert(any())).thenReturn(1);

            ReviewResultResp result = aiReviewService.executeReview(request);

            assertNotNull(result);
            assertEquals(0, result.getTotalIssues());
        }
    }

    @Nested
    @DisplayName("查询审查结果")
    class GetReviewResult {

        @Test
        @DisplayName("获取审查摘要")
        void shouldGetSummary() {
            ReviewCommentVO comment = ReviewCommentVO.builder()
                    .id(1L)
                    .pullRequestId(100L)
                    .severity("critical")
                    .category("bug")
                    .title("test bug")
                    .build();

            when(commentMapper.findByPullRequestId(100L)).thenReturn(List.of(
                    createCommentEntity(1L, 100L, "critical", "bug", "test bug")
            ));

            ReviewResultResp result = aiReviewService.getReviewSummary(100L);

            assertNotNull(result);
            assertEquals(1, result.getTotalIssues());
            assertEquals(1, result.getCriticalCount());
        }
    }

    private com.acore.aireview.entity.AiReviewCommentEntity createCommentEntity(
            Long id, Long pullRequestId, String severity, String category, String title) {
        var entity = new com.acore.aireview.entity.AiReviewCommentEntity();
        entity.setId(id);
        entity.setPullRequestId(pullRequestId);
        entity.setSeverity(severity);
        entity.setCategory(category);
        entity.setTitle(title);
        return entity;
    }
}