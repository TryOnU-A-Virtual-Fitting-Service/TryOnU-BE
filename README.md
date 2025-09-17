# TryOnU Backend API

가상 피팅 서비스를 위한 Spring Boot 백엔드 API입니다.

## 🚀 기술 스택

- **Java 21**
- **Spring Boot 3.4.7**
- **Spring Data JPA**
- **PostgreSQL**
- **AWS S3**
- **AWS Bedrock**

## 🛠️ 개발 환경 설정

### 필요 조건
- Java 21
- Gradle 8.14.2
- PostgreSQL

### 실행 방법
```bash
# 의존성 설치
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 테스트 실행
./gradlew test

# 테스트 커버리지 확인
./gradlew jacocoTestReport
```

## 📁 프로젝트 구조

```
src/
├── main/java/tryonu/api/
│   ├── controller/     # REST API 컨트롤러
│   ├── service/        # 비즈니스 로직 서비스
│   ├── repository/     # 데이터 접근 계층
│   ├── domain/         # 엔티티
│   ├── dto/           # 데이터 전송 객체
│   └── common/        # 공통 유틸리티
└── test/              # 테스트 코드
```

## 🔧 API 문서

애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs