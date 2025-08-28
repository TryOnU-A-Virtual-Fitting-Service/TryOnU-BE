# TryOnU API 에러 가이드

## 1. 개요

TryOnU API는 사용자가 의류 이미지를 업로드하여 가상으로 피팅해볼 수 있는 기능을 제공합니다. 본 문서는 프론트엔드 개발자가 API 에러를 효율적으로 식별하고 처리할 수 있도록 인증 방식, 에러 코드, 응답 포맷 등을 상세히 설명합니다.

## 2. API 응답 포맷

모든 API는 일관된 응답 포맷을 사용합니다.

### 성공 응답

```json
{
  "isSuccess": true,
  "data": {
    // 실제 응답 데이터
  }
}
```

### 실패 응답

```json
{
  "isSuccess": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "에러 메시지",
    "validationErrors": {
      // validation 실패 시에만 포함 (선택)
      "fieldName": "필드별 에러 메시지"
    }
  }
}
```

## 3. 인증 방식

### 헤더 요구사항
- **X-UUID**: 사용자 식별을 위한 UUID (필수)

```http
X-UUID: 550e8400-e29b-41d4-a716-446655440000
```

## 4. 공통 HTTP 상태 코드

| 상태 코드 | 설명 | 발생 상황 |
|-----------|------|-----------|
| **200** | OK | 요청 성공 |
| **201** | Created | 리소스 생성 성공 |
| **400** | Bad Request | 잘못된 요청 파라미터, validation 실패 |
| **401** | Unauthorized | `X-UUID` 헤더 누락 또는 유효하지 않은 사용자 |
| **403** | Forbidden | 접근 권한 없음 |
| **404** | Not Found | 요청한 리소스를 찾을 수 없음 |
| **405** | Method Not Allowed | 지원하지 않는 HTTP 메서드 |
| **408** | Request Timeout | 요청 처리 시간 초과 |
| **413** | Payload Too Large | 파일 크기 초과 (10MB 이상) |
| **415** | Unsupported Media Type | 지원하지 않는 Content-Type |
| **500** | Internal Server Error | 서버 내부 오류 |
| **502** | Bad Gateway | 외부 API 호출 실패 |
| **503** | Service Unavailable | 서버 리소스 부족 |

## 5. 에러 코드 상세 가이드

### 5.1 사용자 관련 에러 (U001-U004)

#### 🔍 **U001 - 사용자를 찾을 수 없습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "U001",
    "message": "사용자를 찾을 수 없습니다."
  }
}
```
- **HTTP 상태**: 404 Not Found
- **발생 원인**: 
  - 잘못된 X-UUID 헤더 값
  - 초기화되지 않은 사용자
- **해결 방법**: 
  - X-UUID 헤더 값 확인
  - `/user/init` API로 사용자 초기화

#### ⚠️ **U002 - 이미 존재하는 사용자입니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "U002",
    "message": "이미 존재하는 사용자입니다."
  }
}
```
- **HTTP 상태**: 409 Conflict
- **발생 원인**: 중복된 사용자 생성 시도
- **해결 방법**: 기존 사용자 정보 사용

#### 📱 **U003 - 디바이스 ID는 필수입니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "U003",
    "message": "디바이스 ID는 필수입니다."
  }
}
```
- **HTTP 상태**: 400 Bad Request
- **발생 원인**: X-UUID 헤더 누락
- **해결 방법**: 요청에 X-UUID 헤더 추가

#### 👤 **U004 - 사용자 개인정보를 찾을 수 없습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "U004",
    "message": "사용자 개인정보를 찾을 수 없습니다."
  }
}
```
- **HTTP 상태**: 404 Not Found
- **발생 원인**: 사용자 개인정보 미등록
- **해결 방법**: 사용자 정보 재등록 요청

### 5.2 의류 관련 에러 (C001)

#### 👔 **C001 - 의류를 찾을 수 없습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "C001",
    "message": "의류를 찾을 수 없습니다."
  }
}
```
- **HTTP 상태**: 404 Not Found
- **발생 원인**: 잘못된 의류 ID 또는 삭제된 의류
- **해결 방법**: 올바른 의류 ID 확인

### 5.3 기본 모델 관련 에러 (D001)

#### 🚶 **D001 - 기본 모델을 찾을 수 없습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "D001",
    "message": "기본 모델을 찾을 수 없습니다."
  }
}
```
- **HTTP 상태**: 404 Not Found
- **발생 원인**: 잘못된 기본 모델 ID 또는 삭제된 모델
- **해결 방법**: 올바른 기본 모델 ID 확인

