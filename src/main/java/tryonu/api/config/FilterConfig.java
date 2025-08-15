package tryonu.api.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tryonu.api.common.filter.RequestBodyCachingFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RequestBodyCachingFilter> requestBodyCachingFilterRegistration(RequestBodyCachingFilter filter) {
        FilterRegistrationBean<RequestBodyCachingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(Integer.MIN_VALUE); // 최우선 실행
        registration.addUrlPatterns("/*");
        registration.setName("requestBodyCachingFilter");
        return registration;
    }
}




