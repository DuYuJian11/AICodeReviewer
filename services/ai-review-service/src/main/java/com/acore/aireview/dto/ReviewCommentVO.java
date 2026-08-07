package com.acore.aireview.dto;

import com.acore.aireview.entity.AiReviewCommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI 审查评论视图对象
 *
 * @author acore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCommentVO {

    private Long id;
    private Long pullRequestId;
    private String filePath;
    private Integer lineStart;
    private Integer lineEnd;
    private String severity;
    private String category;
    private String title;
    private String description;
    private String suggestion;
    private String model;
    private LocalDateTime createdAt;

    public static ReviewCommentVO fromEntity(AiReviewCommentEntity entity) {
        return ReviewCommentVO.builder()
                .id(entity.getId())
                .pullRequestId(entity.getPullRequestId())
                .filePath(entity.getFilePath())
                .lineStart(entity.getLineStart())
                .lineEnd(entity.getLineEnd())
                .severity(entity.getSeverity())
                .category(entity.getCategory())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .suggestion(entity.getSuggestion())
                .model(entity.getModel())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}