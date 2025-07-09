package tryonu.api.health.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tryonu.api.common.dto.ApiResponseWrapper;
import tryonu.api.health.dto.HealthCheckResponse;
import tryonu.api.health.service.HealthCheckService;

/**
 * 헬스체크 API 컨트롤러
 * 애플리케이션의 상태를 확인하는 엔드포인트를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Tag(name = "헬스체크 API", description = "애플리케이션 상태 확인 API")
public class HealthCheckController {
    
    private final HealthCheckService healthCheckService;
    
    /**
     * 애플리케이션 상태를 확인합니다.
     * 
     * @return 애플리케이션 상태 정보
     */
    @GetMapping
    @Operation(
        summary = "애플리케이션 상태 확인",
        description = "가상피팅 애플리케이션의 현재 상태, 버전, 가동 시간 등의 정보를 반환합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "상태 확인 성공",
            content = @Content(
                schema = @Schema(implementation = HealthCheckResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 내부 오류"
        )
    })
    public ApiResponseWrapper<HealthCheckResponse> checkHealth() {
        log.info("🏥 [HealthCheckController] 헬스체크 요청 수신");
        
        HealthCheckResponse healthInfo = healthCheckService.checkHealth();
        
        log.info("💚 [HealthCheckController] 헬스체크 응답 완료 - status={}", 
                healthInfo.status());
        
        return ApiResponseWrapper.ofSuccess(healthInfo);
    }
} 