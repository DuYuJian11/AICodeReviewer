package com.acore.report.dto;

import com.acore.report.entity.ReviewReport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 报告视图对象
 *
 * @author acore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportVO {

    private Long id;
    private Long pullRequestId;
    private String title;
    private String summary;
    private String scanSummary;
    private Integer aiCommentCount;
    private Integer criticalCount;
    private Integer majorCount;
    private Integer minorCount;
    private Boolean qualityGatePassed;
    private String conclusion;
    private String status;
    private LocalDateTime createdAt;

    public static ReportVO fromEntity(ReviewReport entity) {
        return ReportVO.builder()
                .id(entity.getId())
                .pullRequestId(entity.getPullRequestId())
                .title(entity.getTitle())
                .summary(entity.getSummary())
                .scanSummary(entity.getScanSummary())
                .aiCommentCount(entity.getAiCommentCount())
                .criticalCount(entity.getCriticalCount())
                .majorCount(entity.getMajorCount())
                .minorCount(entity.getMinorCount())
                .qualityGatePassed(entity.getQualityGatePassed())
                .conclusion(entity.getConclusion())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}