package tryonu.api.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * 요청 본문을 캐싱하기 위한 필터.
 * 예외 발생 시에도 GlobalExceptionHandler에서 본문을 조회할 수 있도록 래핑한다.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RequestBodyCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = (request instanceof ContentCachingRequestWrapper)
                ? (ContentCachingRequestWrapper) request
                : new ContentCachingRequestWrapper(request);

        try {
            filterChain.doFilter(wrappedRequest, response);
        } finally {
            // 강제 캐시 로딩: 아직 읽히지 않은 경우 getContentAsByteArray() 호출로 내부 버퍼 초기화
            wrappedRequest.getContentAsByteArray();
        }
    }
}


