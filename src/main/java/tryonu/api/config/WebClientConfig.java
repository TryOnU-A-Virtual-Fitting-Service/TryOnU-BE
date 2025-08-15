package tryonu.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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
    
    // 기본 타임아웃 설정 (빠른 API용)
    @Value("${webclient.timeout.connect-timeout-ms}")
    private int connectTimeoutMs;
    
    @Value("${webclient.timeout.read-timeout-ms}")
    private int readTimeoutMs;
    
    @Value("${webclient.timeout.response-timeout-ms}")
    private int responseTimeoutMs;
    
    // 무거운 작업용 타임아웃 설정
    @Value("${webclient.heavy-timeout.connect-timeout-ms}")
    private int heavyConnectTimeoutMs;
    
    @Value("${webclient.heavy-timeout.read-timeout-ms}")
    private int heavyReadTimeoutMs;
    
    @Value("${webclient.heavy-timeout.response-timeout-ms}")
    private int heavyResponseTimeoutMs;
    
    // 커넥션 풀 설정
    @Value("${webclient.connection-pool.max-connections}")
    private int maxConnections;
    
    @Value("${webclient.connection-pool.pending-acquire-timeout-ms}")
    private int pendingAcquireTimeoutMs;
    
    @Value("${webclient.connection-pool.max-idle-time-ms}")
    private int maxIdleTimeMs;
    
    @Value("${webclient.connection-pool.max-life-time-ms}")
    private int maxLifeTimeMs;
    
    /**
     * 빠른 API용 HttpClient (이미지 다운로드, 카테고리 예측)
     */
    private HttpClient createFastHttpClient() {
        log.info("[WebClientConfig] 빠른 API용 WebClient 설정 - connectTimeout={}ms, readTimeout={}ms, responseTimeout={}ms", 
                connectTimeoutMs, readTimeoutMs, responseTimeoutMs);
                
        // 커넥션 풀 설정
        ConnectionProvider connectionProvider = ConnectionProvider.builder("fast-api-pool")
                .maxConnections(maxConnections)
                .pendingAcquireTimeout(Duration.ofMillis(pendingAcquireTimeoutMs))
                .maxIdleTime(Duration.ofMillis(maxIdleTimeMs))
                .maxLifeTime(Duration.ofMillis(maxLifeTimeMs))
                .build();
        
        return HttpClient.create(connectionProvider)
                .responseTimeout(Duration.ofMillis(responseTimeoutMs))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .doOnConnected(conn -> conn
                    .addHandlerLast(new ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
                );
    }

    /**
     * 무거운 작업용 HttpClient (가상피팅, 배경제거)
     */
    private HttpClient createHeavyHttpClient() {
        log.info("[WebClientConfig] 무거운 작업용 WebClient 설정 - connectTimeout={}ms, readTimeout={}ms, responseTimeout={}ms", 
                heavyConnectTimeoutMs, heavyReadTimeoutMs, heavyResponseTimeoutMs);
                
        // 커넥션 풀 설정 (무거운 작업용은 더 적은 커넥션으로 안정성 우선)
        ConnectionProvider connectionProvider = ConnectionProvider.builder("heavy-work-pool")
                .maxConnections(maxConnections / 2) // 절반으로 제한
                .pendingAcquireTimeout(Duration.ofMillis(pendingAcquireTimeoutMs))
                .maxIdleTime(Duration.ofMillis(maxIdleTimeMs))
                .maxLifeTime(Duration.ofMillis(maxLifeTimeMs))
                .build();
        
        return HttpClient.create(connectionProvider)
                .responseTimeout(Duration.ofMillis(heavyResponseTimeoutMs))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, heavyConnectTimeoutMs)
                .doOnConnected(conn -> conn
                    .addHandlerLast(new ReadTimeoutHandler(heavyReadTimeoutMs, TimeUnit.MILLISECONDS))
                );
    }
    
    /**
     * 가상피팅 API용 WebClient (무거운 작업용 설정)
     */
    @Bean
    public WebClient fittingWebClient() {
        return WebClient.builder()
                .baseUrl(fittingApiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(createHeavyHttpClient()))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySizeMb * 1024 * 1024))
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    /**
     * 배경 제거 API용 WebClient (무거운 작업용 설정)
     */
    @Bean
    public WebClient backgroundRemovalWebClient() {
        return WebClient.builder()
                .baseUrl(backgroundRemovalApiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(createHeavyHttpClient()))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySizeMb * 1024 * 1024))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.IMAGE_PNG_VALUE)
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    /**
     * 카테고리 예측 API용 WebClient (빠른 API 설정)
     */
    @Bean
    public WebClient categoryPredictionWebClient() {
        return WebClient.builder()
                .baseUrl(categoryPredictionApiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(createFastHttpClient()))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySizeMb * 1024 * 1024))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    /**
     * 이미지 다운로드 전용 WebClient (빠른 API 설정)
     */
    @Bean
    public WebClient imageDownloadWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(createFastHttpClient()))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySizeMb * 1024 * 1024))
                .defaultHeader(HttpHeaders.ACCEPT, "image/*")
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    /**
     * Slack Webhook 전송용 WebClient (가벼운 타임아웃)
     */
    @Bean(name = "slackWebClient")
    public WebClient slackWebClient() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(createFastHttpClient()))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
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