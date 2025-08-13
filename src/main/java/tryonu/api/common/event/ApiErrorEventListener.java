package tryonu.api.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tryonu.api.common.notification.SlackNotifier;
import java.util.List;
import java.util.Map;

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
            Map<String, Object> payload = buildBlockKitPayload(event);
            slackNotifier.sendPayload(payload);
        } else {
            log.debug("[ApiErrorEventListener] 알림 건너뜀 status={}", status);
        }
    }

    private Map<String, Object> buildBlockKitPayload(ApiErrorEvent e) {
        boolean is5xx = e.httpStatus() >= 500;
        String headerText = is5xx ? "🚨 API 에러 발생" : "⚠️ 클라이언트 에러 발생 (4xx)";
        String pathWithQuery = e.requestPath() + (e.queryString() != null ? ("?" + e.queryString()) : "");
        String queryStringValue = (e.queryString() == null || e.queryString().isBlank()) ? "null" : e.queryString();

        Map<String, Object> header = Map.of(
                "type", "header",
                "text", Map.of("type", "plain_text", "text", headerText, "emoji", true)
        );

        StringBuilder messageText = new StringBuilder("*💬 Message*\n> ").append(e.errorMessage());
        if (e.validationErrors() != null && !e.validationErrors().isEmpty()) {
            String details = e.validationErrors().entrySet().stream()
                    .map(en -> "`" + en.getKey() + "`: " + en.getValue())
                    .collect(java.util.stream.Collectors.joining("\n"));
            messageText.append("\n\n*Details*\n").append(details);
        }
        Map<String, Object> message = Map.of(
                "type", "section",
                "text", Map.of("type", "mrkdwn", "text", messageText.toString())
        );

        Map<String, Object> fields = Map.of(
                "type", "section",
                "fields", List.of(
                        Map.of("type", "mrkdwn", "text", "*🔢 Status Code*\n`" + e.httpStatus() + "`"),
                        Map.of("type", "mrkdwn", "text", "*🏷️ Error Code*\n`" + e.errorCode() + "`"),
                        Map.of("type", "mrkdwn", "text", "*📍 Path*\n`" + pathWithQuery + "`"),
                        Map.of("type", "mrkdwn", "text", "*▶️ Method*\n`" + e.httpMethod() + "`"),
                        Map.of("type", "mrkdwn", "text", "*🔎 Query*\n`" + queryStringValue + "`")
                )
        );

        Map<String, Object> divider = Map.of("type", "divider");

        Map<String, Object> context = Map.of(
                "type", "context",
                "elements", List.of(
                        Map.of("type", "mrkdwn", "text", "💻 *IP:* `" + e.remoteAddr() + "`   |   👤 *User-Agent:* `" + String.valueOf(e.userAgent()) + "`")
                )
        );

        String bodyText = (e.requestBody() == null || e.requestBody().isBlank()) ? "null" : truncate(e.requestBody(), 1800);
        Map<String, Object> bodyBlock = Map.of(
                "type", "section",
                "text", Map.of("type", "mrkdwn", "text", "*🧾 Request Body*\n```" + bodyText + "```")
        );

        Map<String, Object> stackBlock = (e.stackTrace() == null || e.stackTrace().isBlank()) ? null : Map.of(
                "type", "section",
                "text", Map.of("type", "mrkdwn", "text", "*🧵 Stack Trace*\n```" + truncate(e.stackTrace(), 2500) + "```")
        );

        if (stackBlock != null) {
            return Map.of("blocks", List.of(header, message, fields, divider, context, bodyBlock, stackBlock));
        }
        return Map.of("blocks", List.of(header, message, fields, divider, context, bodyBlock));
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }
}


