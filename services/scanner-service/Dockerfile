# ============================================================
# Stage 1: 构建
# ============================================================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build
COPY pom.xml ./
COPY shared ./shared
COPY services/scanner-service ./services/scanner-service

RUN mvn clean package -pl services/scanner-service -am -DskipTests -q

# ============================================================
# Stage 2: 运行
# ============================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /build/services/scanner-service/target/*.jar app.jar

EXPOSE 8083

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
    CMD wget -qO- http://localhost:8083/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]