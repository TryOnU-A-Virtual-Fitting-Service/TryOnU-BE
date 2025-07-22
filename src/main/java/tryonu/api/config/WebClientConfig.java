package tryonu.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 외부 API 연동(WebClient) 목적별 Bean 등록
 * - 가상피팅
 * - 배경 제거
 */
@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${external.fitting-api.base-url}")
    private String fittingApiBaseUrl;

    @Value("${external.background-removal-api.base-url}")
    private String backgroundRemovalApiBaseUrl;

    @Value("${external.category-prediction-api.base-url}")
    private String categoryPredictionApiBaseUrl;
    
    @Value("${webclient.max-in-memory-size-mb}")
    private int maxInMemorySizeMb;
    
    /**
     * 가상피팅 API용 WebClient
     */
    @Bean
    public WebClient fittingWebClient() {
        return WebClient.builder()
                .baseUrl(fittingApiBaseUrl)
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    /**
     * 배경 제거 API용 WebClient
     */
    @Bean
    public WebClient backgroundRemovalWebClient() {
        return WebClient.builder()
                .baseUrl(backgroundRemovalApiBaseUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySizeMb * 1024 * 1024))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.IMAGE_PNG_VALUE)
                .build();
    }

    /**
     * 카테고리 예측 API용 WebClient
     */
    @Bean
    public WebClient categoryPredictionWebClient() {
        return WebClient.builder()
                .baseUrl(categoryPredictionApiBaseUrl)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySizeMb * 1024 * 1024))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * 이미지 다운로드 전용 WebClient
     */
    @Bean
    public WebClient imageDownloadWebClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySizeMb * 1024 * 1024))
                .defaultHeader(HttpHeaders.ACCEPT, "image/*")
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.debug("Request: {} {}", request.method(), request.url());
            return Mono.just(request);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.debug("Response status: {}", response.statusCode());
            return Mono.just(response);
        });
    }
} 