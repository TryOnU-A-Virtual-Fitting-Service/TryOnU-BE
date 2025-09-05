package tryonu.api.common.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

/**
 * 모든 API 응답을 위한 공통 래퍼 클래스
 * 
 * @param <T> 응답 데이터의 타입
 */
@Schema(description = "API 응답 공통 형식", 
        example = """
        {
          "isSuccess": true,
          "data": {
            "id": 1,
            "name": "예시 데이터"
          },
          "error": null
        }""")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponseWrapper<T>(
    @Schema(description = "요청 성공 여부", example = "true")
    boolean isSuccess,
    
    @Schema(description = "응답 데이터 (성공 시에만 존재)")
    T data,
    
    @Schema(description = "에러 정보 (실패 시에만 존재)")
    ErrorResponse error
) {
    
    /**
     * 성공 응답 생성
     * 
     * @param data 응답 데이터
     * @param <T> 데이터 타입
     * @return 성공 응답
     */
    public static <T> ApiResponseWrapper<T> ofSuccess(T data) {
        return new ApiResponseWrapper<>(true, data, null);
    }
    
    /**
     * 성공 응답 생성 (데이터 없음)
     * 
     * @param <T> 데이터 타입
     * @return 성공 응답
     */
    public static <T> ApiResponseWrapper<T> ofSuccess() {
        return new ApiResponseWrapper<>(true, null, null);
    }
    
    /**
     * 실패 응답 생성
     * 
     * @param errorCode 에러 코드
     * @param message 에러 메시지
     * @param <T> 데이터 타입
     * @return 실패 응답
     */
    public static <T> ApiResponseWrapper<T> ofFailure(String errorCode, String message) {
        return new ApiResponseWrapper<>(false, null, new ErrorResponse(errorCode, message, null));
    }

    /**
     * 필드별 validation 에러를 포함한 실패 응답 생성
     * @param errorCode 에러 코드
     * @param message 에러 메시지
     * @param validationErrors 필드별 에러 맵
     * @param <T> 데이터 타입
     * @return 실패 응답
     */
    public static <T> ApiResponseWrapper<T> ofValidationFailure(String errorCode, String message, Map<String, String> validationErrors) {
        return new ApiResponseWrapper<>(false, null, new ErrorResponse(errorCode, message, validationErrors));
    }
    
    /**
     * 에러 응답 정보
     * 
     * @param code 에러 코드
     * @param message 에러 메시지
     * @param validationErrors 필드별 validation 에러 (선택)
     */
    @Schema(description = "에러 응답 정보",
            example = """
            {
              "code": "USER_NOT_FOUND",
              "message": "사용자를 찾을 수 없습니다.",
              "validationErrors": null
            }""")
    public record ErrorResponse(
        @Schema(description = "에러 코드", example = "USER_NOT_FOUND")
        String code,
        
        @Schema(description = "에러 메시지", example = "사용자를 찾을 수 없습니다.")
        String message,

        @Schema(description = "필드별 validation 에러 (validation 실패 시에만 존재)", 
                example = "{\"name\": \"이름은 필수입니다.\"}")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Map<String, String> validationErrors
    ) {}
} 