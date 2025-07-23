package tryonu.api.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.dto.requests.VirtualFittingRequest;
import tryonu.api.dto.responses.VirtualFittingResponse;
import tryonu.api.dto.responses.VirtualFittingStatusResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class VirtualFittingUtil {
    private final WebClient fittingWebClient;
    
    @Value("${external.fashn-api.api-key}")
    private String apiKey;

    /**
     * 가상피팅 실행 요청 (WebHook URL 포함)
     */
    public VirtualFittingResponse runVirtualFitting(VirtualFittingRequest request, String webhookUrl) {
        log.info("[VirtualFittingUtil] 가상피팅 실행 요청 - modelName={}, webhookUrl={}", 
                request.modelName(), webhookUrl);
        
        try {
            String uri = UriComponentsBuilder.fromUriString("/run")
                    .queryParam("webhook_url", webhookUrl)
                    .build()
                    .toUriString();
            
            log.info("[VirtualFittingUtil] API 요청 상세 - URI: {}, apiKey존재: {}", 
                    uri, apiKey != null ? "YES" : "NO");
            
            return fittingWebClient.post()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), 
                        clientResponse -> {
                            log.error("[VirtualFittingUtil] FASHN API 4xx 오류 - Status: {}, Headers: {}", 
                                clientResponse.statusCode(), clientResponse.headers());
                            return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("[VirtualFittingUtil] FASHN API 오류 응답: {}", errorBody);
                                    return Mono.error(new RuntimeException("FASHN API 요청 실패: " + errorBody));
                                });
                        })
                    .onStatus(status -> status.is5xxServerError(),
                        clientResponse -> {
                            log.error("[VirtualFittingUtil] FASHN API 5xx 오류 - Status: {}", clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("[VirtualFittingUtil] FASHN API 서버 오류: {}", errorBody);
                                    return Mono.error(new RuntimeException("FASHN API 서버 오류: " + errorBody));
                                });
                        })
                    .bodyToMono(VirtualFittingResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("[VirtualFittingUtil] FASHN API WebClient 오류 - Status: {}, Body: {}", 
                e.getStatusCode(), e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.VIRTUAL_FITTING_API_ERROR, "가상피팅 API 호출에 실패했습니다: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("[VirtualFittingUtil] 가상피팅 실행 실패 - modelName={}, errorType={}, error={}", 
                    request.modelName(), e.getClass().getSimpleName(), e.getMessage(), e);
            throw new CustomException(ErrorCode.VIRTUAL_FITTING_API_ERROR, "가상피팅 API 호출에 실패했습니다.");
        }
    }

    /**
     * 가상피팅 실행 요청 (WebHook URL 없이, 폴링 방식용)
     */
    public VirtualFittingResponse runVirtualFitting(VirtualFittingRequest request) {
        log.info("[VirtualFittingUtil] 가상피팅 실행 요청 (폴링 방식) - modelName={}", 
                request.modelName());
        
        try {
            log.info("[VirtualFittingUtil] API 요청 상세 - URI: /run, apiKey존재: {}", 
                    apiKey != null ? "YES" : "NO");
            log.info("[VirtualFittingUtil] 전송할 request 객체: modelName={}, inputs.modelImage={}, inputs.garmentImage={}", 
                    request.modelName(), 
                    request.inputs() != null ? request.inputs().modelImage() : "null", 
                    request.inputs() != null ? request.inputs().garmentImage() : "null");
            
            return fittingWebClient.post()
                    .uri("/run")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), 
                        clientResponse -> {
                            log.error("[VirtualFittingUtil] FASHN API 4xx 오류 - Status: {}, Headers: {}", 
                                clientResponse.statusCode(), clientResponse.headers());
                            return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("[VirtualFittingUtil] FASHN API 오류 응답: {}", errorBody);
                                    return Mono.error(new RuntimeException("FASHN API 요청 실패: " + errorBody));
                                });
                        })
                    .onStatus(status -> status.is5xxServerError(),
                        clientResponse -> {
                            log.error("[VirtualFittingUtil] FASHN API 5xx 오류 - Status: {}", clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("[VirtualFittingUtil] FASHN API 서버 오류: {}", errorBody);
                                    return Mono.error(new RuntimeException("FASHN API 서버 오류: " + errorBody));
                                });
                        })
                    .bodyToMono(VirtualFittingResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("[VirtualFittingUtil] FASHN API WebClient 오류 - Status: {}, Body: {}", 
                e.getStatusCode(), e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.VIRTUAL_FITTING_API_ERROR, "가상피팅 API 호출에 실패했습니다: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("[VirtualFittingUtil] 가상피팅 실행 실패 - modelName={}, errorType={}, error={}", 
                    request.modelName(), e.getClass().getSimpleName(), e.getMessage(), e);
            throw new CustomException(ErrorCode.VIRTUAL_FITTING_API_ERROR, "가상피팅 API 호출에 실패했습니다.");
        }
    }

    /**
     * 가상피팅 상태 조회
     */
    public VirtualFittingStatusResponse getVirtualFittingStatus(String predictionId) {
        log.info("[VirtualFittingUtil] 가상피팅 상태 조회 - predictionId={}", predictionId);
        
        try {
            return fittingWebClient.get()
                    .uri("/status/{id}", predictionId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), 
                        clientResponse -> {
                            log.error("[VirtualFittingUtil] FASHN API 상태 확인 4xx 오류 - Status: {}", clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("[VirtualFittingUtil] FASHN API 상태 확인 오류 응답: {}", errorBody);
                                    return Mono.error(new RuntimeException("FASHN API 상태 확인 실패: " + errorBody));
                                });
                        })
                    .bodyToMono(VirtualFittingStatusResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("[VirtualFittingUtil] FASHN API 상태 확인 WebClient 오류 - Status: {}, Body: {}", 
                e.getStatusCode(), e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.VIRTUAL_FITTING_API_ERROR, "가상피팅 상태 조회에 실패했습니다: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("[VirtualFittingUtil] 가상피팅 상태 조회 실패 - predictionId={}, error={}", 
                    predictionId, e.getMessage(), e);
            throw new CustomException(ErrorCode.VIRTUAL_FITTING_API_ERROR, "가상피팅 상태 조회에 실패했습니다.");
        }
    }

    /**
     * 가상피팅 완료까지 폴링 방식으로 대기
     */
    public VirtualFittingStatusResponse waitForCompletion(String predictionId, long maxWaitTimeMs, long pollIntervalMs) {
        log.info("[VirtualFittingUtil] 가상피팅 완료 대기 시작 - predictionId={}, maxWait={}ms, interval={}ms", 
                predictionId, maxWaitTimeMs, pollIntervalMs);
        
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < maxWaitTimeMs) {
            VirtualFittingStatusResponse status = getVirtualFittingStatus(predictionId);
            
            log.debug("[VirtualFittingUtil] 가상피팅 상태 확인 - predictionId={}, status={}", 
                    predictionId, status.status());
            
            if ("completed".equals(status.status()) || "failed".equals(status.status())) {
                log.info("[VirtualFittingUtil] 가상피팅 완료 - predictionId={}, finalStatus={}", 
                        predictionId, status.status());
                return status;
            }
            
            try {
                Thread.sleep(pollIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CustomException(ErrorCode.VIRTUAL_FITTING_FAILED, "가상피팅 대기 중 인터럽트가 발생했습니다.");
            }
        }
        
        log.warn("[VirtualFittingUtil] 가상피팅 타임아웃 - predictionId={}, maxWait={}ms", 
                predictionId, maxWaitTimeMs);
        throw new CustomException(ErrorCode.VIRTUAL_FITTING_TIMEOUT, "가상피팅 처리 시간이 초과되었습니다.");
    }
} 