package com.acore.report.service.impl;

import com.acore.shared.exception.BusinessException;
import com.acore.shared.exception.ErrorCode;
import com.acore.report.client.AiReviewServiceClient;
import com.acore.report.client.ScannerServiceClient;
import com.acore.report.config.ReviewWebSocketEndpoint;
import com.acore.report.dto.AiReviewSummaryResp;
import com.acore.report.dto.ReportVO;
import com.acore.report.dto.ScanResultResp;
import com.acore.report.entity.ReviewReport;
import com.acore.report.repository.ReportMapper;
import com.acore.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 报告服务实现
 *
 * @author acore
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportMapper reportMapper;
    private final ScannerServiceClient scannerServiceClient;
    private final AiReviewServiceClient aiReviewServiceClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReportVO generateReport(Long pullRequestId, String title) {
        log.info("生成审查报告: pullRequestId={}", pullRequestId);

        // 推送进度
        ReviewWebSocketEndpoint.pushProgress(pullRequestId, "report", 0, "开始生成报告...");

        // 聚合扫描结果
        ReviewWebSocketEndpoint.pushProgress(pullRequestId, "report", 30, "正在汇总扫描结果...");
        ScanResultResp scan = fetchScanResult(pullRequestId);

        // 聚合 AI 审查结果
        ReviewWebSocketEndpoint.pushProgress(pullRequestId, "report", 60, "正在聚合 AI 审查评论...");
        AiReviewSummaryResp aiSummary = fetchAiReviewSummary(pullRequestId);

        // 写入聚合结果
        ReviewReport report = new ReviewReport();
        report.setPullRequestId(pullRequestId);
        report.setTitle(title);
        report.setStatus("completed");

        if (scan != null) {
            report.setScanResultId(scan.getId());
            report.setScanSummary(String.format("SonarQube 扫描完成：Bug %d 个、漏洞 %d 个、代码异味 %d 个、覆盖率 %.1f%%",
                    nvl(scan.getBugs()), nvl(scan.getVulnerabilities()), nvl(scan.getCodeSmells()),
                    scan.getCoverage() == null ? 0 : scan.getCoverage()));
            report.setQualityGatePassed(scan.getQualityGatePassed());
        } else {
            report.setScanSummary("扫描结果获取失败，未生成扫描摘要");
        }

        if (aiSummary != null) {
            int total = Math.max(aiSummary.getTotalIssues(), 0);
            int critical = Math.max(aiSummary.getCriticalCount(), 0);
            int major = Math.max(aiSummary.getMajorCount(), 0);
            report.setAiCommentCount(total);
            report.setCriticalCount(critical);
            report.setMajorCount(major);
            report.setMinorCount(Math.max(total - critical - major, 0));
        } else {
            report.setAiCommentCount(0);
            report.setCriticalCount(0);
            report.setMajorCount(0);
            report.setMinorCount(0);
        }

        report.setSummary(buildSummary(report));
        report.setConclusion(resolveConclusion(report));

        reportMapper.insert(report);

        // 推送完成
        ReviewWebSocketEndpoint.pushCompleted(pullRequestId, report.getId());
        log.info("报告生成完成: reportId={}, conclusion={}", report.getId(), report.getConclusion());

        return ReportVO.fromEntity(report);
    }

    @Override
    public ReportVO getLatestReport(Long pullRequestId) {
        return reportMapper.findByPullRequestId(pullRequestId)
                .map(ReportVO::fromEntity)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "报告不存在"));
    }

    @Override
    public ReportVO getReportById(Long reportId) {
        ReviewReport report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "报告不存在");
        }
        return ReportVO.fromEntity(report);
    }

    @Override
    public List<ReportVO> listReports(Long userId) {
        return reportMapper.listByUserId(userId).stream()
                .map(ReportVO::fromEntity)
                .toList();
    }

    /**
     * 调用扫描服务获取最新扫描结果；服务不可用时降级返回 null
     */
    private ScanResultResp fetchScanResult(Long pullRequestId) {
        try {
            ScanResultResp scan = scannerServiceClient.getLatestScanResult(pullRequestId).getData();
            log.info("扫描服务聚合成功: pullRequestId={}", pullRequestId);
            return scan;
        } catch (Exception e) {
            log.warn("调用扫描服务失败，扫描结果以默认值生成: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 调用 AI 审查服务获取审查摘要；服务不可用时降级返回 null
     */
    private AiReviewSummaryResp fetchAiReviewSummary(Long pullRequestId) {
        try {
            AiReviewSummaryResp summary = aiReviewServiceClient.getReviewSummary(pullRequestId).getData();
            log.info("AI 审查服务聚合成功: pullRequestId={}", pullRequestId);
            return summary;
        } catch (Exception e) {
            log.warn("调用 AI 审查服务失败，审查数据以默认值生成: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 组装报告摘要
     */
    private String buildSummary(ReviewReport report) {
        if (report.getScanResultId() == null && nvl(report.getAiCommentCount()) == 0) {
            return String.format("PR #%d 代码审查报告（数据聚合失败）", report.getPullRequestId());
        }
        return String.format("PR #%d 代码审查报告：发现 %d 个问题（严重 %d、主要 %d、次要 %d）",
                report.getPullRequestId(),
                nvl(report.getAiCommentCount()),
                nvl(report.getCriticalCount()),
                nvl(report.getMajorCount()),
                nvl(report.getMinorCount()));
    }

    /**
     * 结论规则：扫描结果不可用 -> review_needed；质量门禁未通过 -> failed；存在严重问题 -> review_needed；否则 passed
     */
    private String resolveConclusion(ReviewReport report) {
        if (report.getScanResultId() == null) {
            return "review_needed";
        }
        if (Boolean.FALSE.equals(report.getQualityGatePassed())) {
            return "failed";
        }
        if (nvl(report.getCriticalCount()) > 0) {
            return "review_needed";
        }
        return "passed";
    }

    private int nvl(Integer value) {
        return value == null ? 0 : value;
    }
}
