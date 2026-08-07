package com.acore.aireview.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.acore.shared.exception.BusinessException;
import com.acore.shared.exception.ErrorCode;
import com.acore.aireview.config.AiProperties;
import com.acore.aireview.dto.ReviewCommentVO;
import com.acore.aireview.dto.ReviewRequest;
import com.acore.aireview.dto.ReviewResultResp;
import com.acore.aireview.entity.AiReviewCommentEntity;
import com.acore.aireview.repository.AiReviewCommentMapper;
import com.acore.aireview.service.AiReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AI 审查服务实现
 *
 * @author acore
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiReviewServiceImpl implements AiReviewService {

    private final AiReviewCommentMapper commentMapper;
    private final AiProperties aiProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReviewResultResp executeReview(ReviewRequest request) {
        log.info("执行 AI 审查: pullRequestId={}, files={} chars",
                request.getPullRequestId(), request.getDiffContent().length());

        // 调用 AI API 获取审查结果
        List<ReviewCommentVO> comments = callAiApi(request);

        // 批量保存
        comments.forEach(comment -> {
            AiReviewCommentEntity entity = new AiReviewCommentEntity();
            entity.setPullRequestId(request.getPullRequestId());
            entity.setFilePath(comment.getFilePath());
            entity.setLineStart(comment.getLineStart());
            entity.setLineEnd(comment.getLineEnd());
            entity.setSeverity(comment.getSeverity());
            entity.setCategory(comment.getCategory());
            entity.setTitle(comment.getTitle());
            entity.setDescription(comment.getDescription());
            entity.setSuggestion(comment.getSuggestion());
            entity.setModel(aiProperties.getModel());
            commentMapper.insert(entity);
            comment.setId(entity.getId());
        });

        // 统计数据
        int total = comments.size();
        long critical = comments.stream().filter(c -> "critical".equals(c.getSeverity())).count();
        long major = comments.stream().filter(c -> "major".equals(c.getSeverity())).count();

        log.info("AI 审查完成: pullRequestId={}, totalComments={}, critical={}, major={}",
                request.getPullRequestId(), total, critical, major);

        return ReviewResultResp.builder()
                .comments(comments)
                .summary(String.format("AI 审查发现 %d 个问题，其中严重 %d 个，主要 %d 个", total, critical, major))
                .model(aiProperties.getModel())
                .totalIssues(total)
                .criticalCount((int) critical)
                .majorCount((int) major)
                .build();
    }

    @Override
    public List<ReviewCommentVO> getReviewComments(Long pullRequestId) {
        return commentMapper.findByPullRequestId(pullRequestId).stream()
                .map(ReviewCommentVO::fromEntity)
                .toList();
    }

    @Override
    public ReviewResultResp getReviewSummary(Long pullRequestId) {
        List<ReviewCommentVO> comments = getReviewComments(pullRequestId);
        int total = comments.size();
        long critical = comments.stream().filter(c -> "critical".equals(c.getSeverity())).count();
        long major = comments.stream().filter(c -> "major".equals(c.getSeverity())).count();

        return ReviewResultResp.builder()
                .comments(comments)
                .summary(String.format("AI 审查发现 %d 个问题，其中严重 %d 个，主要 %d 个", total, critical, major))
                .model(aiProperties.getModel())
                .totalIssues(total)
                .criticalCount((int) critical)
                .majorCount((int) major)
                .build();
    }

    @Override
    public List<ReviewCommentVO> getFileReviewComments(Long pullRequestId, String filePath) {
        return commentMapper.findByPullRequestIdAndFile(pullRequestId, filePath).stream()
                .map(ReviewCommentVO::fromEntity)
                .toList();
    }

    /**
     * 调用 AI API 进行代码审查
     *
     * <p>优先使用 DeepSeek API，可切换为 OpenAI API。</p>
     */
    private List<ReviewCommentVO> callAiApi(ReviewRequest request) {
        try {
            WebClient client = WebClient.builder()
                    .baseUrl(aiProperties.getBaseUrl())
                    .defaultHeader("Authorization", "Bearer " + aiProperties.getApiKey())
                    .build();

            String prompt = buildPrompt(request.getDiffContent());

            JSONObject requestBody = new JSONObject();
            requestBody.set("model", aiProperties.getModel());
            requestBody.set("max_tokens", aiProperties.getMaxTokens());

            JSONArray messages = new JSONArray();
            messages.add(new JSONObject()
                    .set("role", "system")
                    .set("content", "你是一位资深的代码审查专家。请严格审查以下代码变更，找出潜在问题。" +
                            "请以 JSON 数组格式返回结果，每个元素包含：filePath, lineStart, lineEnd, severity(critical/major/minor/info), " +
                            "category(bug/vulnerability/code_smell/style/performance), title, description, suggestion。"));
            messages.add(new JSONObject()
                    .set("role", "user")
                    .set("content", prompt));
            requestBody.set("messages", messages);

            // 实际项目中会调用真实 API
            // 此处模拟返回结果，避免真实 API 调用
            // String response = client.post()
            //         .uri("/chat/completions")
            //         .bodyValue(requestBody)
            //         .retrieve()
            //         .bodyToMono(String.class)
            //         .timeout(Duration.ofSeconds(aiProperties.getTimeoutSeconds()))
            //         .block();

            return parseMockResponse(request.getDiffContent());

        } catch (Exception e) {
            log.error("AI API 调用失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.AI_API_CALL_FAILED, e.getMessage());
        }
    }

    /**
     * 构建 Prompt
     */
    private String buildPrompt(String diffContent) {
        // 控制 Prompt 长度，避免 Token 超限
        String truncatedDiff = diffContent.length() > 30000
                ? diffContent.substring(0, 30000) + "\n... (超出部分已截断)"
                : diffContent;
        return "请审查以下代码变更：\n```diff\n" + truncatedDiff + "\n```";
    }

    /**
     * 模拟 AI 响应（用于开发测试）
     */
    private List<ReviewCommentVO> parseMockResponse(String diffContent) {
        List<ReviewCommentVO> comments = new ArrayList<>();
        // 模拟一些评论
        if (diffContent.contains("TODO")) {
            comments.add(ReviewCommentVO.builder()
                    .filePath("src/main/java/com/example/Service.java")
                    .lineStart(10)
                    .lineEnd(10)
                    .severity("info")
                    .category("code_smell")
                    .title("存在待办事项")
                    .description("代码中包含 TODO 注释，建议在合并前处理")
                    .suggestion("完成 TODO 项或创建 Issue 跟踪")
                    .model(aiProperties.getModel())
                    .build());
        }
        if (diffContent.contains("System.out")) {
            comments.add(ReviewCommentVO.builder()
                    .filePath("src/main/java/com/example/Controller.java")
                    .lineStart(5)
                    .lineEnd(5)
                    .severity("minor")
                    .category("code_smell")
                    .title("使用了 System.out 打印")
                    .description("生产代码中不应使用 System.out.println，应使用日志框架")
                    .suggestion("替换为 SLF4J 日志记录器")
                    .model(aiProperties.getModel())
                    .build());
        }
        if (diffContent.contains("@Transactional")) {
            comments.add(ReviewCommentVO.builder()
                    .filePath("src/main/java/com/example/Service.java")
                    .lineStart(15)
                    .lineEnd(15)
                    .severity("major")
                    .category("bug")
                    .title("事务注解缺少 rollbackFor")
                    .description("@Transactional 默认只在 RuntimeException 时回滚，建议明确指定 rollbackFor")
                    .suggestion("改为 @Transactional(rollbackFor = Exception.class)")
                    .model(aiProperties.getModel())
                    .build());
        }
        return comments;
    }
}