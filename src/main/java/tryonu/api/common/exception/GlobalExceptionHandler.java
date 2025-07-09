package tryonu.api.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tryonu.api.common.dto.ApiResponseWrapper;

/**
 * 전역 예외 처리기
 * 애플리케이션에서 발생하는 모든 예외를 일관된 형식으로 처리합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
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
            "RUNTIME_ERROR",
            "서버 내부 오류가 발생했습니다."
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
            "UNEXPECTED_ERROR",
            "예상치 못한 오류가 발생했습니다."
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 