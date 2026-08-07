package com.acore.shared.constant;

/**
 * 系统常量
 *
 * @author acore
 */
public interface AppConstants {

    /** 项目名称 */
    String PROJECT_NAME = "AI Code Review Platform";

    /** 项目版本 */
    String PROJECT_VERSION = "1.0.0";

    // ==================== 日期格式 ====================

    String DATE_FORMAT = "yyyy-MM-dd";
    String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    String TIME_ZONE = "Asia/Shanghai";

    // ==================== 分页默认值 ====================

    int DEFAULT_PAGE = 1;
    int DEFAULT_PAGE_SIZE = 20;
    int MAX_PAGE_SIZE = 200;

    // ==================== 任务状态 ====================

    String TASK_STATUS_PENDING = "pending";
    String TASK_STATUS_RUNNING = "running";
    String TASK_STATUS_COMPLETED = "completed";
    String TASK_STATUS_FAILED = "failed";

    // ==================== 审查任务类型 ====================

    String TASK_TYPE_SCAN = "scan";
    String TASK_TYPE_AI_REVIEW = "ai_review";
    String TASK_TYPE_REPORT = "report";
    String TASK_TYPE_NOTIFICATION = "notification";

    // ==================== 仓库平台 ====================

    String PLATFORM_GITHUB = "github";
    String PLATFORM_GITLAB = "gitlab";

    // ==================== 请求头 ====================

    String HEADER_USER_ID = "X-User-Id";
    String HEADER_USER_NAME = "X-User-Name";
    String HEADER_AUTHORIZATION = "Authorization";
    String TOKEN_PREFIX = "Bearer ";
}