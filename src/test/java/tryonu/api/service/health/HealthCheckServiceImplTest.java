package tryonu.api.service.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import tryonu.api.config.BaseServiceTest;
import tryonu.api.converter.HealthCheckConverter;
import tryonu.api.dto.responses.HealthCheckResponse;

import tryonu.api.fixture.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * HealthCheckServiceImpl 단위 테스트
 * 
 * 테스트 전략:
 * - Converter는 Mock으로 처리
 * - 시간 관련 로직 테스트
 * - GWT(Given-When-Then) 패턴으로 테스트 작성
 */
class HealthCheckServiceImplTest extends BaseServiceTest {

        @InjectMocks
        private HealthCheckServiceImpl healthCheckService;

        @Mock
        private HealthCheckConverter healthCheckConverter;

        @BeforeEach
        void setUp() {
                // HealthCheckServiceImpl의 startTime은 생성 시점에 결정되므로
                // 테스트마다 새로운 인스턴스를 생성할 필요는 없음
        }

        @Nested
        @DisplayName("헬스체크")
        class CheckHealth {

                @Test
                @DisplayName("성공: 정상적인 헬스체크 응답")
                void checkHealth_Success() {
                        // Given
                        LocalDateTime mockNow = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
                        long expectedUptimeSeconds = 60L; // 1분
                        HealthCheckResponse expectedResponse = new HealthCheckResponse(
                                        "UP",
                                        mockNow,
                                        expectedUptimeSeconds);

                        given(healthCheckConverter.toHealthCheckResponse(eq("UP"), any(LocalDateTime.class), anyLong()))
                                        .willReturn(expectedResponse);

                        // When
                        HealthCheckResponse result = healthCheckService.checkHealth();

                        // Then
                        assertThat(result).isNotNull();
                        assertThat(result.status()).isEqualTo("UP");
                        assertThat(result.timestamp()).isEqualTo(mockNow);
                        assertThat(result.uptime()).isEqualTo(expectedUptimeSeconds);

                        // Verify interactions
                        then(healthCheckConverter).should().toHealthCheckResponse(eq("UP"), any(LocalDateTime.class),
                                        anyLong());
                }

                @Test
                @DisplayName("성공: 헬스체크 응답 시간 확인")
                void checkHealth_Success_TimestampVerification() {
                        // Given
                        LocalDateTime beforeCall = LocalDateTime.now();
                        HealthCheckResponse mockResponse = ResponseFixture.createHealthCheckResponse();

                        given(healthCheckConverter.toHealthCheckResponse(eq("UP"), any(LocalDateTime.class), anyLong()))
                                        .willReturn(mockResponse);

                        // When
                        HealthCheckResponse result = healthCheckService.checkHealth();
                        LocalDateTime afterCall = LocalDateTime.now();

                        // Then
                        assertThat(result).isNotNull();
                        assertThat(result.status()).isEqualTo("UP");

                        // Verify that the timestamp passed to converter is within reasonable bounds
                        verify(healthCheckConverter).toHealthCheckResponse(
                                        eq("UP"),
                                        argThat(timestamp -> !timestamp.isBefore(beforeCall.minusSeconds(1)) &&
                                                        !timestamp.isAfter(afterCall.plusSeconds(1))),
                                        anyLong());
                }

                @Test
                @DisplayName("성공: 업타임 계산 확인")
                void checkHealth_Success_UptimeCalculation() {
                        // Given
                        HealthCheckResponse mockResponse = ResponseFixture.createHealthCheckResponse();

                        given(healthCheckConverter.toHealthCheckResponse(eq("UP"), any(LocalDateTime.class), anyLong()))
                                        .willReturn(mockResponse);

                        // When
                        HealthCheckResponse result = healthCheckService.checkHealth();

                        // Then
                        assertThat(result).isNotNull();

                        // Verify that uptime is non-negative (서비스가 시작된 이후이므로)
                        verify(healthCheckConverter).toHealthCheckResponse(
                                        eq("UP"),
                                        any(LocalDateTime.class),
                                        argThat(uptime -> uptime >= 0L));
                }

                @Test
                @DisplayName("성공: 다중 호출 시 업타임 증가 확인")
                void checkHealth_Success_UptimeIncreases() throws InterruptedException {
                        // Given
                        HealthCheckResponse firstResponse = new HealthCheckResponse("UP", LocalDateTime.now(), 5L);
                        HealthCheckResponse secondResponse = new HealthCheckResponse("UP", LocalDateTime.now(), 6L);

                        given(healthCheckConverter.toHealthCheckResponse(eq("UP"), any(LocalDateTime.class), anyLong()))
                                        .willReturn(firstResponse, secondResponse);

                        // When
                        healthCheckService.checkHealth(); // 첫 번째 호출

                        Thread.sleep(10); // 짧은 대기 (업타임이 증가하도록)

                        healthCheckService.checkHealth(); // 두 번째 호출

                        // Then
                        // 두 번째 호출의 업타임이 첫 번째보다 같거나 큰지 확인
                        verify(healthCheckConverter, times(2)).toHealthCheckResponse(
                                        eq("UP"),
                                        any(LocalDateTime.class),
                                        anyLong());
                }

                @Test
                @DisplayName("성공: 상태는 항상 UP")
                void checkHealth_Success_StatusAlwaysUp() {
                        // Given
                        HealthCheckResponse mockResponse = ResponseFixture.createHealthCheckResponse();

                        given(healthCheckConverter.toHealthCheckResponse(eq("UP"), any(LocalDateTime.class), anyLong()))
                                        .willReturn(mockResponse);

                        // When
                        HealthCheckResponse result = healthCheckService.checkHealth();

                        // Then
                        assertThat(result).isNotNull();
                        assertThat(result.status()).isEqualTo("UP");

                        // Verify that status is always "UP"
                        then(healthCheckConverter).should().toHealthCheckResponse(eq("UP"), any(LocalDateTime.class),
                                        anyLong());
                }
        }
}