### 5.4 피팅 결과 관련 에러 (T001)

#### 🎯 **T001 - 피팅 결과를 찾을 수 없습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "T001",
    "message": "피팅 결과를 찾을 수 없습니다."
  }
}
```
- **HTTP 상태**: 404 Not Found
- **발생 원인**: 잘못된 피팅 결과 ID 또는 삭제된 결과
- **해결 방법**: 올바른 피팅 결과 ID 확인

### 5.5 회사 관련 에러 (CP001-CP002)

#### 🏢 **CP001 - 요청한 회사를 찾을 수 없습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "CP001",
    "message": "요청한 회사를 찾을 수 없습니다."
  }
}
```
- **HTTP 상태**: 404 Not Found

#### 🏢 **CP002 - 이미 존재하는 회사입니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "CP002",
    "message": "이미 존재하는 회사입니다."
  }
}
```
- **HTTP 상태**: 409 Conflict

### 5.6 가상피팅 관련 에러 (VF001-VF010)

#### 🤖 **VF001 - 가상피팅 처리 중 오류가 발생했습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "VF001",
    "message": "가상피팅 처리 중 오류가 발생했습니다."
  }
}
```
- **HTTP 상태**: 500 Internal Server Error
- **발생 원인**: 가상피팅 엔진 내부 오류
- **해결 방법**: 잠시 후 재시도

#### 🔍 **VF002 - 의류 카테고리 예측에 실패했습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "VF002",
    "message": "의류 카테고리 예측에 실패했습니다."
  }
}
```
- **HTTP 상태**: 400 Bad Request
- **발생 원인**: 의류 이미지 품질 문제
- **해결 방법**: 고품질 의류 이미지로 재시도

#### 🎨 **VF003 - 배경 제거 처리에 실패했습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "VF003",
    "message": "배경 제거 처리에 실패했습니다."
  }
}
```
- **HTTP 상태**: 400 Bad Request
- **발생 원인**: 복잡한 배경 또는 이미지 품질 문제
- **해결 방법**: 단순한 배경의 고품질 이미지로 재시도

#### ⏰ **VF004 - 가상피팅 처리 시간이 초과되었습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "VF004",
    "message": "가상피팅 처리 시간이 초과되었습니다."
  }
}
```
- **HTTP 상태**: 408 Request Timeout
- **발생 원인**: 서버 부하 또는 복잡한 이미지 처리
- **해결 방법**: 잠시 후 재시도

#### 🔌 **VF005 - 가상피팅 API 호출에 실패했습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "VF005",
    "message": "가상피팅 API 호출에 실패했습니다."
  }
}
```
- **HTTP 상태**: 502 Bad Gateway
- **발생 원인**: 외부 가상피팅 서비스 연결 문제
- **해결 방법**: 잠시 후 재시도

#### 🖼️ **VF006 - 이미지 로드에 실패했습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "VF006",
    "message": "이미지 로드에 실패했습니다."
  }
}
```
- **HTTP 상태**: 400 Bad Request
- **발생 원인**: 손상된 이미지 파일 또는 지원하지 않는 형식
- **해결 방법**: 올바른 이미지 파일로 재업로드

#### 🚫 **VF007 - 부적절한 콘텐츠가 감지되었습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "VF007",
    "message": "부적절한 콘텐츠가 감지되었습니다."
  }
}
```
- **HTTP 상태**: 400 Bad Request
- **발생 원인**: 콘텐츠 모더레이션 필터링
- **해결 방법**: 적절한 콘텐츠로 재업로드

#### 📷 **VF008 - 의류 이미지 타입을 자동 감지할 수 없습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "VF008",
    "message": "의류 이미지 타입을 자동 감지할 수 없습니다."
  }
}
```
- **HTTP 상태**: 400 Bad Request
- **발생 원인**: 명확하지 않은 의류 이미지
- **해결 방법**: 명확한 의류가 포함된 이미지로 재시도

#### 🤸 **VF009 - 모델 또는 의류 이미지에서 자세를 감지할 수 없습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "VF009",
    "message": "모델 또는 의류 이미지에서 자세를 감지할 수 없습니다."
  }
}
```
- **HTTP 상태**: 400 Bad Request
- **발생 원인**: 자세 감지 실패
- **해결 방법**: 명확한 자세의 이미지로 재시도

