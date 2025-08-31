package tryonu.api.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 서비스 레이어 메서드의 시작과 완료를 자동으로 로깅하는 Aspect
 */
@Slf4j
@Aspect
@Component
public class ServiceLoggingAspect {

    /**
     * 서비스 메서드 실행 전 로깅
     */
    @Before("execution(* tryonu.api.service..*ServiceImpl.*(..))")
    public void logMethodEntry(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // 서비스명에서 'Impl' 제거하고 '[ServiceName]' 형태로 포맷팅
        String serviceName = className.replace("ServiceImpl", "Service");

        if (args.length > 0) {
            // 민감한 정보는 로깅하지 않도록 필터링
            String argsString = formatArguments(args);
            log.info("[{}] {} 시작 - params={}", serviceName, methodName, argsString);
        } else {
            log.info("[{}] {} 시작", serviceName, methodName);
        }
    }

    /**
     * 서비스 메서드 정상 완료 후 로깅
     */
    @AfterReturning(pointcut = "execution(* tryonu.api.service..*ServiceImpl.*(..))", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        String serviceName = className.replace("ServiceImpl", "Service");

        if (result != null) {
            String resultType = result.getClass().getSimpleName();
            log.info("[{}] {} 완료 - returnType={}", serviceName, methodName, resultType);
        } else {
            log.info("[{}] {} 완료", serviceName, methodName);
        }
    }

    /**
     * 인자들을 안전하게 문자열로 변환 (민감한 정보 필터링)
     */
    private String formatArguments(Object[] args) {
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) {
                        return "null";
                    }

                    String argString = arg.toString();
                    String className = arg.getClass().getSimpleName();

                    // MultipartFile의 경우 파일명과 크기만 로깅
                    if (className.contains("MultipartFile")) {
                        return "MultipartFile[...]";
                    }

                    // 긴 문자열은 잘라서 표시
                    if (argString.length() > 100) {
                        return className + "[" + argString.substring(0, 100) + "...]";
                    }

                    return argString;
                })
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }
}
