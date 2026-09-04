package com.acore.scanner.controller;

import com.acore.scanner.dto.ScanRequest;
import com.acore.scanner.dto.ScanResultVO;
import com.acore.scanner.service.ScanService;
import com.acore.shared.dto.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 扫描控制器
 *
 * @author acore
 */
@Slf4j
@RestController
@RequestMapping("/scanner")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;

    /**
     * 发起代码扫描
     */
    @PostMapping("/scan")
    public Result<ScanResultVO> executeScan(@Valid @RequestBody ScanRequest request) {
        return Result.success(scanService.executeScan(request));
    }

    /**
     * 获取 PR 的最新扫描结果
     */
    @GetMapping("/latest/{pullRequestId}")
    public Result<ScanResultVO> getLatestScanResult(@PathVariable Long pullRequestId) {
        return Result.success(scanService.getLatestScanResult(pullRequestId));
    }

    /**
     * 获取 PR 的所有扫描结果
     */
    @GetMapping("/results/{pullRequestId}")
    public Result<List<ScanResultVO>> listScanResults(@PathVariable Long pullRequestId) {
        return Result.success(scanService.listScanResults(pullRequestId));
    }

    /**
     * 检查质量门禁
     */
    @GetMapping("/quality-gate/{pullRequestId}")
    public Result<Boolean> checkQualityGate(@PathVariable Long pullRequestId) {
        return Result.success(scanService.checkQualityGate(pullRequestId));
    }
}