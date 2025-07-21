package tryonu.api.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // User 관련 에러
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS("U002", "이미 존재하는 사용자입니다."),
    DEVICE_ID_REQUIRED("U003", "디바이스 ID는 필수입니다."),
    USER_INFO_NOT_FOUND("U004", "사용자 개인정보를 찾을 수 없습니다."),
    
    // Cloth 관련 에러
    CLOTH_NOT_FOUND("C001", "의류를 찾을 수 없습니다."),
    
    // FittingModel 관련 에러
    FITTING_MODEL_NOT_FOUND("F001", "피팅 모델을 찾을 수 없습니다."),
    
    // DefaultModel 관련 에러
    DEFAULT_MODEL_NOT_FOUND("D001", "기본 모델을 찾을 수 없습니다."),
    
    // TryOnResult 관련 에러
    TRY_ON_RESULT_NOT_FOUND("T001", "피팅 결과를 찾을 수 없습니다."),
    
    // 공통 에러
    INVALID_REQUEST("COM001", "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR("COM002", "서버 내부 오류가 발생했습니다."),
    UNAUTHORIZED("COM003", "인증이 필요합니다."),
    FORBIDDEN("COM004", "접근 권한이 없습니다."),
    RESOURCE_NOT_FOUND("COM005", "요청하신 리소스를 찾을 수 없습니다.(url 체크 필요)"),
    UNEXPECTED_ERROR("COM006", "예상치 못한 오류가 발생했습니다."),
    
    // 인프라 관련 에러
    S3_CLIENT_CREATION_FAILED("INF001", "S3 클라이언트 생성에 실패했습니다.");
    
    private final String code;
    private final String message;
} 