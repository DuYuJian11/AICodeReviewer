package com.acore.shared.exception;

import lombok.Getter;

/**
 * 错误码枚举
 *
 * @author acore
 */
@Getter
public enum ErrorCode {

    // ==================== 通用（1000-1999） ====================

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或令牌已过期"),
    FORBIDDEN(403, "没有权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),

    // ==================== 用户（2000-2999） ====================

    USER_NOT_FOUND(2000, "用户不存在"),
    USER_PASSWORD_ERROR(2001, "密码错误"),
    USER_DISABLED(2002, "账号已被禁用"),
    USER_EXISTS(2003, "用户已存在"),
    USERNAME_OR_PASSWORD_EMPTY(2004, "用户名或密码不能为空"),
    TOKEN_INVALID(2005, "令牌无效或已过期"),
    TOKEN_REFRESH_FAILED(2006, "令牌刷新失败"),

    // ==================== 仓库（3000-3999） ====================

    REPO_NOT_FOUND(3000, "仓库不存在"),
    REPO_OAUTH_FAILED(3001, "仓库 OAuth 授权失败"),
    REPO_WEBHOOK_FAILED(3002, "Webhook 注册失败"),
    PR_NOT_FOUND(3003, "PR/MR 不存在"),
    PLATFORM_UNSUPPORTED(3004, "不支持的仓库平台"),

    // ==================== 扫描（4000-4999） ====================

    SCAN_FAILED(4000, "代码扫描执行失败"),
    SCAN_NOT_FOUND(4001, "扫描结果不存在"),
    SONARQUBE_CONNECT_FAILED(4002, "SonarQube 连接失败"),

    // ==================== AI 审查（5000-5999） ====================

    AI_REVIEW_FAILED(5000, "AI 审查执行失败"),
    AI_API_CALL_FAILED(5001, "AI API 调用失败"),
    AI_API_TIMEOUT(5002, "AI API 调用超时"),
    AI_RESPONSE_PARSE_ERROR(5003, "AI 响应解析失败"),

    // ==================== 任务（6000-6999） ====================

    TASK_NOT_FOUND(6000, "任务不存在"),
    TASK_STATUS_INVALID(6001, "任务状态不合法"),
    TASK_RETRY_EXCEEDED(6002, "任务重试次数已超限"),

    // ==================== 通知（7000-7999） ====================

    NOTIFICATION_SEND_FAILED(7000, "通知发送失败"),
    MAIL_SEND_FAILED(7001, "邮件发送失败"),
    DINGTALK_SEND_FAILED(7002, "钉钉消息发送失败"),
    WECHAT_SEND_FAILED(7003, "企业微信消息发送失败"),

    ;

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}