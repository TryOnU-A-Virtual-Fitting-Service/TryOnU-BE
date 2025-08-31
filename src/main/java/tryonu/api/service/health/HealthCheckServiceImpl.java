package tryonu.api.service.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tryonu.api.dto.responses.HealthCheckResponse;
import tryonu.api.converter.HealthCheckConverter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 헬스체크 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HealthCheckServiceImpl implements HealthCheckService {

    private final HealthCheckConverter healthCheckConverter;
    private final LocalDateTime startTime = LocalDateTime.now();

    @Override
    public HealthCheckResponse checkHealth() {
        LocalDateTime now = LocalDateTime.now();
        long uptimeSeconds = ChronoUnit.SECONDS.between(startTime, now);

        HealthCheckResponse response = healthCheckConverter.toHealthCheckResponse("UP", now, uptimeSeconds);

        return response;
    }
}