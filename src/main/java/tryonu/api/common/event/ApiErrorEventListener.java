package tryonu.api.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tryonu.api.common.notification.SlackNotifier;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiErrorEventListener {

    private final SlackNotifier slackNotifier;

    @Value("${monitoring.slack.notify-4xx:true}")
    private boolean notify4xx;

    @Value("${monitoring.slack.notify-5xx:true}")
    private boolean notify5xx;

    @Async("taskExecutor")
    @EventListener
    public void onApiError(ApiErrorEvent event) {
        int status = event.httpStatus();
        boolean is4xx = status >= 400 && status < 500;
        boolean is5xx = status >= 500;

        if ((is4xx && notify4xx) || (is5xx && notify5xx)) {
            String text = "[API ERROR] status=" + status +
                    ", code=" + event.errorCode() +
                    ", message=" + event.errorMessage() +
                    ", method=" + event.httpMethod() +
                    ", path=" + event.requestPath() +
                    (event.queryString() != null ? ("?" + event.queryString()) : "") +
                    ", ua=" + event.userAgent() +
                    ", ip=" + event.remoteAddr();
            slackNotifier.send(text);
        } else {
            log.debug("[ApiErrorEventListener] 알림 건너뜀 status={}", status);
        }
    }
}


