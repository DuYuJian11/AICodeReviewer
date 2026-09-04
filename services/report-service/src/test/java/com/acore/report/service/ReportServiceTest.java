package com.acore.report.service;

import com.acore.report.client.AiReviewServiceClient;
import com.acore.report.client.ScannerServiceClient;
import com.acore.report.dto.AiReviewSummaryResp;
import com.acore.report.dto.ReportVO;
import com.acore.report.dto.ScanResultResp;
import com.acore.report.entity.ReviewReport;
import com.acore.report.repository.ReportMapper;
import com.acore.report.service.impl.ReportServiceImpl;
import com.acore.shared.dto.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 报告服务单元测试
 *
 * @author acore
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportMapper reportMapper;

    @Mock
    private ScannerServiceClient scannerServiceClient;

    @Mock
    private AiReviewServiceClient aiReviewServiceClient;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Nested
    @DisplayName("生成报告")
    class GenerateReport {

        @Test
        @DisplayName("生成成功：聚合扫描与 AI 审查真实数据")
        void shouldGenerateSuccessfully() {
            ScanResultResp scan = ScanResultResp.builder()
                    .id(10L)
                    .bugs(2)
                    .vulnerabilities(1)
                    .codeSmells(8)
                    .coverage(85.0)
                    .qualityGatePassed(true)
                    .build();
            when(scannerServiceClient.getLatestScanResult(100L)).thenReturn(Result.success(scan));

            AiReviewSummaryResp aiSummary = AiReviewSummaryResp.builder()
                    .totalIssues(3)
                    .criticalCount(0)
                    .majorCount(1)
                    .build();
            when(aiReviewServiceClient.getReviewSummary(100L)).thenReturn(Result.success(aiSummary));

            when(reportMapper.insert(any(ReviewReport.class))).thenAnswer(invocation -> {
                ReviewReport report = invocation.getArgument(0);
                report.setId(1L);
                return 1;
            });

            ReportVO result = reportService.generateReport(100L, "测试报告");

            assertNotNull(result);
            assertEquals("passed", result.getConclusion());
            assertEquals("completed", result.getStatus());
            assertEquals(3, result.getAiCommentCount());
            assertEquals(0, result.getCriticalCount());
            assertEquals(1, result.getMajorCount());
            assertEquals(2, result.getMinorCount());
            assertEquals(Boolean.TRUE, result.getQualityGatePassed());
            verify(scannerServiceClient).getLatestScanResult(100L);
            verify(aiReviewServiceClient).getReviewSummary(100L);
        }

        @Test
        @DisplayName("生成成功：扫描服务不可用时降级，结论为 review_needed")
        void shouldDegradeWhenScanServiceDown() {
            when(scannerServiceClient.getLatestScanResult(100L))
                    .thenThrow(new RuntimeException("scanner-service 连接超时"));

            AiReviewSummaryResp aiSummary = AiReviewSummaryResp.builder()
                    .totalIssues(2)
                    .criticalCount(0)
                    .majorCount(0)
                    .build();
            when(aiReviewServiceClient.getReviewSummary(100L)).thenReturn(Result.success(aiSummary));

            when(reportMapper.insert(any(ReviewReport.class))).thenAnswer(invocation -> {
                ReviewReport report = invocation.getArgument(0);
                report.setId(1L);
                return 1;
            });

            ReportVO result = reportService.generateReport(100L, "测试报告");

            assertNotNull(result);
            assertEquals("review_needed", result.getConclusion());
            assertEquals("completed", result.getStatus());
            assertEquals(2, result.getAiCommentCount());
        }

        @Test
        @DisplayName("生成成功：质量门禁未通过时结论为 failed")
        void shouldFailWhenQualityGateNotPassed() {
            ScanResultResp scan = ScanResultResp.builder()
                    .id(11L)
                    .bugs(9)
                    .vulnerabilities(2)
                    .codeSmells(30)
                    .coverage(60.0)
                    .qualityGatePassed(false)
                    .build();
            when(scannerServiceClient.getLatestScanResult(100L)).thenReturn(Result.success(scan));

            AiReviewSummaryResp aiSummary = AiReviewSummaryResp.builder()
                    .totalIssues(1)
                    .criticalCount(0)
                    .majorCount(1)
                    .build();
            when(aiReviewServiceClient.getReviewSummary(100L)).thenReturn(Result.success(aiSummary));

            when(reportMapper.insert(any(ReviewReport.class))).thenAnswer(invocation -> {
                ReviewReport report = invocation.getArgument(0);
                report.setId(1L);
                return 1;
            });

            ReportVO result = reportService.generateReport(100L, "测试报告");

            assertNotNull(result);
            assertEquals("failed", result.getConclusion());
            assertEquals(Boolean.FALSE, result.getQualityGatePassed());
        }
    }

    @Nested
    @DisplayName("查询报告")
    class GetReport {

        @Test
        @DisplayName("获取最新报告成功")
        void shouldGetLatest() {
            ReviewReport report = new ReviewReport();
            report.setId(1L);
            report.setPullRequestId(100L);
            report.setStatus("completed");
            report.setConclusion("passed");

            when(reportMapper.findByPullRequestId(100L)).thenReturn(Optional.of(report));

            ReportVO result = reportService.getLatestReport(100L);

            assertNotNull(result);
            assertEquals("completed", result.getStatus());
        }

        @Test
        @DisplayName("获取报告详情成功")
        void shouldGetById() {
            ReviewReport report = new ReviewReport();
            report.setId(1L);
            report.setPullRequestId(100L);

            when(reportMapper.selectById(1L)).thenReturn(report);

            ReportVO result = reportService.getReportById(1L);

            assertNotNull(result);
            assertEquals(100L, result.getPullRequestId());
        }

        @Test
        @DisplayName("获取报告详情失败：不存在")
        void shouldFailWhenNotFound() {
            when(reportMapper.selectById(999L)).thenReturn(null);

            assertThrows(com.acore.shared.exception.BusinessException.class,
                    () -> reportService.getReportById(999L));
        }
    }
}
