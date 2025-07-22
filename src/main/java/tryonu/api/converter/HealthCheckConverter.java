package tryonu.api.converter;

import org.springframework.stereotype.Component;
import tryonu.api.dto.responses.HealthCheckResponse;

import java.time.LocalDateTime;

@Component
public class HealthCheckConverter {

    /**
     * HealthCheckResponse 생성
     */
    public HealthCheckResponse toHealthCheckResponse(String status, LocalDateTime timestamp, long uptime) {
        return new HealthCheckResponse(status, timestamp, uptime);
    }
} 