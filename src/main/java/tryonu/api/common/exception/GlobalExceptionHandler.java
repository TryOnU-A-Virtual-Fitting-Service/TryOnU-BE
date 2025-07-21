package tryonu.api.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tryonu.api.common.exception.enums.ErrorCode;

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
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = (fieldError != null) ? fieldError.getDefaultMessage() : "잘못된 요청입니다.";
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponseWrapper.ofFailure(ErrorCode.INVALID_REQUEST.getCode(), message));
    }

    /**
     * 정적 리소스 요청 404 (NoResourceFoundException) 처리
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponseWrapper.ofFailure(ErrorCode.RESOURCE_NOT_FOUND.getCode(), ErrorCode.RESOURCE_NOT_FOUND.getMessage()));
    }
} 