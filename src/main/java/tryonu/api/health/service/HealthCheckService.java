package tryonu.api.health.service;

import tryonu.api.health.dto.responses.HealthCheckResponse;

/**
 * 헬스체크 서비스 인터페이스
 */
public interface HealthCheckService {
    
    /**
     * 애플리케이션 상태를 확인합니다.
     * 
     * @return 헬스체크 응답 정보
     */
    HealthCheckResponse checkHealth();
} 