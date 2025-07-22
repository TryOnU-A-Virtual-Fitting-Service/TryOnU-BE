package tryonu.api.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tryonu.api.common.exception.enums.ErrorCode;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import java.util.Map;

/**
 * 전역 예외 처리기
 * 애플리케이션에서 발생하는 모든 예외를 일관된 형식으로 처리합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 커스텀 예외 처리
     * @param ex CustomException
     * @return 에러 응답
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleCustomException(CustomException ex) {
        log.error("❗ [GlobalExceptionHandler] 커스텀 예외 발생: code={}, message={}", ex.getErrorCode().getCode(), ex.getMessage());
        HttpStatus status = ex.getErrorCode().getHttpStatus();
        ApiResponseWrapper<Void> response = ApiResponseWrapper.ofFailure(
            ex.getErrorCode().getCode(),
            ex.getMessage()
        );
        return ResponseEntity.status(status).body(response);
    }

    /**
     * 일반적인 런타임 예외 처리
     * 
     * @param e 발생한 예외
     * @return 에러 응답
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleRuntimeException(RuntimeException e) {
        log.error("⚠️ [GlobalExceptionHandler] 런타임 예외 발생", e);
        ApiResponseWrapper<Void> response = ApiResponseWrapper.ofFailure(
            ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 예상치 못한 예외 처리
     * 
     * @param e 발생한 예외
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleException(Exception e) {
        log.error("🚨 [GlobalExceptionHandler] 예상치 못한 예외 발생", e);
        ApiResponseWrapper<Void> response = ApiResponseWrapper.ofFailure(
            ErrorCode.UNEXPECTED_ERROR.getCode(),
            ErrorCode.UNEXPECTED_ERROR.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * @Valid 검증 실패 (RequestBody 등) 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(java.util.stream.Collectors.toMap(
                org.springframework.validation.FieldError::getField,
                org.springframework.validation.FieldError::getDefaultMessage,
                (msg1, msg2) -> msg1 // 필드 중복 시 첫 번째 메시지 사용
            ));
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponseWrapper.ofValidationFailure(ErrorCode.INVALID_REQUEST.getCode(), "요청값이 올바르지 않습니다.", errors));
    }

    /**
     * 정적 리소스 요청 404 (NoResourceFoundException) 처리
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponseWrapper.ofFailure(ErrorCode.RESOURCE_NOT_FOUND.getCode(), ErrorCode.RESOURCE_NOT_FOUND.getMessage()));
    }

    /**
     * 이미지 응답 크기 초과 처리
     */
    @ExceptionHandler(DataBufferLimitException.class)
    public ResponseEntity<ApiResponseWrapper<?>> handleDataBufferLimitException(DataBufferLimitException ex) {
        log.error("이미지 응답 크기 초과: {}", ex.getMessage());
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponseWrapper.ofFailure("IMAGE_TOO_LARGE", "이미지 크기가 너무 큽니다. 10MB 이하로 업로드 해주세요."));
    }

    /**
     * 지원하지 않는 Content-Type 예외 처리
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponseWrapper<?>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        log.error("지원하지 않는 Content-Type: {}", ex.getContentType());
        return ResponseEntity
            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .body(ApiResponseWrapper.ofFailure("UNSUPPORTED_MEDIA_TYPE", "지원하지 않는 Content-Type입니다. multipart/form-data로 요청해 주세요."));
    }
} 