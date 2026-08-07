package com.acore.report.service.impl;

import com.acore.shared.exception.BusinessException;
import com.acore.shared.exception.ErrorCode;
import com.acore.report.config.ReviewWebSocketEndpoint;
import com.acore.report.dto.ReportVO;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReportVO generateReport(Long pullRequestId, String title) {
        log.info("生成审查报告: pullRequestId={}", pullRequestId);

        // 推送进度
        ReviewWebSocketEndpoint.pushProgress(pullRequestId, "report", 0, "开始生成报告...");

        // 创建报告
        ReviewReport report = new ReviewReport();
        report.setPullRequestId(pullRequestId);
        report.setTitle(title);
        report.setStatus("generating");
        reportMapper.insert(report);

        // 模拟各阶段
        simulateStage(pullRequestId, "report", 30, "正在汇总扫描结果...");
        simulateStage(pullRequestId, "report", 60, "正在聚合 AI 审查评论...");
        simulateStage(pullRequestId, "report", 90, "正在生成最终结论...");

        // 最终报告数据（实际项目中会从 scanner-service 和 ai-review-service 聚合）
        report.setSummary(String.format("PR #%d 代码审查报告", pullRequestId));
        report.setScanSummary("SonarQube 扫描完成，未发现严重问题");
        report.setAiCommentCount(5);
        report.setCriticalCount(0);
        report.setMajorCount(2);
        report.setMinorCount(3);
        report.setQualityGatePassed(true);
        report.setConclusion("passed");
        report.setStatus("completed");

        reportMapper.updateById(report);

        // 推送完成
        ReviewWebSocketEndpoint.pushCompleted(pullRequestId, report.getId());
        log.info("报告生成完成: reportId={}", report.getId());

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

    private void simulateStage(Long pullRequestId, String stage, int progress, String message) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
        ReviewWebSocketEndpoint.pushProgress(pullRequestId, stage, progress, message);
    }
}