#### ⚙️ **VF010 - 가상피팅 파이프라인 처리 중 예상치 못한 오류가 발생했습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "VF010",
    "message": "가상피팅 파이프라인 처리 중 예상치 못한 오류가 발생했습니다."
  }
}
```
- **HTTP 상태**: 500 Internal Server Error
- **발생 원인**: 파이프라인 내부 오류
- **해결 방법**: 개발팀 문의

### 5.7 공통 에러 (COM001-COM008)

#### 📋 **COM001 - 잘못된 요청입니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "COM001",
    "message": "잘못된 요청입니다.",
    "validationErrors": {
      "modelUrl": "모델 이미지 URL은 필수입니다",
      "productPageUrl": "올바른 URL 형식이어야 합니다"
    }
  }
}
```
- **HTTP 상태**: 400 Bad Request
- **발생 원인**: 유효성 검증 실패, 필수 필드 누락
- **해결 방법**: `validationErrors` 필드 확인 후 수정

#### 🔧 **COM002 - 서버 내부 오류가 발생했습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "COM002",
    "message": "서버 내부 오류가 발생했습니다."
  }
}
```
- **HTTP 상태**: 500 Internal Server Error
- **발생 원인**: 예상치 못한 서버 오류
- **해결 방법**: 잠시 후 재시도, 지속 시 개발팀 문의

#### 🔐 **COM003 - 인증이 필요합니다. 유효한 헤더를 제공해주세요**
```json
{
  "isSuccess": false,
  "error": {
    "code": "COM003",
    "message": "인증이 필요합니다. 유효한 헤더를 제공해주세요."
  }
}
```
- **HTTP 상태**: 401 Unauthorized
- **발생 원인**: X-UUID 헤더 누락 또는 잘못된 값
- **해결 방법**: 올바른 X-UUID 헤더 추가

#### 🚫 **COM004 - 접근 권한이 없습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "COM004",
    "message": "접근 권한이 없습니다."
  }
}
```
- **HTTP 상태**: 403 Forbidden

#### 🔍 **COM005 - 요청하신 리소스를 찾을 수 없습니다(url 체크 필요)**
```json
{
  "isSuccess": false,
  "error": {
    "code": "COM005",
    "message": "요청하신 리소스를 찾을 수 없습니다.(url 체크 필요)"
  }
}
```
- **HTTP 상태**: 404 Not Found
- **발생 원인**: 잘못된 API 엔드포인트
- **해결 방법**: API URL 확인

#### ❌ **COM006 - 지원하지 않는 HTTP 메서드입니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "COM006",
    "message": "지원하지 않는 HTTP 메서드입니다."
  }
}
```
- **HTTP 상태**: 405 Method Not Allowed
- **발생 원인**: 잘못된 HTTP 메서드 사용
- **해결 방법**: 올바른 HTTP 메서드 사용

#### ⚠️ **COM007 - 예상치 못한 오류가 발생했습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "COM007",
    "message": "예상치 못한 오류가 발생했습니다."
  }
}
```
- **HTTP 상태**: 500 Internal Server Error

#### 📎 **COM008 - 지원하지 않는 Content-Type입니다. multipart/form-data로 요청해 주세요**
```json
{
  "isSuccess": false,
  "error": {
    "code": "COM008",
    "message": "지원하지 않는 Content-Type입니다. multipart/form-data로 요청해 주세요."
  }
}
```
- **HTTP 상태**: 415 Unsupported Media Type
- **발생 원인**: 파일 업로드 시 잘못된 Content-Type
- **해결 방법**: `multipart/form-data`로 요청

### 5.8 파일 업로드 관련 에러 (FILE001)

#### 📁 **FILE001 - 이미지 크기가 너무 큽니다. 10MB 이하로 업로드 해주세요**
```json
{
  "isSuccess": false,
  "error": {
    "code": "FILE001",
    "message": "이미지 크기가 너무 큽니다. 10MB 이하로 업로드 해주세요."
  }
}
```
- **HTTP 상태**: 400 Bad Request
- **발생 원인**: 파일 크기 제한 초과 (10MB)
- **해결 방법**: 이미지 압축 후 재업로드

