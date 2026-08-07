# ============================================================
# Stage 1: 构建
# ============================================================
# 构建方式（在项目根目录执行）:
#   docker build -f services/discovery-server/Dockerfile -t discovery-server .
# ============================================================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build
COPY pom.xml ./
COPY shared ./shared
COPY services/discovery-server ./services/discovery-server

# 构建 discovery-server 模块（连带 shared 依赖一起构建）
RUN mvn clean package -pl services/discovery-server -am -DskipTests -q

# ============================================================
# Stage 2: 运行
# ============================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /build/services/discovery-server/target/*.jar app.jar

EXPOSE 8761

# 健康检查
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
    CMD wget -qO- http://localhost:8761/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]