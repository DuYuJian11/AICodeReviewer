package com.acore.report.client;

import com.acore.report.dto.ScanResultResp;
import com.acore.shared.dto.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 扫描服务 Feign 客户端
 *
 * <p>通过 Eureka 服务名调用 scanner-service。</p>
 *
 * @author acore
 */
@FeignClient(name = "scanner-service")
public interface ScannerServiceClient {

    /**
     * 获取 PR 的最新扫描结果
     */
    @GetMapping("/scanner/latest/{pullRequestId}")
    Result<ScanResultResp> getLatestScanResult(@PathVariable("pullRequestId") Long pullRequestId);
}
