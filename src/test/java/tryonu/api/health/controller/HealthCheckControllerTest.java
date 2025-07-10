package tryonu.api.health.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.controller.HealthCheckController;
import tryonu.api.dto.responses.HealthCheckResponse;
import tryonu.api.service.health.HealthCheckService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * 헬스체크 컨트롤러 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class HealthCheckControllerTest {
    
    @Mock
    private HealthCheckService healthCheckService;
    
    @InjectMocks
    private HealthCheckController healthCheckController;
    
    private HealthCheckResponse mockHealthResponse;
    
    @BeforeEach
    void setUp() {
        mockHealthResponse = new HealthCheckResponse(
            "UP",
            LocalDateTime.now(),
            3600L
        );
    }
    
    @Test
    @DisplayName("헬스체크 API가 정상적으로 상태 정보를 반환한다")
    void checkHealth_ShouldReturnHealthInfo() {
        // given
        when(healthCheckService.checkHealth()).thenReturn(mockHealthResponse);
        
        // when
        ApiResponseWrapper<HealthCheckResponse> response = healthCheckController.checkHealth();
        
        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.data()).isNotNull();
        assertThat(response.data().status()).isEqualTo("UP");
        assertThat(response.data().uptime()).isEqualTo(3600L);
        assertThat(response.error()).isNull();
    }
} 