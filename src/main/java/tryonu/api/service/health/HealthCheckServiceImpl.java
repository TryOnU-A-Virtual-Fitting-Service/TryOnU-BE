package tryonu.api.service.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tryonu.api.dto.responses.HealthCheckResponse;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 헬스체크 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HealthCheckServiceImpl implements HealthCheckService {
    
    private final LocalDateTime startTime = LocalDateTime.now();
    
    @Override
    public HealthCheckResponse checkHealth() {
        log.info("🟢 [HealthCheck] 애플리케이션 상태 확인 요청");
        
        LocalDateTime now = LocalDateTime.now();
        long uptimeSeconds = ChronoUnit.SECONDS.between(startTime, now);
        
        HealthCheckResponse response = new HealthCheckResponse(
            "UP",
            now,
            uptimeSeconds
        );
        
        log.info("✅ [HealthCheck] 상태 확인 완료 - status={}, uptime={}초", response.status(), response.uptime());
        
        return response;
    }
} 