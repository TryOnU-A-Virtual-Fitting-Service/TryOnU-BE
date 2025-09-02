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
import org.springframework.http.converter.HttpMessageNotReadableException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import tryonu.api.common.event.ApiErrorPublisher;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Map;

/**
 * 전역 예외 처리기
 * 애플리케이션에서 발생하는 모든 예외를 일관된 형식으로 처리합니다.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ApiErrorPublisher apiErrorPublisher;
    
    /**
     * 커스텀 예외 처리
     * @param ex CustomException
     * @return 에러 응답
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleCustomException(CustomException ex, HttpServletRequest request) {
        log.error("❗ [GlobalExceptionHandler] 커스텀 예외 발생: code={}, message={}", ex.getErrorCode().getCode(), ex.getMessage());
        HttpStatus status = ex.getErrorCode().getHttpStatus();
        ApiResponseWrapper<Void> response = ApiResponseWrapper.ofFailure(
            ex.getErrorCode().getCode(),
            ex.getMessage()
        );
        apiErrorPublisher.publish(request, status.value(), ex.getErrorCode().getCode(), ex.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    /**
     * 메소드 파라미터 검증 실패 (@RequestParam, @PathVariable 등) 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
            .collect(java.util.stream.Collectors.toMap(
                v -> v.getPropertyPath().toString(),
                jakarta.validation.ConstraintViolation::getMessage,
                (a, b) -> a
            ));
        apiErrorPublisher.publishWithValidationErrors(request, HttpStatus.BAD_REQUEST.value(), ErrorCode.INVALID_REQUEST.getCode(), "Constraint violation", errors);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponseWrapper.ofValidationFailure(ErrorCode.INVALID_REQUEST.getCode(), "요청값이 올바르지 않습니다.", errors));
    }

    /**
     * 일반적인 런타임 예외 처리
     * 
     * @param e 발생한 예외
     * @return 에러 응답
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("⚠️ [GlobalExceptionHandler] 런타임 예외 발생", e);
        ApiResponseWrapper<Void> response = ApiResponseWrapper.ofFailure(
            ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
            ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
        );
        apiErrorPublisher.publishWithThrowable(request, HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 메모리 부족 예외 처리 (OutOfMemoryError)
     */
    @ExceptionHandler(OutOfMemoryError.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleOutOfMemoryError(OutOfMemoryError e, HttpServletRequest request) {
        // 메모리 상태 정보 수집
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024; // MB
        long totalMemory = runtime.totalMemory() / 1024 / 1024; // MB
        long freeMemory = runtime.freeMemory() / 1024 / 1024; // MB
        long usedMemory = totalMemory - freeMemory; // MB
        
        log.error("💥 [GlobalExceptionHandler] OutOfMemoryError 발생 - " +
                "메모리 상태: used={}MB, total={}MB, max={}MB, free={}MB, " +
                "에러타입: {}", 
                usedMemory, totalMemory, maxMemory, freeMemory, e.getMessage(), e);
        
        // 강제 GC 실행 시도 (주의: 프로덕션에서는 권장하지 않지만 긴급 상황)
        try {
            System.gc();
            log.warn("⚠️ [GlobalExceptionHandler] 긴급 GC 실행 완료");
        } catch (Exception gcException) {
            log.error("❌ [GlobalExceptionHandler] GC 실행 실패", gcException);
        }
        
        ApiResponseWrapper<Void> response = ApiResponseWrapper.ofFailure(
            ErrorCode.OUT_OF_MEMORY_ERROR.getCode(),
            ErrorCode.OUT_OF_MEMORY_ERROR.getMessage()
        );
        apiErrorPublisher.publishWithThrowable(request, HttpStatus.SERVICE_UNAVAILABLE.value(), ErrorCode.OUT_OF_MEMORY_ERROR.getCode(), e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * 데이터 무결성 제약 위반 (예: Unique Key 중복)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        String message = "데이터 무결성 제약 위반: 중복되었거나 올바르지 않은 값입니다.";
        log.warn("🔐 [GlobalExceptionHandler] DataIntegrityViolationException - {}", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());
        // 중복 키 등은 409로 응답
        HttpStatus status = HttpStatus.CONFLICT;
        apiErrorPublisher.publishWithThrowable(request, status.value(), ErrorCode.USER_ALREADY_EXISTS.getCode(), message, ex);
        return ResponseEntity
            .status(status)
            .body(ApiResponseWrapper.ofFailure(ErrorCode.USER_ALREADY_EXISTS.getCode(), message));
    }

    /**
     * 예상치 못한 예외 처리
     * 
     * @param e 발생한 예외
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleException(Exception e, HttpServletRequest request) {
        log.error("🚨 [GlobalExceptionHandler] 예상치 못한 예외 발생", e);
        ApiResponseWrapper<Void> response = ApiResponseWrapper.ofFailure(
            ErrorCode.UNEXPECTED_ERROR.getCode(),
            ErrorCode.UNEXPECTED_ERROR.getMessage()
        );
        apiErrorPublisher.publishWithThrowable(request, HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.UNEXPECTED_ERROR.getCode(), e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * JSON 파싱 실패 등 요청 본문을 읽을 수 없는 경우 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String message = "요청 본문을 파싱할 수 없습니다. JSON 형식을 확인해 주세요.";
        log.warn("✋ [GlobalExceptionHandler] 메시지 변환 실패 - {}", ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());
        apiErrorPublisher.publishWithThrowable(request, HttpStatus.BAD_REQUEST.value(), ErrorCode.INVALID_REQUEST.getCode(), message, ex);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponseWrapper.ofFailure(ErrorCode.INVALID_REQUEST.getCode(), message));
    }

    /**
     * @Valid 검증 실패 (RequestBody 등) 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(java.util.stream.Collectors.toMap(
                org.springframework.validation.FieldError::getField,
                org.springframework.validation.FieldError::getDefaultMessage,
                (msg1, msg2) -> msg1 // 필드 중복 시 첫 번째 메시지 사용
            ));
        apiErrorPublisher.publishWithValidationErrors(request, HttpStatus.BAD_REQUEST.value(), ErrorCode.INVALID_REQUEST.getCode(), "Validation failed", errors);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponseWrapper.ofValidationFailure(ErrorCode.INVALID_REQUEST.getCode(), "요청값이 올바르지 않습니다.", errors));
    }

    /**
     * 정적 리소스 요청 404 (NoResourceFoundException) 처리
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        apiErrorPublisher.publish(request, HttpStatus.NOT_FOUND.value(), ErrorCode.RESOURCE_NOT_FOUND.getCode(), ErrorCode.RESOURCE_NOT_FOUND.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponseWrapper.ofFailure(ErrorCode.RESOURCE_NOT_FOUND.getCode(), ErrorCode.RESOURCE_NOT_FOUND.getMessage()));
    }

    /**
     * 이미지 응답 크기 초과 처리
     */
    @ExceptionHandler(DataBufferLimitException.class)
    public ResponseEntity<ApiResponseWrapper<?>> handleDataBufferLimitException(DataBufferLimitException ex, HttpServletRequest request) {
        log.error("이미지 응답 크기 초과: {}", ex.getMessage());
        apiErrorPublisher.publish(request, ErrorCode.IMAGE_TOO_LARGE.getHttpStatus().value(), ErrorCode.IMAGE_TOO_LARGE.getCode(), ex.getMessage());
        return ResponseEntity
            .status(ErrorCode.IMAGE_TOO_LARGE.getHttpStatus())
            .body(ApiResponseWrapper.ofFailure(ErrorCode.IMAGE_TOO_LARGE.getCode(), ErrorCode.IMAGE_TOO_LARGE.getMessage()));
    }

    /**
     * 지원하지 않는 Content-Type 예외 처리
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponseWrapper<?>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.error("지원하지 않는 Content-Type: {}", ex.getContentType());
        apiErrorPublisher.publish(request,
                ErrorCode.UNSUPPORTED_MEDIA_TYPE.getHttpStatus().value(),
                ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode(),
                ErrorCode.UNSUPPORTED_MEDIA_TYPE.getMessage());
        return ResponseEntity
            .status(ErrorCode.UNSUPPORTED_MEDIA_TYPE.getHttpStatus())
            .body(ApiResponseWrapper.ofFailure(
                    ErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode(),
                    ErrorCode.UNSUPPORTED_MEDIA_TYPE.getMessage()
            ));
    }


}