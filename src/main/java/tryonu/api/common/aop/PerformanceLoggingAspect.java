package tryonu.api.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 메서드 실행 시간을 측정하여 성능 최적화 지점을 식별하는 Aspect
 */
@Slf4j
@Aspect
@Component
public class PerformanceLoggingAspect {

    // 100ms 이상 걸리는 메서드만 성능 로깅
    private static final long PERFORMANCE_THRESHOLD_MS = 100;

    // API 응답시간 임계값 (ms)
    private static final long API_WARN_THRESHOLD_MS = 1000; // 1초 이상
    private static final long API_INFO_THRESHOLD_MS = 500; // 500ms 이상

    /**
     * 서비스 레이어 메서드 실행 시간 측정
     */
    @Around("execution(* tryonu.api.service..*ServiceImpl.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String serviceName = className.replace("ServiceImpl", "Service");

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            // 성능 임계값을 초과하는 경우만 로깅
            if (executionTime >= PERFORMANCE_THRESHOLD_MS) {
                log.warn("⚡ [Performance] {}.{} 실행시간: {}ms (최적화 검토 필요)",
                        serviceName, methodName, executionTime);
            } else {
                log.debug("⚡ [Performance] {}.{} 실행시간: {}ms",
                        serviceName, methodName, executionTime);
            }

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("⚡ [Performance] {}.{} 실행시간: {}ms (예외 발생)",
                    serviceName, methodName, executionTime);
            throw e;
        }
    }

    /**
     * 컨트롤러 레이어 메서드 실행 시간 측정 (API 응답 시간 모니터링)
     */
    @Around("execution(* tryonu.api.controller..*Controller.*(..))")
    public Object logApiExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String controllerName = className.replace("Controller", "");

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            // API 응답시간은 모든 요청을 로깅 (모니터링 목적)
            if (executionTime >= API_WARN_THRESHOLD_MS) {
                log.warn("🌐 [API-Performance] {}.{} 응답시간: {}ms (느린 응답)",
                        controllerName, methodName, executionTime);
            } else if (executionTime >= API_INFO_THRESHOLD_MS) {
                log.info("🌐 [API-Performance] {}.{} 응답시간: {}ms",
                        controllerName, methodName, executionTime);
            } else {
                log.debug("🌐 [API-Performance] {}.{} 응답시간: {}ms",
                        controllerName, methodName, executionTime);
            }

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("🌐 [API-Performance] {}.{} 응답시간: {}ms (예외 발생)",
                    controllerName, methodName, executionTime);
            throw e;
        }
    }
}
