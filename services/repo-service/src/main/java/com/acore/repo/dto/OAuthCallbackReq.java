package com.acore.repo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * OAuth 回调请求
 *
 * @author acore
 */
@Data
public class OAuthCallbackReq {

    @NotBlank(message = "平台不能为空")
    private String platform;

    @NotBlank(message = "授权码不能为空")
    private String code;
}