package com.acore.scanner.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 扫描结果实体
 *
 * @author acore
 */
@Data
@TableName("scan_result")
public class ScanResultEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联 PR ID */
    private Long pullRequestId;

    /** 扫描器类型：sonarqube */
    private String scannerType;

    /** Bug 数量 */
    private Integer bugs;

    /** 漏洞数量 */
    private Integer vulnerabilities;

    /** 代码异味数量 */
    private Integer codeSmells;

    /** 代码覆盖率 */
    private Double coverage;

    /** 质量门禁是否通过 */
    private Boolean qualityGatePassed;

    /** 扫描报告 URL */
    private String reportUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}