package tryonu.api.common.exception.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // User 관련 에러
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("U002", "이미 존재하는 사용자입니다.", HttpStatus.CONFLICT),
    DEVICE_ID_REQUIRED("U003", "디바이스 ID는 필수입니다.", HttpStatus.BAD_REQUEST),
    USER_INFO_NOT_FOUND("U004", "사용자 개인정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    
    // Cloth 관련 에러
    CLOTH_NOT_FOUND("C001", "의류를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    
    // FittingModel 관련 에러
    FITTING_MODEL_NOT_FOUND("F001", "피팅 모델을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    
    // DefaultModel 관련 에러
    DEFAULT_MODEL_NOT_FOUND("D001", "기본 모델을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    
    // TryOnResult 관련 에러
    TRY_ON_RESULT_NOT_FOUND("T001", "피팅 결과를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    
    // 가상피팅 관련 에러
    VIRTUAL_FITTING_FAILED("VF001", "가상피팅 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    CATEGORY_PREDICTION_FAILED("VF002", "의류 카테고리 예측에 실패했습니다.", HttpStatus.BAD_REQUEST),
    BACKGROUND_REMOVAL_FAILED("VF003", "배경 제거 처리에 실패했습니다.", HttpStatus.BAD_REQUEST),
    VIRTUAL_FITTING_TIMEOUT("VF004", "가상피팅 처리 시간이 초과되었습니다.", HttpStatus.REQUEST_TIMEOUT),
    VIRTUAL_FITTING_API_ERROR("VF005", "가상피팅 API 호출에 실패했습니다.", HttpStatus.BAD_GATEWAY),
    
    // fashn.ai API 구체적 에러들
    IMAGE_LOAD_ERROR("VF006", "이미지 로드에 실패했습니다.", HttpStatus.BAD_REQUEST),
    CONTENT_MODERATION_ERROR("VF007", "부적절한 콘텐츠가 감지되었습니다.", HttpStatus.BAD_REQUEST), 
    PHOTO_TYPE_ERROR("VF008", "의류 이미지 타입을 자동 감지할 수 없습니다.", HttpStatus.BAD_REQUEST),
    POSE_ERROR("VF009", "모델 또는 의류 이미지에서 자세를 감지할 수 없습니다.", HttpStatus.BAD_REQUEST),
    PIPELINE_ERROR("VF010", "가상피팅 파이프라인 처리 중 예상치 못한 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // 공통 에러
    INVALID_REQUEST("COM001", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("COM002", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED("COM003", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("COM004", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    RESOURCE_NOT_FOUND("COM005", "요청하신 리소스를 찾을 수 없습니다.(url 체크 필요)", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("COM006", "지원하지 않는 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    UNEXPECTED_ERROR("COM007", "예상치 못한 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // 파일 업로드 관련 에러
    IMAGE_TOO_LARGE("FILE001", "이미지 크기가 너무 큽니다. 10MB 이하로 업로드 해주세요.", HttpStatus.BAD_REQUEST),
    
    // 인프라 관련 에러
    S3_CLIENT_CREATION_FAILED("INF001", "S3 클라이언트 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
} 