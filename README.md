<div align="center">

# AI Code Review Platform

**AI智能代码审查平台**

基于 Spring Cloud 微服务架构，集成 SonarQube 静态扫描与 AI 大模型代码审查能力。

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0.x-brightgreen.svg)](https://spring.io/projects/spring-cloud)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

---

## 目录

- [项目简介](#项目简介)
- [系统架构](#系统架构)
- [技术栈](#技术栈)
- [微服务模块](#微服务模块)
- [快速开始](#快速开始)
- [API 文档](#api-文档)
- [项目结构](#项目结构)
- [外部依赖](#外部依赖)
- [开发指南](#开发指南)
- [许可证](#许可证)

---

## 项目简介

AI Code Review Platform 是一个智能代码审查平台，旨在帮助开发团队提升代码质量。平台核心能力：

- **仓库对接**：支持 GitHub / GitLab 仓库 OAuth 授权，自动同步 PR/MR 信息
- **静态扫描**：集成 SonarQube 执行代码质量扫描，检测 Bug、漏洞、代码异味
- **AI 审查**：调用 DeepSeek / OpenAI 大模型 API，对代码变更进行智能审查
- **报告聚合**：聚合扫描结果与 AI 审查评论，生成综合报告
- **多渠道通知**：支持邮件、钉钉、企业微信等通知渠道
- **实时推送**：WebSocket 实时推送审查进度

---

## 系统架构

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  客户端/CLI  │────▶│  API Gateway │────▶│  Eureka     │
│  (浏览器)    │     │  (端口8080)  │     │  (端口8761) │
└─────────────┘     └──────┬──────┘     └─────────────┘
                           │
           ┌───────────────┼───────────────┐
           ▼               ▼               ▼
    ┌──────────┐    ┌──────────┐    ┌──────────┐
    │ User     │    │ Repo    │    │ Scanner  │
    │ Service  │    │ Service │    │ Service  │
    │ :8081    │    │ :8082   │    │ :8083    │
    └──────────┘    └──────────┘    └──────────┘
           │               │               │
           ▼               ▼               ▼
    ┌──────────┐    ┌──────────┐    ┌──────────┐
    │ AI Review│    │ Report   │    │ Notify   │
    │ Service  │    │ Service  │    │ Service  │
    │ :8084    │    │ :8085    │    │ :8086    │
    └──────────┘    └──────────┘    └──────────┘
           │               │               │
           └───────────────┼───────────────┘
                           │
           ┌───────────────┼───────────────┐
           ▼               ▼               ▼
      ┌────────┐     ┌────────┐     ┌──────────┐
      │ MySQL  │     │ Redis  │     │ RabbitMQ │
      │ :3306  │     │ :6379  │     │ :5672    │
      └────────┘     └────────┘     └──────────┘
```

---

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 17 |
| 框架 | Spring Boot | 3.2.8 |
| 微服务 | Spring Cloud | 2023.0.x |
| 注册中心 | Eureka | 2023.0.x |
| 网关 | Spring Cloud Gateway | 2023.0.x |
| ORM | MyBatis-Plus | 3.5.7 |
| 数据库 | MySQL | 8.0.37 |
| 缓存 | Redis | 7.2 |
| 消息队列 | RabbitMQ | 3.13.x |
| 静态扫描 | SonarQube | 10.5.x (Community) |
| AI 模型 | DeepSeek API / OpenAI API | - |
| 构建工具 | Maven | 3.8+ |
| 测试 | JUnit 5 + Mockito | - |
| 容器化 | Docker | - |

---

## 微服务模块

| 服务 | 端口 | 说明 |
|------|------|------|
| **discovery-server** | 8761 | Eureka 服务注册与发现中心 |
| **api-gateway** | 8080 | Spring Cloud Gateway 统一路由、鉴权、限流 |
| **user-service** | 8081 | 用户认证（JWT）、权限管理（RBAC） |
| **repo-service** | 8082 | GitHub/GitLab 仓库对接、OAuth 授权 |
| **scanner-service** | 8083 | SonarQube 静态代码扫描、结果存储 |
| **ai-review-service** | 8084 | AI 审查核心（调用大模型 API 生成评论） |
| **report-service** | 8085 | 报告生成、WebSocket 实时推送 |
| **notification-service** | 8086 | 邮件/钉钉/企业微信通知 |

---

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- Docker Desktop 4.x+
- MySQL 8.0+ / Redis 7.0+ / RabbitMQ 3.12+

### 1. 启动中间件

```bash
# 启动 MySQL、Redis、RabbitMQ
docker compose up -d
```

### 2. 构建项目

```bash
# 构建全部模块（含测试）
mvn clean install

# 构建全部模块（跳过测试，速度更快）
mvn clean install -DskipTests
```

### 3. 启动服务

按顺序启动以下服务：

```bash
# 1. 注册中心
mvn spring-boot:run -pl services/discovery-server

# 2. API 网关
mvn spring-boot:run -pl services/api-gateway

# 3. 业务服务（可并行启动）
mvn spring-boot:run -pl services/user-service
mvn spring-boot:run -pl services/repo-service
mvn spring-boot:run -pl services/scanner-service
mvn spring-boot:run -pl services/ai-review-service
mvn spring-boot:run -pl services/report-service
mvn spring-boot:run -pl services/notification-service
```

### 4. 验证

- Eureka 控制台：http://localhost:8761
- API 网关：http://localhost:8080
- 各服务健康检查：http://localhost:{port}/actuator/health

---

## API 文档

### 用户服务 (`/api/user`)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/user/register` | 用户注册 | 否 |
| POST | `/api/user/login` | 用户登录 | 否 |
| POST | `/api/user/refresh` | 刷新令牌 | 否 |
| GET | `/api/user/me` | 获取当前用户信息 | 是 |
| GET | `/api/user/{id}` | 根据 ID 查询用户 | 是(管理员) |
| PUT | `/api/user/{id}` | 更新用户信息 | 是(管理员) |
| DELETE | `/api/user/{id}` | 删除用户 | 是(管理员) |

### 仓库服务 (`/api/repo`)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/repo` | 添加仓库 | 是 |
| GET | `/api/repo` | 获取仓库列表 | 是 |
| GET | `/api/repo/{repoId}` | 获取仓库详情 | 是 |
| PUT | `/api/repo/{repoId}` | 更新仓库 | 是 |
| DELETE | `/api/repo/{repoId}` | 删除仓库 | 是 |
| GET | `/api/repo/{repoId}/pulls` | 获取 PR 列表 | 是 |
| POST | `/api/repo/{repoId}/webhook` | Webhook 接收 | 否 |

### 扫描服务 (`/api/scanner`)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/scanner/scan` | 发起扫描 | 是 |
| GET | `/api/scanner/latest/{pullRequestId}` | 获取最新扫描结果 | 是 |
| GET | `/api/scanner/results/{pullRequestId}` | 获取所有扫描结果 | 是 |
| GET | `/api/scanner/quality-gate/{pullRequestId}` | 检查质量门禁 | 是 |

### AI 审查服务 (`/api/ai-review`)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/ai-review/execute` | 执行 AI 审查 | 是 |
| GET | `/api/ai-review/summary/{pullRequestId}` | 获取审查摘要 | 是 |
| GET | `/api/ai-review/comments/{pullRequestId}` | 获取审查评论 | 是 |
| GET | `/api/ai-review/comments/{pullRequestId}/file` | 获取文件评论 | 是 |

### 报告服务 (`/api/report`)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/report/generate/{pullRequestId}` | 生成报告 | 是 |
| GET | `/api/report/latest/{pullRequestId}` | 获取最新报告 | 是 |
| GET | `/api/report/{reportId}` | 获取报告详情 | 是 |
| GET | `/api/report/list` | 获取报告列表 | 是 |

### 通知服务 (`/api/notification`)

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/notification/send` | 发送通知 | 是 |
| POST | `/api/notification/send/mail` | 发送邮件 | 是 |
| POST | `/api/notification/send/dingtalk` | 发送钉钉通知 | 是 |
| POST | `/api/notification/send/wechat` | 发送企业微信通知 | 是 |
| GET | `/api/notification/list/{pullRequestId}` | 获取通知记录 | 是 |

### WebSocket 端点

```
ws://localhost:8080/ws/review/{pullRequestId}
```

---

## 项目结构

```
D:/MYproject/
├── CLAUDE.md                    # 项目开发指南
├── README.md                    # 本文件
├── pom.xml                      # 根 POM（聚合模块）
├── docker-compose.yml           # 中间件编排
├── docker/mysql/init/init.sql   # 数据库初始化脚本
├── .gitignore
├── shared/                      # 公共模块
│   └── src/main/java/com/acore/shared/
│       ├── dto/                 # 公共 DTO（Result<T>、PageResult 等）
│       ├── exception/           # 异常定义（ErrorCode、BusinessException）
│       ├── util/                # 工具类
│       └── constant/            # 常量
└── services/
    ├── discovery-server/        # 注册中心 (8761)
    ├── api-gateway/             # API 网关 (8080)
    ├── user-service/            # 用户服务 (8081)
    ├── repo-service/            # 仓库服务 (8082)
    ├── scanner-service/         # 扫描服务 (8083)
    ├── ai-review-service/       # AI 审查服务 (8084)
    ├── report-service/          # 报告服务 (8085)
    └── notification-service/    # 通知服务 (8086)
```

---

## 外部依赖

### 基础设施（Docker）

| 服务 | 镜像 | 默认端口 | 用途 |
|------|------|----------|------|
| MySQL | mysql:8.0.37 | 3306 | 持久化存储 |
| Redis | redis:7.2 | 6379 | 缓存/会话管理 |
| RabbitMQ | rabbitmq:3.13-management | 5672 / 15672 | 消息队列 |
| SonarQube | sonarqube:10.5-community | 9000 | 静态代码扫描 |

### 第三方服务

| 依赖 | 用途 | 获取方式 |
|------|------|----------|
| GitHub OAuth App | 仓库授权 | GitHub Settings > Developer settings |
| GitLab OAuth App | 仓库授权 | GitLab Settings > Applications |
| DeepSeek API Key | AI 代码审查 | platform.deepseek.com |
| SonarQube Admin Token | SonarQube API 调用 | SonarQube Administration > Security |

---

## 开发指南

### 编码规范

- 包名：`com.acore.<服务名>`
- 统一响应体：所有 Controller 返回 `Result<T>`
- 日志：所有 Service 层使用 `@Slf4j` 记录日志
- 事务：所有数据库操作加 `@Transactional(rollbackFor = Exception.class)`
- 遵循阿里巴巴 Java 开发手册（黄山版）

### 测试要求

- 使用 JUnit 5 + Mockito
- 核心业务逻辑覆盖率 > 80%
- 每个服务启动前必须跑通 `mvn clean test`

### 提交规范

```
feat: 新功能
fix: Bug 修复
docs: 文档更新
refactor: 代码重构
test: 测试相关
chore: 构建/工具链更新
```

### 常用命令

```bash
# 构建全部模块
mvn clean install -DskipTests

# 构建并测试单个服务
mvn clean test -pl services/user-service -am

# 启动单个服务
mvn spring-boot:run -pl services/user-service

# 构建 Docker 镜像
docker build -f services/user-service/Dockerfile -t user-service .

# Docker 启动中间件
docker compose up -d
```

---

## 许可证

[MIT License](LICENSE)

Copyright (c) 2024 Acore
