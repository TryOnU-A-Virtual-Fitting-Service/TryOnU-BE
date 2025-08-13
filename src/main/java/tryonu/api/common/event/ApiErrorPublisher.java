package tryonu.api.common.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ApiErrorPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(HttpServletRequest request, int httpStatus, String errorCode, String message) {
        ApiErrorEvent event = new ApiErrorEvent(
                httpStatus,
                errorCode,
                message,
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getHeader("User-Agent"),
                request.getRemoteAddr(),
                Instant.now()
        );
        publisher.publishEvent(event);
    }
}


