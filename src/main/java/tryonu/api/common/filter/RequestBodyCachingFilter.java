package tryonu.api.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * HTTP 요청 본문을 캐싱하는 필터 (성능 최적화 적용)
 * - JSON 타입만 캐싱
 * - 상한은 설정값 기반 (기본 64KB)
 * - 지연 로딩으로 문자열 변환
 */
@Component
@Order(1)
public class RequestBodyCachingFilter implements Filter {

    @Value("${monitoring.request-body.cache-limit-bytes:65536}")
    private int bodyCacheLimitBytes; // default 64KB

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            if (isCacheable(httpRequest)) {
                CachedBodyHttpServletRequest cached = new CachedBodyHttpServletRequest(httpRequest, bodyCacheLimitBytes);
                chain.doFilter(cached, response);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isCacheable(HttpServletRequest request) {
        String contentType = request.getContentType();
        int contentLength = request.getContentLength();

        boolean typeOk = contentType != null && contentType.toLowerCase(Locale.ROOT).contains("application/json");
        if (!typeOk) {
            return false;
        }

        // 길이 미상(-1)인 경우는 위험하므로 제외
        if (contentLength == -1 || contentLength > bodyCacheLimitBytes) {
            return false;
        }
        return true;
    }

    /**
     * 요청 본문을 캐싱하는 래퍼. 문자열 변환은 Supplier로 지연 처리한다.
     */
    static class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
        private final byte[] cachedBody;
        private String cachedBodyString;

        CachedBodyHttpServletRequest(HttpServletRequest request, int cacheLimitBytes) throws IOException {
            super(request);
            byte[] raw = StreamUtils.copyToByteArray(request.getInputStream());
            if (raw.length > cacheLimitBytes) {
                // 상한 적용
                cachedBody = java.util.Arrays.copyOf(raw, cacheLimitBytes);
            } else {
                cachedBody = raw;
            }
            // 문자열 형태 캐시도 함께 제공 (Publisher 호환)
            String bodyStr = new String(cachedBody, StandardCharsets.UTF_8);
            int max = 16 * 1024; // 16KB 전송 상한
            if (bodyStr.length() > max) {
                bodyStr = bodyStr.substring(0, max);
            }
            request.setAttribute("CACHED_REQUEST_BODY", bodyStr);
            // 지연 로딩 Supplier도 병행 제공
            request.setAttribute("getRequestBody", (Supplier<String>) this::getBodyAsString);
        }

        private String getBodyAsString() {
            if (cachedBodyString == null) {
                cachedBodyString = new String(cachedBody, StandardCharsets.UTF_8);
            }
            return cachedBodyString;
        }

        @Override
        public ServletInputStream getInputStream() {
            ByteArrayInputStream bais = new ByteArrayInputStream(cachedBody);
            return new ServletInputStream() {
                @Override
                public boolean isFinished() {
                    return bais.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    // not supported
                }

                @Override
                public int read() throws IOException {
                    return bais.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
    }
}