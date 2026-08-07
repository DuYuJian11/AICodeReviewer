# ============================================================
# Stage 1: 构建
# ============================================================
# 构建方式（在项目根目录执行）:
#   docker build -f services/api-gateway/Dockerfile -t api-gateway .
# ============================================================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build
COPY pom.xml ./
COPY shared ./shared
COPY services/api-gateway ./services/api-gateway

RUN mvn clean package -pl services/api-gateway -am -DskipTests -q

# ============================================================
# Stage 2: 运行
# ============================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /build/services/api-gateway/target/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]