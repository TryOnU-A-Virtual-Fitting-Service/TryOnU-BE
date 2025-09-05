package tryonu.api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tryonu.api.common.auth.UuidAuthenticationFilter;
import tryonu.api.common.auth.CustomAuthenticationEntryPoint;
import tryonu.api.common.auth.CustomAccessDeniedHandler;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 설정
 * 개발 환경에서 API 접근을 위한 보안 설정을 관리합니다.
 */
@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UuidAuthenticationFilter uuidAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final Environment env;
    
    @Value("${app.cors.allowed-origin-patterns}")
    private String allowedOriginPatterns;

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
            
            // CORS 설정 추가 (Swagger UI에서 API 호출을 위해)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
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
            .authorizeHttpRequests(authz -> {
                List<String> publicEndpoints = new java.util.ArrayList<>(List.of(
                    "/health",                // 헬스 체크 엔드포인트 (무조건 허용)
                    "/actuator/health",       // 스프링 액추에이터 헬스 체크 (무조건 허용)
                    "/swagger-ui/**",         // Swagger UI 리소스 (API 문서화용, 무조건 허용)
                    "/swagger-ui.html",       // Swagger UI 진입점 (무조건 허용)
                    "/api-docs/**",           // Swagger API 문서 엔드포인트 (무조건 허용)
                    "/v3/api-docs/**",        // OpenAPI 3.0 문서 엔드포인트 (무조건 허용)
                    "/user/init",            // 익명 사용자 초기화 API (회원가입/최초 진입, 무조건 허용)
                    "/setup",                 // 프론트엔드 애셋 로드 API (로고 등, 무조건 허용)
                    "/webhook/**",            // WebHook 엔드포인트 (외부 서비스 호출, 무조건 허용)
                    "/error",
                    "/setup/**"                 
                ));

                boolean isProd = Arrays.asList(env.getActiveProfiles()).contains("prod");
                if (!isProd) {
                    publicEndpoints.addAll(List.of(
                        "/monitoring/**",         // 모니터링 전용 API (개발/스테이징에서만 허용)
                        "/actuator/memory",       // 메모리 모니터링 엔드포인트
                        "/actuator/gc",           // GC 정보 엔드포인트
                        "/actuator/metrics/**"    // 메트릭스 엔드포인트
                    ));
                }

                authz
                    .requestMatchers(publicEndpoints.toArray(String[]::new)).permitAll()
                    .anyRequest().authenticated();
            })
            
            // 커스텀 인증 필터 추가 (권한 설정 후에 추가)
            .addFilterBefore(uuidAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 커스텀 예외 처리 핸들러 설정
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
            );
        
        log.info("✅ [SecurityConfig] Spring Security 설정 완료 - 헬스체크, Swagger UI, API 엔드포인트 인증 해제");
        
        return http.build();
    }

    /**
     * CORS 설정
     * 프론트엔드에서 API 호출을 위한 CORS 정책 설정
     * 
     * @return CorsConfigurationSource CORS 설정 정보
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("🌐 [SecurityConfig] CORS 설정 초기화");
        
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 오리진 설정 (설정 파일에서 주입받은 값 사용)
        if (allowedOriginPatterns != null && !allowedOriginPatterns.trim().isEmpty()) {
            String[] patterns = allowedOriginPatterns.split(",");
            List<String> cleanPatterns = Arrays.stream(patterns)
                .map(String::trim)
                .filter(pattern -> !pattern.isEmpty())
                .toList();
            
            configuration.setAllowedOriginPatterns(cleanPatterns);
            log.info("✅ [SecurityConfig] 허용된 오리진 패턴: {}", cleanPatterns);
        } else {
            // 기본값 설정 (개발 환경용)
            configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "https://localhost:*",
                "http://127.0.0.1:*",
                "https://127.0.0.1:*"
            ));
            log.warn("⚠️ [SecurityConfig] CORS 설정값이 없어 기본값 사용");
        }
        
        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));
        
        // 허용할 헤더
        configuration.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "X-UUID"  // 커스텀 인증 헤더
        ));
        
        // 노출할 헤더
        configuration.setExposedHeaders(List.of(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Access-Control-Allow-Methods",
            "Access-Control-Allow-Headers"
        ));
        
        // 인증 정보 포함 허용 (쿠키, Authorization 헤더 등)
        configuration.setAllowCredentials(true);
        
        // Preflight 요청 캐시 시간 (1시간)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        log.info("✅ [SecurityConfig] CORS 설정 완료 - 프론트엔드 API 호출 허용");
        log.info("🔍 [SecurityConfig] CORS 설정 상세: methods={}, headers={}, credentials={}, maxAge={}", 
                configuration.getAllowedMethods(), 
                configuration.getAllowedHeaders(), 
                configuration.getAllowCredentials(), 
                configuration.getMaxAge());
        
        return source;
    }
} 