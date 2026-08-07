package com.acore.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发送通知请求
 *
 * @author acore
 */
@Data
public class SendNotificationReq {

    /** 通知类型：mail / dingtalk / wechat */
    @NotBlank(message = "通知类型不能为空")
    private String notifyType;

    /** 接收人/群组 */
    @NotBlank(message = "接收人不能为空")
    private String receiver;

    /** 标题 */
    @NotBlank(message = "标题不能为空")
    private String title;

    /** 内容 */
    @NotBlank(message = "内容不能为空")
    private String content;

    /** 关联 PR ID */
    private Long pullRequestId;
}