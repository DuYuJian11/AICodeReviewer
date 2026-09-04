package com.acore.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 扫描服务返回的扫描结果（Feign 契约 DTO）
 *
 * <p>与 scanner-service 的 {@code ScanResultVO} 保持字段兼容。</p>
 *
 * @author acore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanResultResp {

    private Long id;
    private Long pullRequestId;
    private String scannerType;
    private Integer bugs;
    private Integer vulnerabilities;
    private Integer codeSmells;
    private Double coverage;
    private Boolean qualityGatePassed;
    private String reportUrl;
}
