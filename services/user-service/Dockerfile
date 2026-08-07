# ============================================================
# Stage 1: 构建
# ============================================================
# 构建方式（在项目根目录执行）:
#   docker build -f services/user-service/Dockerfile -t user-service .
# ============================================================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build
COPY pom.xml ./
COPY shared ./shared
COPY services/user-service ./services/user-service

RUN mvn clean package -pl services/user-service -am -DskipTests -q

# ============================================================
# Stage 2: 运行
# ============================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /build/services/user-service/target/*.jar app.jar

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
    CMD wget -qO- http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]