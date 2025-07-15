#!/bin/bash

echo "🚀 TryonU Backend API - 로컬 환경 시작"
echo "=================================="
echo ""
echo "✅ 로컬 환경 실행 완료"
echo "📊 Swagger UI: http://localhost:8080/swagger-ui.html"
echo "🔍 Health Check: http://localhost:8080/api/health"
echo ""
echo "📊 PostgreSQL 데이터베이스: tryonu-dev" 

# 환경 변수 설정
export SPRING_PROFILES_ACTIVE=local
export GRADLE_OPTS="-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8"


# 애플리케이션 실행
./gradlew bootRun --args="--spring.profiles.active=local"

