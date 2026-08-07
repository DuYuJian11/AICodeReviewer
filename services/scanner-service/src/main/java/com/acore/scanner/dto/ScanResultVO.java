package com.acore.scanner.dto;

import com.acore.scanner.entity.ScanResultEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 扫描结果视图对象
 *
 * @author acore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanResultVO {

    private Long id;
    private Long pullRequestId;
    private String scannerType;
    private Integer bugs;
    private Integer vulnerabilities;
    private Integer codeSmells;
    private Double coverage;
    private Boolean qualityGatePassed;
    private String reportUrl;
    private LocalDateTime createdAt;

    public static ScanResultVO fromEntity(ScanResultEntity entity) {
        return ScanResultVO.builder()
                .id(entity.getId())
                .pullRequestId(entity.getPullRequestId())
                .scannerType(entity.getScannerType())
                .bugs(entity.getBugs())
                .vulnerabilities(entity.getVulnerabilities())
                .codeSmells(entity.getCodeSmells())
                .coverage(entity.getCoverage())
                .qualityGatePassed(entity.getQualityGatePassed())
                .reportUrl(entity.getReportUrl())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}