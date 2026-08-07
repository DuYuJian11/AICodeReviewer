package com.acore.report.service;

import com.acore.report.dto.ReportVO;
import com.acore.report.entity.ReviewReport;
import com.acore.report.repository.ReportMapper;
import com.acore.report.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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

    @InjectMocks
    private ReportServiceImpl reportService;

    @Nested
    @DisplayName("生成报告")
    class GenerateReport {

        @Test
        @DisplayName("生成成功")
        void shouldGenerateSuccessfully() {
            when(reportMapper.insert(any(ReviewReport.class))).thenAnswer(invocation -> {
                ReviewReport report = invocation.getArgument(0);
                report.setId(1L);
                return 1;
            });
            when(reportMapper.updateById(any())).thenReturn(1);

            ReportVO result = reportService.generateReport(100L, "测试报告");

            assertNotNull(result);
            assertEquals("passed", result.getConclusion());
            assertEquals("completed", result.getStatus());
            verify(reportMapper).insert(any(ReviewReport.class));
            verify(reportMapper).updateById(any());
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