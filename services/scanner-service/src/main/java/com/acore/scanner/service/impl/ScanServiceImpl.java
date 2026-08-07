package com.acore.scanner.service.impl;

import com.acore.shared.exception.BusinessException;
import com.acore.shared.exception.ErrorCode;
import com.acore.scanner.config.SonarQubeProperties;
import com.acore.scanner.dto.ScanRequest;
import com.acore.scanner.dto.ScanResultVO;
import com.acore.scanner.entity.ScanResultEntity;
import com.acore.scanner.repository.ScanResultMapper;
import com.acore.scanner.service.ScanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 扫描服务实现
 *
 * @author acore
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScanServiceImpl implements ScanService {

    private final ScanResultMapper scanResultMapper;
    private final SonarQubeProperties sonarQubeProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ScanResultVO executeScan(ScanRequest request) {
        log.info("发起代码扫描: pullRequestId={}, repoUrl={}, branch={}",
                request.getPullRequestId(), request.getRepoUrl(), request.getBranch());

        // 实际项目中会调用 SonarQube API 执行扫描
        // 此处模拟扫描结果
        ScanResultEntity entity = new ScanResultEntity();
        entity.setPullRequestId(request.getPullRequestId());
        entity.setScannerType("sonarqube");

        // 模拟扫描结果
        entity.setBugs((int) (Math.random() * 10));
        entity.setVulnerabilities((int) (Math.random() * 5));
        entity.setCodeSmells((int) (Math.random() * 50));
        entity.setCoverage(70.0 + Math.random() * 25);
        entity.setQualityGatePassed(entity.getBugs() < 5);
        entity.setReportUrl(String.format("%s/dashboard?id=com.acore:pr-%d",
                sonarQubeProperties.getHost(), request.getPullRequestId()));

        scanResultMapper.insert(entity);
        log.info("代码扫描完成: scanId={}, bugs={}, vulnerabilities={}, codeSmells={}, coverage={}",
                entity.getId(), entity.getBugs(), entity.getVulnerabilities(),
                entity.getCodeSmells(), entity.getCoverage());

        return ScanResultVO.fromEntity(entity);
    }

    @Override
    public ScanResultVO getLatestScanResult(Long pullRequestId) {
        return scanResultMapper.findLatestByPullRequestId(pullRequestId)
                .map(ScanResultVO::fromEntity)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCAN_NOT_FOUND));
    }

    @Override
    public List<ScanResultVO> listScanResults(Long pullRequestId) {
        return scanResultMapper.findByPullRequestId(pullRequestId).stream()
                .map(ScanResultVO::fromEntity)
                .toList();
    }

    @Override
    public boolean checkQualityGate(Long pullRequestId) {
        ScanResultVO result = getLatestScanResult(pullRequestId);
        return result.getQualityGatePassed() != null && result.getQualityGatePassed();
    }
}