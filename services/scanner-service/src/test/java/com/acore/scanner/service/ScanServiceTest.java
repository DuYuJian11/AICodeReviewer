package com.acore.scanner.service;

import com.acore.shared.exception.BusinessException;
import com.acore.shared.exception.ErrorCode;
import com.acore.scanner.config.SonarQubeProperties;
import com.acore.scanner.dto.ScanRequest;
import com.acore.scanner.dto.ScanResultVO;
import com.acore.scanner.entity.ScanResultEntity;
import com.acore.scanner.repository.ScanResultMapper;
import com.acore.scanner.service.impl.ScanServiceImpl;
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
 * 扫描服务单元测试
 *
 * @author acore
 */
@ExtendWith(MockitoExtension.class)
class ScanServiceTest {

    @Mock
    private ScanResultMapper scanResultMapper;

    @Mock
    private SonarQubeProperties sonarQubeProperties;

    @InjectMocks
    private ScanServiceImpl scanService;

    private ScanResultEntity mockResult;

    @BeforeEach
    void setUp() {
        mockResult = new ScanResultEntity();
        mockResult.setId(1L);
        mockResult.setPullRequestId(100L);
        mockResult.setScannerType("sonarqube");
        mockResult.setBugs(3);
        mockResult.setVulnerabilities(1);
        mockResult.setCodeSmells(20);
        mockResult.setCoverage(85.5);
        mockResult.setQualityGatePassed(true);
        mockResult.setReportUrl("http://localhost:9000/dashboard?id=com.acore:pr-100");
    }

    @Nested
    @DisplayName("执行扫描")
    class ExecuteScan {

        @Test
        @DisplayName("扫描成功")
        void shouldExecuteSuccessfully() {
            ScanRequest request = new ScanRequest();
            request.setPullRequestId(100L);
            request.setRepoUrl("https://github.com/test/test-repo");
            request.setBranch("main");

            when(sonarQubeProperties.getHost()).thenReturn("http://localhost:9000");
            when(scanResultMapper.insert(any(ScanResultEntity.class))).thenAnswer(invocation -> {
                ScanResultEntity entity = invocation.getArgument(0);
                entity.setId(1L);
                return 1;
            });

            ScanResultVO result = scanService.executeScan(request);

            assertNotNull(result);
            assertEquals(100L, result.getPullRequestId());
            assertEquals("sonarqube", result.getScannerType());
            assertNotNull(result.getBugs());
            assertNotNull(result.getCoverage());
            verify(scanResultMapper).insert(any(ScanResultEntity.class));
        }
    }

    @Nested
    @DisplayName("查询扫描结果")
    class GetScanResult {

        @Test
        @DisplayName("获取最新扫描结果成功")
        void shouldGetLatest() {
            when(scanResultMapper.findLatestByPullRequestId(100L)).thenReturn(Optional.of(mockResult));

            ScanResultVO result = scanService.getLatestScanResult(100L);

            assertNotNull(result);
            assertEquals(3, result.getBugs());
            assertTrue(result.getQualityGatePassed());
        }

        @Test
        @DisplayName("获取最新扫描结果失败：不存在")
        void shouldFailWhenNotFound() {
            when(scanResultMapper.findLatestByPullRequestId(999L)).thenReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> scanService.getLatestScanResult(999L));
            assertEquals(ErrorCode.SCAN_NOT_FOUND.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("质量门禁")
    class QualityGate {

        @Test
        @DisplayName("质量门禁通过")
        void shouldPassWhenBugsLow() {
            when(scanResultMapper.findLatestByPullRequestId(100L)).thenReturn(Optional.of(mockResult));

            boolean passed = scanService.checkQualityGate(100L);

            assertTrue(passed);
        }

        @Test
        @DisplayName("质量门禁未通过")
        void shouldFailWhenBugsHigh() {
            mockResult.setQualityGatePassed(false);
            when(scanResultMapper.findLatestByPullRequestId(100L)).thenReturn(Optional.of(mockResult));

            boolean passed = scanService.checkQualityGate(100L);

            assertFalse(passed);
        }
    }
}