package tryonu.api.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.common.wrapper.ApiResponseWrapper;

import java.io.IOException;

/**
 * 권한이 부족한 사용자가 리소스에 접근할 때 처리하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException {
        
        log.warn("[CustomAccessDeniedHandler] 권한 부족 접근 시도: path={}, method={}", 
                request.getRequestURI(), request.getMethod());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponseWrapper<Void> errorResponse = ApiResponseWrapper.ofFailure(
            ErrorCode.FORBIDDEN.getCode(),
            "해당 리소스에 접근할 권한이 없습니다."
        );

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
} 