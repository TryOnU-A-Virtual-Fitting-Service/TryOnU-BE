#!/bin/bash

echo "🧪 TryonU Backend API - 테스트 환경 시작"
echo "=================================="

# 환경 변수 설정
export SPRING_PROFILES_ACTIVE=test

# 테스트 실행
./gradlew test --tests "*Test" --info

echo "✅ 테스트 환경 실행 완료" 