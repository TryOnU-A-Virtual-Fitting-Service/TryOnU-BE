package tryonu.api.common.event;

import java.time.Instant;
import java.util.Map;

/**
 * API 에러 발생 이벤트
 * 슬랙 알림 등 비동기 후처리를 위해 발행한다.
 */
public record ApiErrorEvent(
    int httpStatus,
    String errorCode,
    String errorMessage,
    String httpMethod,
    String requestPath,
    String queryString,
    String userAgent,
    String remoteAddr,
    String requestBody,
    String stackTrace,
    Map<String, String> validationErrors,
    Instant occurredAt
) {}


