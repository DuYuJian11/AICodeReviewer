# CLAUDE.md — AI Code Review Platform（AI智能代码审查平台）

## 项目概述

**AI Code Review Platform** 是一个基于 Spring Cloud 的微服务架构智能代码审查平台，集成 SonarQube 静态扫描与 AI 大模型代码审查能力，支持 GitHub/GitLab 仓库对接、自动化审查流程与多渠道通知。

| 项目 | 说明 |
|------|------|
| **项目全称** | AI Code Review Platform（AI智能代码审查平台） |
| **架构风格** | Spring Cloud 微服务（Maven 多模块） |
| **构建工具** | Maven |
| **测试框架** | JUnit 5 + Mockito |

### 技术栈详细版本

| 依赖 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 3.2.8 |
| Spring Cloud | 2023.0.x |
| MySQL | 8.0.37 |
| Redis | 7.2 |
| RabbitMQ | 3.13.x |
| SonarQube | 10.5.x（Community Edition） |
| DeepSeek API | 最新版本（或 OpenAI API） |

---

## 微服务模块

所有服务位于 `services/` 目录下，每个服务拥有独立的 `pom.xml`。

### 服务列表

| 服务 | 目录 | 端口 | 职责 |
|------|------|------|------|
| **注册中心** | `services/discovery-server` | 8761 | Eureka 服务注册与发现中心 |
| **API 网关** | `services/api-gateway` | 8080 | Spring Cloud Gateway 统一路由、鉴权、限流 |
| **用户服务** | `services/user-service` | 8081 | 用户认证（JWT）、权限管理（RBAC） |
| **仓库服务** | `services/repo-service` | 8082 | GitHub/GitLab 仓库对接、OAuth 授权 |
| **扫描服务** | `services/scanner-service` | 8083 | SonarQube 静态代码扫描、结果存储 |
| **AI 审查服务** | `services/ai-review-service` | 8084 | AI 审查核心（调用大模型 API 生成评论） |
| **报告服务** | `services/report-service` | 8085 | 报告生成、WebSocket 实时推送 |
| **通知服务** | `services/notification-service` | 8086 | 邮件/钉钉/企业微信通知 |

### 服务职责说明

#### discovery-server（端口 8761）
- Eureka Server 注册中心
- 所有服务启动时向其注册，通过服务名进行远程调用
- 提供健康检查与心跳机制

#### api-gateway（端口 8080）
- Spring Cloud Gateway 统一入口
- 路由转发、负载均衡
- JWT 令牌校验与鉴权拦截
- 请求限流与日志记录

#### user-service（端口 8081）
- 用户注册、登录、令牌刷新（JWT）
- 基于 RBAC 的权限管理
- 用户信息 CRUD

#### repo-service（端口 8082）
- 对接 GitHub/GitLab 仓库
- OAuth 授权流程管理
- 仓库信息同步、Webhook 事件接收
- PR/MR 信息拉取与状态同步

#### scanner-service（端口 8083）
- 集成 SonarQube 执行静态代码扫描
- 扫描结果存储与查询
- 质量门禁（Quality Gate）判断

#### ai-review-service（端口 8084）
- AI 审查核心业务
- 调用 DeepSeek / OpenAI 大模型 API
- 生成代码审查评论
- 评论分类与聚合

#### report-service（端口 8085）
- 审查报告生成与聚合
- WebSocket 实时推送审查进度
- 报告历史查询与导出

#### notification-service（端口 8086）
- 邮件通知（SMTP）
- 钉钉机器人消息推送
- 企业微信消息推送

---

## 数据库设计

### 核心表清单（6 张表）

| 表名 | 说明 | 核心字段 |
|------|------|----------|
| `sys_user` | 用户表 | id, username, password, email, avatar, role, status, created_at, updated_at |
| `repository` | 仓库信息表 | id, user_id, platform(github/gitlab), repo_name, repo_url, oauth_token, webhook_secret, is_active, created_at |
| `pull_request` | PR/MR 信息表 | id, repository_id, pr_number, title, description, branch_from, branch_to, commit_sha, author, status, created_at |
| `scan_result` | 静态扫描结果表 | id, pull_request_id, scanner_type, bugs, vulnerabilities, code_smells, coverage, quality_gate_passed, report_url, created_at |
| `ai_review_comment` | AI 审查评论表 | id, pull_request_id, file_path, line_start, line_end, severity, category, title, description, suggestion, model, created_at |
| `review_task` | 审查任务队列表 | id, pull_request_id, task_type(scan/ai_review/report/notification), status(pending/running/completed/failed), retry_count, max_retries, result, error_message, created_at, updated_at |

---

## 项目结构

```
D:/MYproject/
├── CLAUDE.md                    # 本文件
├── pom.xml                      # 根 POM（聚合模块）
├── shared/                      # 公共模块
│   ├── pom.xml
│   └── src/main/java/com/acore/shared/
│       ├── dto/                  # 公共 DTO（统一响应体 Result<T> 等）
│       ├── exception/            # 公共异常定义
│       ├── util/                 # 工具类
│       ├── constant/             # 常量定义
│       └── config/               # 公共配置（Jackson、Feign 等）
├── services/
│   ├── discovery-server/         # 注册中心（8761）
│   │   ├── pom.xml
│   │   └── src/
│   ├── api-gateway/              # API 网关（8080）
│   │   ├── pom.xml
│   │   └── src/
│   ├── user-service/             # 用户服务（8081）
│   │   ├── pom.xml
│   │   └── src/
│   ├── repo-service/             # 仓库服务（8082）
│   │   ├── pom.xml
│   │   └── src/
│   ├── scanner-service/          # 扫描服务（8083）
│   │   ├── pom.xml
│   │   └── src/
│   ├── ai-review-service/        # AI 审查服务（8084）
│   │   ├── pom.xml
│   │   └── src/
│   ├── report-service/           # 报告服务（8085）
│   │   ├── pom.xml
│   │   └── src/
│   └── notification-service/     # 通知服务（8086）
│       ├── pom.xml
│       └── src/
└── .gitignore
```

