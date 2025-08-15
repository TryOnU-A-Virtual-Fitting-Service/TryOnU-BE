package tryonu.api.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.common.event.ApiErrorPublisher;

import java.io.IOException;

/**
 * 인증되지 않은 사용자가 보호된 리소스에 접근할 때 처리하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final ApiErrorPublisher apiErrorPublisher;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        
        log.warn("[CustomAuthenticationEntryPoint] 인증되지 않은 접근 시도: path={}, method={}", 
                request.getRequestURI(), request.getMethod());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");


        ApiResponseWrapper<Void> errorResponse = ApiResponseWrapper.ofFailure(
            ErrorCode.UNAUTHORIZED.getCode(),
            ErrorCode.UNAUTHORIZED.getMessage()
        );

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);

        // Publish error event for Slack/monitoring
        apiErrorPublisher.publish(request, HttpServletResponse.SC_UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getCode(), ErrorCode.UNAUTHORIZED.getMessage());
    }
} 