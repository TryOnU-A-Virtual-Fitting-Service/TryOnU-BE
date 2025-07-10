package tryonu.api.health.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 헬스체크 API 응답 DTO
 * 
 * @param status 애플리케이션 상태
 * @param timestamp 체크 시간
 * @param uptime 애플리케이션 가동 시간 (초)
 */
@Schema(description = "헬스체크 응답 정보")
public record HealthCheckResponse(
    @Schema(description = "애플리케이션 상태", example = "UP")
    String status,
    
    @Schema(description = "체크 시간", example = "2024-01-15T10:30:00")
    LocalDateTime timestamp,
    
    @Schema(description = "애플리케이션 가동 시간 (초)", example = "3600")
    long uptime
) {} 