### 服务内部通用结构

```
services/xxx-service/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/acore/<servicename>/
    │   │   ├── XxxServiceApplication.java
    │   │   ├── controller/       # REST 控制器
    │   │   ├── service/          # 业务逻辑接口 + impl
    │   │   ├── repository/       # MyBatis-Plus / JPA 数据访问
    │   │   ├── entity/           # 数据库实体
    │   │   ├── dto/              # 服务专用 DTO
    │   │   ├── config/           # 服务配置类
    │   │   └── handler/          # 全局异常处理器等
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       └── bootstrap.yml
    └── test/
        └── java/com/acore/<servicename>/
            ├── controller/
            ├── service/
            └── repository/
```

---

## 编码规范

### 包命名

包名统一使用 `com.acore.<服务名>`，例如：
- `com.acore.user` — 用户服务
- `com.acore.repo` — 仓库服务
- `com.acore.scanner` — 扫描服务
- `com.acore.aireview` — AI 审查服务
- `com.acore.report` — 报告服务
- `com.acore.notification` — 通知服务

### 分层规范

| 分层 | 命名 | 规范 |
|------|------|------|
| Controller | `*Controller.java` | 只做参数校验和路由转发，不写业务逻辑 |
| Service 接口 | `*Service.java` | 面向接口编程 |
| Service 实现 | `*ServiceImpl.java` | 业务逻辑层，必须记录 SLF4J 日志 |
| Repository | `*Mapper.java` / `*Repository.java` | 数据访问层 |
| Entity | `*Entity.java` | 数据库实体映射 |
| DTO | `*DTO.java` / `*Req.java` / `*Resp.java` | 数据传输对象 |

### 强制规则

- **所有 Controller 层必须返回统一响应体** `Result<T>`（定义在 shared 模块中）
- **所有 Service 层必须记录日志**（使用 SLF4J + Lombok `@Slf4j`）
- **所有数据库操作必须加事务注解**（`@Transactional(rollbackFor = Exception.class)`）
- 类名使用 PascalCase，方法/变量使用 camelCase，常量使用 UPPER_SNAKE_CASE
- 接口名不加 `I` 前缀，实现类加 `Impl` 后缀
- 禁止在 Controller 中写业务逻辑
- 遵循阿里巴巴 Java 开发手册（黄山版）

---

## 测试要求

- **所有服务必须包含完整的单元测试**，使用 JUnit 5 + Mockito
- 测试覆盖率目标：
  - 核心业务逻辑（Service 层）：> 80%
  - 整体项目：≥ 60%
- 测试文件命名：`*Test.java`（如 `UserServiceTest.java`）
- 每个服务启动前必须跑通 `mvn clean test`
- 测试需覆盖：正常流程、异常流程、边界条件

---

## Git 提交规范

提交信息用中文，前缀格式如下：

| 前缀 | 使用场景 |
|------|----------|
| `feat:` | 新功能 |
| `fix:` | Bug 修复 |
| `docs:` | 文档更新 |
| `refactor:` | 代码重构 |
| `test:` | 测试相关 |
| `chore:` | 构建/工具链/依赖更新 |

示例：
```
feat: 用户注册接口实现
fix: 修复扫描结果重复入库的问题
docs: 更新 API 接口文档
refactor: 抽取公共分页逻辑到 shared 模块
test: 补齐 AI 审查服务单元测试
chore: 升级 Spring Boot 至 3.2.8
```

---

## 外部依赖清单

### 基础设施（Docker Desktop）

| 服务 | 镜像 | 默认端口 | 用途 |
|------|------|----------|------|
| MySQL | mysql:8.0.37 | 3306 | 持久化存储 |
| Redis | redis:7.2 | 6379 | 缓存/会话管理 |
| RabbitMQ | rabbitmq:3.13-management | 5672(AMQP) / 15672(UI) | 消息队列/任务分发 |
| SonarQube | sonarqube:10.5-community | 9000 | 静态代码扫描 |

### 第三方服务

| 依赖 | 用途 | 获取方式 |
|------|------|----------|
| GitHub OAuth App | 仓库授权 | GitHub Settings > Developer settings > OAuth Apps |
| GitLab OAuth App | 仓库授权 | GitLab Settings > Applications |
| DeepSeek API Key | AI 代码审查 | platform.deepseek.com |
| OpenAI API Key | AI 代码审查（备选） | platform.openai.com |
| SonarQube Admin Token | SonarQube API 调用 | SonarQube Administration > Security > Users |

---

## 常用命令

```bash
# 构建全部模块（跳过测试）
mvn clean install -DskipTests

# 构建全部模块（含测试）
mvn clean install

# 构建并测试单个服务
mvn clean test -pl services/user-service -am

# 启动单个服务
mvn spring-boot:run -pl services/user-service

# 构建 shared 模块
mvn clean install -pl shared

# Docker 启动基础设施
docker compose up -d mysql redis rabbitmq sonarqube
```

---

## 环境要求

- JDK 17+
- Maven 3.8+
- Docker Desktop 4.x+
- MySQL 8.0+
- Redis 7.0+
- RabbitMQ 3.12+
- SonarQube 10.5+