package tryonu.api.common.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import tryonu.api.domain.User;
import tryonu.api.repository.user.UserRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;

/**
 * DeviceId 기반 인증 필터
 * 헤더에서 deviceId를 추출하여 사용자 인증을 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceIdAuthenticationFilter extends OncePerRequestFilter {

    private static final String DEVICE_ID_HEADER = "X-Device-ID";
    private final UserRepository userRepository;

        // ✅ 필터를 적용하지 않을 경로 목록을 여기에 정의합니다.
        private final List<AntPathRequestMatcher> excludedPaths = Arrays.asList(
            new AntPathRequestMatcher("/health"),
            new AntPathRequestMatcher("/actuator/health"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/swagger-ui.html"),
            new AntPathRequestMatcher("/api-docs/**"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/users/init"),
            new AntPathRequestMatcher("/error")
    );

    /**
     * ✅ 이 메서드를 오버라이드하여 특정 경로에서 필터가 동작하지 않도록 설정합니다.
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // 현재 요청 경로가 제외 목록에 포함되어 있으면 true를 반환하여 필터를 건너뜁니다.
        return excludedPaths.stream()
                .anyMatch(matcher -> matcher.matches(request));
    }


    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain
        ) throws ServletException, IOException {

        
        String deviceId = extractDeviceId(request);
        
        if (StringUtils.hasText(deviceId)) {
            log.debug("[DeviceIdAuthenticationFilter] deviceId 추출: {}", deviceId);
            
            try {
                // deviceId로 사용자 조회 (
                User user = userRepository.findByDeviceIdAndIsDeletedFalseOrThrow(deviceId);
                log.debug("[DeviceIdAuthenticationFilter] 사용자 인증 성공: userId={}, deviceId={}", 
                         user.getId(), deviceId);
                
                // SecurityContext에 인증 정보 설정
                DeviceIdAuthenticationToken authentication = new DeviceIdAuthenticationToken(user, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
            } catch (Exception e) {
                log.error("[DeviceIdAuthenticationFilter] 사용자 조회 중 오류 발생: deviceId={}, error={}", deviceId, e.getMessage(), e);
                throw e;
            }
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * 요청에서 deviceId를 추출합니다.
     * 
     * @param request HTTP 요청
     * @return deviceId (없으면 null)
     */
    private String extractDeviceId(HttpServletRequest request) {
        String deviceId = request.getHeader(DEVICE_ID_HEADER);
        
        // 헤더에 없으면 쿼리 파라미터에서 확인
        if (!StringUtils.hasText(deviceId)) {
            deviceId = request.getParameter("deviceId");
        }
        
        return deviceId;
    }

} 