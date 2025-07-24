# 멀티스테이지 빌드 - CI/CD 최적화
FROM gradle:8.11-jdk21 AS builder
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon
COPY src ./src
RUN gradle bootJar --no-daemon

# 실행 이미지
FROM amazoncorretto:21-alpine3.20
LABEL maintainer="ATTENTION PLEASE"
LABEL version="0.0.1-SNAPSHOT"

# 필수 패키지
RUN apk --no-cache add curl tzdata && \
    addgroup -g 1000 app && \
    adduser -D -s /bin/sh -u 1000 -G app app

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
RUN mkdir -p /app/logs && chown -R app:app /app

USER app
EXPOSE 8080

# 2GB RAM EC2 최적화 JVM 설정 - 힙 메모리 확대
ENV JAVA_OPTS="-Xms256m -Xmx768m -XX:+UseG1GC -XX:+UseContainerSupport \
-XX:MaxMetaspaceSize=256m -XX:CompressedClassSpaceSize=64m \
-XX:MaxDirectMemorySize=150m -XX:+PrintGCDetails -XX:+PrintGCTimeStamps \
-XX:+UseStringDeduplication -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs/"

# ThatzFit API 헬스체크 설정
HEALTHCHECK --interval=10s --timeout=15s --start-period=90s --retries=10 \
    CMD curl -s http://localhost:8080/api/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=prod -jar app.jar"] 