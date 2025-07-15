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
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.domain.User;
import tryonu.api.service.user.UserService;

import java.io.IOException;
import java.util.Collections;

/**
 * DeviceId 기반 인증 필터
 * 헤더에서 deviceId를 추출하여 사용자 인증을 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceIdAuthenticationFilter extends OncePerRequestFilter {

    private static final String DEVICE_ID_HEADER = "X-Device-ID";
    private final UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                  @NonNull HttpServletResponse response, 
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String deviceId = extractDeviceId(request);
        
        if (StringUtils.hasText(deviceId)) {
            log.debug("[DeviceIdAuthenticationFilter] deviceId 추출: {}", deviceId);
            
            try {
                // deviceId로 사용자 조회
                var userOpt = userService.findByDeviceId(deviceId);
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    log.debug("[DeviceIdAuthenticationFilter] 사용자 인증 성공: userId={}, deviceId={}", 
                             user.getId(), deviceId);
                    
                    // SecurityContext에 인증 정보 설정
                    DeviceIdAuthenticationToken authentication = new DeviceIdAuthenticationToken(user, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                } else {
                    log.debug("[DeviceIdAuthenticationFilter] 사용자를 찾을 수 없음: deviceId={}", deviceId);
                    throw new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다. deviceId: " + deviceId);    
                }
                
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