package tryonu.api.common.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.io.PrintWriter;
import java.io.StringWriter;

@Component
@RequiredArgsConstructor
public class ApiErrorPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(HttpServletRequest request, int httpStatus, String errorCode, String message) {
        String requestBody = extractBody(request);
        ApiErrorEvent event = new ApiErrorEvent(
                httpStatus,
                errorCode,
                message,
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getHeader("User-Agent"),
                request.getRemoteAddr(),
                requestBody,
                null,
                null,
                Instant.now()
        );
        publisher.publishEvent(event);
    }

    public void publishWithValidationErrors(HttpServletRequest request, int httpStatus, String errorCode, String message, java.util.Map<String, String> validationErrors) {
        String requestBody = extractBody(request);
        ApiErrorEvent event = new ApiErrorEvent(
                httpStatus,
                errorCode,
                message,
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getHeader("User-Agent"),
                request.getRemoteAddr(),
                requestBody,
                null,
                validationErrors,
                Instant.now()
        );
        publisher.publishEvent(event);
    }

    public void publishWithThrowable(HttpServletRequest request, int httpStatus, String errorCode, String message, Throwable t) {
        String requestBody = extractBody(request);
        String stacktrace = toStackTrace(t);
        ApiErrorEvent event = new ApiErrorEvent(
                httpStatus,
                errorCode,
                message,
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getHeader("User-Agent"),
                request.getRemoteAddr(),
                requestBody,
                stacktrace,
                null,
                Instant.now()
        );
        publisher.publishEvent(event);
    }

    private String toStackTrace(Throwable t) {
        if (t == null) return null;
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
            java.util.function.Supplier<String> bodySupplier = 
                (java.util.function.Supplier<String>) request.getAttribute("getRequestBody");

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
}


