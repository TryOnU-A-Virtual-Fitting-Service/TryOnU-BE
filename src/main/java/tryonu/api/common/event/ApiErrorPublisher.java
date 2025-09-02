package tryonu.api.common.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import tryonu.api.common.auth.SecurityUtils;

import java.time.Instant;
import java.io.PrintWriter;
import java.io.StringWriter;

@Component
@RequiredArgsConstructor
public class ApiErrorPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(HttpServletRequest request, int httpStatus, String errorCode, String message) {
        publishInternal(request, httpStatus, errorCode, message, null, null);
    }

    public void publishWithValidationErrors(HttpServletRequest request, int httpStatus, String errorCode,
            String message, java.util.Map<String, String> validationErrors) {
        publishInternal(request, httpStatus, errorCode, message, null, validationErrors);
    }

    public void publishWithThrowable(HttpServletRequest request, int httpStatus, String errorCode, String message,
            Throwable t) {
        publishInternal(request, httpStatus, errorCode, message, toStackTrace(t), null);
    }

    private void publishInternal(
            HttpServletRequest request,
            int httpStatus,
            String errorCode,
            String message,
            String stacktrace,
            java.util.Map<String, String> validationErrors) {
        String requestBody = extractBody(request);
        Long userId = extractCurrentUserId();
        ApiErrorEvent event = new ApiErrorEvent(
                httpStatus,
                errorCode,
                message,
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getHeader("User-Agent"),
                resolveClientIp(request),
                requestBody,
                stacktrace,
                validationErrors,
                userId,
                Instant.now());
        publisher.publishEvent(event);
    }

    private String toStackTrace(Throwable t) {
        if (t == null)
            return null;
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            t.printStackTrace(pw);
            pw.flush();
            return sw.toString();
        }
    }

    private String extractBody(HttpServletRequest request) {
        try {
            // 지연 로딩된 요청 본문 확인 (우리 필터 방식)
            @SuppressWarnings("unchecked")
            java.util.function.Supplier<String> bodySupplier = (java.util.function.Supplier<String>) request
                    .getAttribute("getRequestBody");

            if (bodySupplier != null) {
                String cachedBody = bodySupplier.get();
                if (cachedBody != null && !cachedBody.trim().isEmpty()) {
                    return cachedBody;
                }
            }

            // 캐싱된 본문이 없으면 메타 정보 반환
            String contentType = request.getContentType();
            StringBuilder sb = new StringBuilder();
            sb.append("Content-Type: ").append(contentType != null ? contentType : "Unknown");
            sb.append(", Content-Length: ").append(request.getContentLengthLong());

            // 파라미터가 있다면 추가
            if (!request.getParameterMap().isEmpty()) {
                sb.append(", Parameters: ").append(request.getParameterMap().keySet());
            }

            return sb.toString();
        } catch (Exception e) {
            return "Failed to extract request body";
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            int commaIndex = xff.indexOf(',');
            return (commaIndex > -1 ? xff.substring(0, commaIndex) : xff).trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        return (realIp != null && !realIp.isBlank()) ? realIp.trim() : request.getRemoteAddr();
    }

    /**
     * 현재 인증된 사용자의 ID를 추출합니다.
     * 인증되지 않은 경우 null을 반환합니다.
     */
    private Long extractCurrentUserId() {
        try {
            return SecurityUtils.getCurrentUserId();
        } catch (Exception e) {
            // 인증되지 않은 사용자이거나 사용자 정보를 가져올 수 없는 경우
            return null;
        }
    }
}
