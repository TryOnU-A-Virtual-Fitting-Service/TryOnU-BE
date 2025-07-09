#!/bin/bash

echo "🚀 가상피팅 API - 프로덕션 환경 시작"
echo "====================================="

echo "✅ 프로덕션 환경 정보"
echo "🔍 Health Check: http://localhost:8080/api/health"
echo "📊 Actuator: http://localhost:8080/actuator" 

# 환경 변수 설정
export SPRING_PROFILES_ACTIVE=prod

# PostgreSQL 데이터베이스 확인
echo "📊 PostgreSQL 데이터베이스 연결 확인 중..."
echo "   - Host: localhost:5432"
echo "   - Database: tryonu-prod"
echo "   - Username: postgres"
echo ""
echo "⚠️  PostgreSQL이 실행 중이고 tyronu-prod 데이터베이스가 생성되어 있는지 확인하세요.""
echo ""

# JAR 파일 빌드
echo "📦 JAR 파일 빌드 중..."
./gradlew clean build -x test

# 애플리케이션 실행
echo "🚀 애플리케이션 시작 중..."
java -jar build/libs/api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

