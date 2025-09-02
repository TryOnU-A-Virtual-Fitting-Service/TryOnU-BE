package tryonu.api.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import tryonu.api.common.exception.CustomException;

/**
 * 서비스와 레포지토리 레이어에서 발생하는 예외를 자동으로 로깅하는 Aspect
 * (컨트롤러로 전파되는 예외는 GlobalExceptionHandler에서 처리하므로 중복 로깅 방지)
 */
@Slf4j
@Aspect
@Component
public class ExceptionLoggingAspect {

    /**
     * 서비스 레이어에서 예외 발생 시 자동 로깅
     * 단, 컨트롤러로 전파되는 예외는 GlobalExceptionHandler에서 처리하므로 로깅하지 않음
     */
    @AfterThrowing(pointcut = "execution(* tryonu.api.service..*ServiceImpl.*(..))", throwing = "exception")
    public void logServiceException(JoinPoint joinPoint, Throwable exception) {
        // 컨트롤러로 전파되는 예외는 GlobalExceptionHandler에서 처리하므로 로깅하지 않음
        // 이는 중복 로깅을 방지하기 위함
        if (isControllerPropagatedException(exception)) {
            return;
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String serviceName = className.replace("ServiceImpl", "Service");

        if (exception instanceof CustomException customException) {
            // CustomException의 경우 ErrorCode와 메시지 로깅
            log.error("❌ [{}] {}.{} 비즈니스 예외 발생 - errorCode={}, message={}",
                    serviceName, className, methodName,
                    customException.getErrorCode().name(),
                    customException.getMessage());
        } else {
            // 일반 예외의 경우 상세 정보 로깅
            log.error("💥 [{}] {}.{} 시스템 예외 발생 - exceptionType={}, message={}",
                    serviceName, className, methodName,
                    exception.getClass().getSimpleName(),
                    exception.getMessage(), exception);
        }
    }

    /**
     * 컨트롤러로 전파되는 예외인지 확인
     * GlobalExceptionHandler에서 처리하는 예외들은 중복 로깅을 방지하기 위해 제외
     */
    private boolean isControllerPropagatedException(Throwable exception) {
        return exception instanceof CustomException ||
                exception instanceof RuntimeException ||
                exception instanceof Exception;
    }

    /**
     * Repository 레이어에서 예외 발생 시 자동 로깅
     */
    @AfterThrowing(pointcut = "execution(* tryonu.api.repository..*Repository*.*(..))", throwing = "exception")
    public void logRepositoryException(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.error("🗄️💥 [Repository-Error] {}.{} 데이터 접근 예외 - exceptionType={}, message={}",
                className, methodName,
                exception.getClass().getSimpleName(),
                exception.getMessage(), exception);
    }
}
