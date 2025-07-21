package tryonu.api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tryonu.api.common.auth.DeviceIdAuthenticationFilter;
import tryonu.api.common.auth.CustomAuthenticationEntryPoint;
import tryonu.api.common.auth.CustomAccessDeniedHandler;

/**
 * Spring Security 설정
 * 개발 환경에서 API 접근을 위한 보안 설정을 관리합니다.
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final DeviceIdAuthenticationFilter deviceIdAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * Security Filter Chain 설정
     * 개발 환경에서는 헬스체크, Swagger UI, API 엔드포인트에 인증 없이 접근 가능하도록 설정
     * 
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("🔐 [SecurityConfig] Spring Security 설정 초기화");
        
        http
            // CSRF 비활성화 (API 서버이므로)
            .csrf(AbstractHttpConfigurer::disable)
            
            // HTTP Basic 인증 비활성화
            .httpBasic(AbstractHttpConfigurer::disable)
            
            // 폼 로그인 비활성화
            .formLogin(AbstractHttpConfigurer::disable)
            
            // 로그아웃 비활성화
            .logout(AbstractHttpConfigurer::disable)
            
            // 세션 정책 설정 (STATELESS)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 요청별 권한 설정
            .authorizeHttpRequests(authz -> authz
                // 인증 없이 접근 가능한 엔드포인트 통합 설정
                .requestMatchers(
                    "/health",                // 헬스 체크 엔드포인트 (무조건 허용)
                    "/actuator/health",       // 스프링 액추에이터 헬스 체크 (무조건 허용)
                    "/swagger-ui/**",         // Swagger UI 리소스 (API 문서화용, 무조건 허용)
                    "/swagger-ui.html",       // Swagger UI 진입점 (무조건 허용)
                    "/api-docs/**",           // Swagger API 문서 엔드포인트 (무조건 허용)
                    "/v3/api-docs/**",        // OpenAPI 3.0 문서 엔드포인트 (무조건 허용)
                    "/users/init",            // 익명 사용자 초기화 API (회원가입/최초 진입, 무조건 허용)
                    "/error"                  // 에러 페이지 (무조건 허용)
                ).permitAll()
                
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            
            // 커스텀 인증 필터 추가 (권한 설정 후에 추가)
            .addFilterBefore(deviceIdAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 커스텀 예외 처리 핸들러 설정
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
            );
        
        log.info("✅ [SecurityConfig] Spring Security 설정 완료 - 헬스체크, Swagger UI, API 엔드포인트 인증 해제");
        
        return http.build();
    }
} 