package com.acore.report.controller;

import com.acore.report.dto.ReportVO;
import com.acore.report.service.ReportService;
import com.acore.shared.dto.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报告控制器
 *
 * @author acore
 */
@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 生成报告
     */
    @PostMapping("/generate/{pullRequestId}")
    public Result<ReportVO> generateReport(@PathVariable Long pullRequestId,
                                            @RequestParam(defaultValue = "代码审查报告") String title) {
        return Result.success(reportService.generateReport(pullRequestId, title));
    }

    /**
     * 获取 PR 的最新报告
     */
    @GetMapping("/latest/{pullRequestId}")
    public Result<ReportVO> getLatestReport(@PathVariable Long pullRequestId) {
        return Result.success(reportService.getLatestReport(pullRequestId));
    }

    /**
     * 获取报告详情
     */
    @GetMapping("/{reportId}")
    public Result<ReportVO> getReportById(@PathVariable Long reportId) {
        return Result.success(reportService.getReportById(reportId));
    }

    /**
     * 获取报告列表
     */
    @GetMapping("/list")
    public Result<List<ReportVO>> listReports(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(reportService.listReports(userId));
    }
}