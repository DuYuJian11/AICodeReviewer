package com.acore.scanner.service;

import com.acore.scanner.dto.ScanRequest;
import com.acore.scanner.dto.ScanResultVO;

import java.util.List;

/**
 * 扫描服务接口
 *
 * @author acore
 */
public interface ScanService {

    /**
     * 发起代码扫描
     */
    ScanResultVO executeScan(ScanRequest request);

    /**
     * 获取 PR 的最新扫描结果
     */
    ScanResultVO getLatestScanResult(Long pullRequestId);

    /**
     * 获取 PR 的所有扫描结果
     */
    List<ScanResultVO> listScanResults(Long pullRequestId);

    /**
     * 检查质量门禁
     */
    boolean checkQualityGate(Long pullRequestId);
}