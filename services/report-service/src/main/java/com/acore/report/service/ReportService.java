package com.acore.report.service;

import com.acore.report.dto.ReportVO;

import java.util.List;

/**
 * 报告服务接口
 *
 * @author acore
 */
public interface ReportService {

    /**
     * 生成审查报告
     */
    ReportVO generateReport(Long pullRequestId, String title);

    /**
     * 获取 PR 的最新报告
     */
    ReportVO getLatestReport(Long pullRequestId);

    /**
     * 获取报告详情
     */
    ReportVO getReportById(Long reportId);

    /**
     * 获取用户的所有报告
     */
    List<ReportVO> listReports(Long userId);
}