### 5.9 인프라 관련 에러 (INF001-INF002)

#### ☁️ **INF001 - S3 클라이언트 생성에 실패했습니다**
```json
{
  "isSuccess": false,
  "error": {
    "code": "INF001",
    "message": "S3 클라이언트 생성에 실패했습니다."
  }
}
```
- **HTTP 상태**: 500 Internal Server Error
- **발생 원인**: AWS S3 연결 문제
- **해결 방법**: 잠시 후 재시도

#### 💾 **INF002 - 서버 메모리 부족으로 요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요**
```json
{
  "isSuccess": false,
  "error": {
    "code": "INF002",
    "message": "서버 메모리 부족으로 요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요."
  }
}
```
- **HTTP 상태**: 503 Service Unavailable
- **발생 원인**: 서버 메모리 부족
- **해결 방법**: 잠시 후 재시도

## 6. 주요 API 엔드포인트별 예상 에러

### 6.1 사용자 초기화 (`POST /user/init`)
- **COM001**: 필수 필드 누락
- **U002**: 이미 존재하는 사용자

### 6.2 가상 피팅 실행 (`POST /try-on/fitting`)
- **COM003**: X-UUID 헤더 누락
- **COM008**: multipart/form-data 아님
- **FILE001**: 파일 크기 초과
- **VF001~VF010**: 가상피팅 관련 모든 에러
- **D001**: 잘못된 기본 모델 ID

### 6.3 기본 모델 업로드 (`POST /default-model`)
- **COM003**: X-UUID 헤더 누락
- **COM008**: multipart/form-data 아님
- **FILE001**: 파일 크기 초과
- **VF003**: 배경 제거 실패

### 6.4 리소스 조회 API들
- **COM003**: X-UUID 헤더 누락
- **COM005**: 잘못된 URL
- **각 리소스별 NOT_FOUND 에러**

## 7. 프론트엔드 에러 처리 권장사항

### 7.1 에러 코드별 사용자 경험 개선

```typescript
// 에러 처리 예시
function handleApiError(error: ApiError) {
  switch (error.code) {
    case 'U003':
    case 'COM003':
      // 인증 에러 - 자동으로 재인증 시도
      redirectToLogin();
      break;
      
    case 'VF004':
    case 'VF005':
      // 일시적 에러 - 재시도 버튼 제공
      showRetryOption();
      break;
      
    case 'FILE001':
      // 파일 크기 에러 - 압축 안내
      showFileSizeWarning();
      break;
      
    case 'VF007':
      // 콘텐츠 모더레이션 - 안내 메시지
      showContentGuidelines();
      break;
      
    default:
      // 일반 에러 메시지
      showErrorMessage(error.message);
  }
}
```

### 7.2 재시도 가능한 에러 코드
- **VF001, VF004, VF005**: 일시적 서버 문제
- **COM002, COM007**: 일반적인 서버 오류
- **INF001, INF002**: 인프라 문제

### 7.3 사용자 액션이 필요한 에러 코드
- **FILE001**: 파일 크기 조정
- **VF002, VF003, VF006, VF008, VF009**: 이미지 품질 개선
- **VF007**: 적절한 콘텐츠로 변경
- **COM001**: 입력값 수정

## 8. 개발 팁

### 8.1 로깅 및 모니터링
- 에러 코드별 발생 빈도 추적
- 특정 에러 패턴 모니터링 (예: VF 시리즈 에러 급증)
- 사용자별 에러 발생 이력 분석

### 8.2 사용자 친화적 메시지
에러 코드의 기본 메시지 외에 사용자 친화적인 메시지로 변환하여 표시하는 것을 권장합니다.

```typescript
const userFriendlyMessages = {
  'VF002': '의류 이미지가 명확하지 않습니다. 다른 각도에서 촬영한 이미지를 사용해주세요.',
  'VF003': '배경이 복잡한 이미지입니다. 단순한 배경에서 촬영한 이미지를 사용해주세요.',
  'FILE001': '이미지 파일이 너무 큽니다. 10MB 이하의 이미지를 업로드해주세요.',
  // ... 추가 메시지들
};
```

---

## 문의 및 지원

API 사용 중 문제가 발생하거나 문의사항이 있으시면 개발팀에 연락해주세요.

**업데이트**: 2024년 12월 기준
