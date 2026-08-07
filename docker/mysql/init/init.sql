-- ============================================================
-- AI Code Review Platform 数据库初始化脚本
-- 数据库: ai_code_review
-- ============================================================

CREATE DATABASE IF NOT EXISTS ai_code_review
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE ai_code_review;

-- ============================================================
-- 1. 用户表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码（BCrypt 加密）',
    email       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar      VARCHAR(500) DEFAULT NULL COMMENT '头像 URL',
    role        VARCHAR(20)  NOT NULL DEFAULT 'user' COMMENT '角色：admin / user',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 2. 仓库信息表
-- ============================================================
CREATE TABLE IF NOT EXISTS repository (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id        BIGINT       NOT NULL COMMENT '所属用户 ID',
    platform       VARCHAR(20)  NOT NULL COMMENT '平台：github / gitlab',
    repo_name      VARCHAR(200) NOT NULL COMMENT '仓库名称',
    repo_url       VARCHAR(500) NOT NULL COMMENT '仓库完整 URL',
    oauth_token    VARCHAR(500) DEFAULT NULL COMMENT 'OAuth 访问令牌',
    webhook_secret VARCHAR(64)  DEFAULT NULL COMMENT 'Webhook 密钥',
    is_active      TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用：0-禁用 1-启用',
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_platform (platform),
    UNIQUE KEY uk_repo_url_user (repo_url, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仓库信息表';

-- ============================================================
-- 3. PR/MR 信息表
-- ============================================================
CREATE TABLE IF NOT EXISTS pull_request (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    repository_id   BIGINT       NOT NULL COMMENT '所属仓库 ID',
    pr_number       INT          NOT NULL COMMENT 'PR/MR 编号',
    title           VARCHAR(500) DEFAULT NULL COMMENT '标题',
    description     TEXT         DEFAULT NULL COMMENT '描述',
    branch_from     VARCHAR(200) DEFAULT NULL COMMENT '源分支',
    branch_to       VARCHAR(200) DEFAULT NULL COMMENT '目标分支',
    commit_sha      VARCHAR(64)  DEFAULT NULL COMMENT '最新提交 SHA',
    author          VARCHAR(100) DEFAULT NULL COMMENT '作者',
    status          VARCHAR(20)  DEFAULT 'open' COMMENT '状态：open / closed / merged',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_repository_id (repository_id),
    INDEX idx_pr_number (pr_number),
    UNIQUE KEY uk_repo_pr (repository_id, pr_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='PR/MR 信息表';

-- ============================================================
-- 4. 静态扫描结果表
-- ============================================================
CREATE TABLE IF NOT EXISTS scan_result (
    id                  BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    pull_request_id     BIGINT       NOT NULL COMMENT '关联 PR ID',
    scanner_type        VARCHAR(50)  NOT NULL DEFAULT 'sonarqube' COMMENT '扫描器类型',
    bugs                INT          DEFAULT 0 COMMENT 'Bug 数量',
    vulnerabilities     INT          DEFAULT 0 COMMENT '漏洞数量',
    code_smells         INT          DEFAULT 0 COMMENT '代码异味数量',
    coverage            DECIMAL(5,2) DEFAULT NULL COMMENT '代码覆盖率（百分比）',
    quality_gate_passed TINYINT(1)   DEFAULT NULL COMMENT '质量门禁是否通过',
    report_url          VARCHAR(500) DEFAULT NULL COMMENT '扫描报告 URL',
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_pull_request_id (pull_request_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='静态扫描结果表';

-- ============================================================
-- 5. AI 审查评论表
-- ============================================================
CREATE TABLE IF NOT EXISTS ai_review_comment (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    pull_request_id BIGINT       NOT NULL COMMENT '关联 PR ID',
    file_path       VARCHAR(500) NOT NULL COMMENT '文件路径',
    line_start      INT          DEFAULT NULL COMMENT '起始行号',
    line_end        INT          DEFAULT NULL COMMENT '结束行号',
    severity        VARCHAR(20)  NOT NULL DEFAULT 'info' COMMENT '严重级别：critical / major / minor / info',
    category        VARCHAR(50)  DEFAULT NULL COMMENT '分类：bug / vulnerability / code_smell / style / performance',
    title           VARCHAR(500) NOT NULL COMMENT '评论标题',
    description     TEXT         DEFAULT NULL COMMENT '详细描述',
    suggestion      TEXT         DEFAULT NULL COMMENT '修改建议',
    model           VARCHAR(100) DEFAULT NULL COMMENT '使用的 AI 模型',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_pull_request_id (pull_request_id),
    INDEX idx_file_path (file_path),
    INDEX idx_severity (severity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 审查评论表';

-- ============================================================
-- 6. 审查任务队列表
-- ============================================================
CREATE TABLE IF NOT EXISTS review_task (
    id                BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    pull_request_id   BIGINT       NOT NULL COMMENT '关联 PR ID',
    task_type         VARCHAR(30)  NOT NULL COMMENT '任务类型：scan / ai_review / report / notification',
    status            VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT '状态：pending / running / completed / failed',
    retry_count       INT          NOT NULL DEFAULT 0 COMMENT '已重试次数',
    max_retries       INT          NOT NULL DEFAULT 3 COMMENT '最大重试次数',
    result            TEXT         DEFAULT NULL COMMENT '任务结果（JSON）',
    error_message     TEXT         DEFAULT NULL COMMENT '错误信息',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_pull_request_id (pull_request_id),
    INDEX idx_task_type (task_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审查任务队列表';

-- ============================================================
-- 7. 通知记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS notification_record (
    id                BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    pull_request_id   BIGINT       DEFAULT NULL COMMENT '关联 PR ID',
    notify_type       VARCHAR(20)  NOT NULL COMMENT '通知类型：mail / dingtalk / wechat',
    receiver          VARCHAR(500) NOT NULL COMMENT '接收人/群组',
    title             VARCHAR(500) NOT NULL COMMENT '标题',
    content           TEXT         NOT NULL COMMENT '内容',
    status            VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT '发送状态：pending / sent / failed',
    error_message     TEXT         DEFAULT NULL COMMENT '错误信息',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_pull_request_id (pull_request_id),
    INDEX idx_notify_type (notify_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知记录表';

-- ============================================================
-- 8. 审查报告表
-- ============================================================
CREATE TABLE IF NOT EXISTS review_report (
    id                  BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    pull_request_id     BIGINT       NOT NULL COMMENT '关联 PR ID',
    title               VARCHAR(500) NOT NULL COMMENT '报告标题',
    summary             TEXT         DEFAULT NULL COMMENT '报告摘要',
    scan_result_id      BIGINT       DEFAULT NULL COMMENT '扫描结果 ID',
    scan_summary        VARCHAR(500) DEFAULT NULL COMMENT '扫描结果摘要',
    ai_comment_count    INT          DEFAULT 0 COMMENT 'AI 评论总数',
    critical_count      INT          DEFAULT 0 COMMENT '严重问题数',
    major_count         INT          DEFAULT 0 COMMENT '主要问题数',
    minor_count         INT          DEFAULT 0 COMMENT '次要问题数',
    quality_gate_passed TINYINT(1)   DEFAULT NULL COMMENT '质量门禁是否通过',
    conclusion          VARCHAR(30)  DEFAULT NULL COMMENT '最终结论：passed / failed / review_needed',
    status              VARCHAR(20)  NOT NULL DEFAULT 'generating' COMMENT '报告状态：generating / completed / failed',
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_pull_request_id (pull_request_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审查报告表';

-- ============================================================
-- 初始数据：管理员账号
-- 密码: admin123（BCrypt 加密）
-- ============================================================
INSERT INTO sys_user (username, password, email, role, status) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@acore.com', 'admin', 1)
ON DUPLICATE KEY UPDATE username = username;