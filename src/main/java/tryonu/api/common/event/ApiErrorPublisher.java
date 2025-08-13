package tryonu.api.common.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

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
            if (request instanceof ContentCachingRequestWrapper wrapper) {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length == 0) {
                    // 아직 읽히지 않았다면 InputStream에서 한 번 읽어 캐시되도록 유도
                    buf = wrapper.getInputStream().readAllBytes();
                }
                if (buf.length > 0) {
                    return new String(buf, wrapper.getCharacterEncoding());
                }
            } else {
                // 캐싱 래퍼가 아닌 경우에도 최소한 본문을 한번 읽어 문자열화 (주의: 이후 체인에선 사용 불가)
                byte[] buf = request.getInputStream().readAllBytes();
                if (buf.length > 0) {
                    return new String(buf, request.getCharacterEncoding() != null ? request.getCharacterEncoding() : "UTF-8");
                }
            }
        } catch (Exception ignored) {}
        return null;
    }
}


