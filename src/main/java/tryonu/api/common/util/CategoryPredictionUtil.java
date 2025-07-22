package tryonu.api.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import tryonu.api.dto.responses.CategoryPredictionResponse;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryPredictionUtil {
    private final WebClient categoryPredictionWebClient;

    /**
     * 카테고리 예측 API 호출 (MultipartFile → CategoryPredictionResponse)
     */
    public CategoryPredictionResponse predictCategory(MultipartFile file) {
        try {
            return categoryPredictionWebClient.post()
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("file", file.getResource()))
                    .retrieve()
                    .bodyToMono(CategoryPredictionResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("[CategoryPredictionUtil] 카테고리 예측 실패 - fileName={}, error={}", 
                    file.getOriginalFilename(), e.getMessage(), e);
            throw new CustomException(ErrorCode.CATEGORY_PREDICTION_FAILED, "의류 카테고리를 인식할 수 없습니다.");
        }
    }

    /**
     * 카테고리 예측 API 호출 (URL에서 이미지 다운로드 → CategoryPredictionResponse)
     */
    public CategoryPredictionResponse predictCategory(String imageUrl) {
        try {
            // 1. URL에서 이미지 다운로드
            byte[] imageBytes = WebClient.create()
                    .get()
                    .uri(imageUrl)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
            
            if (imageBytes == null || imageBytes.length == 0) {
                throw new RuntimeException("URL에서 이미지를 다운로드할 수 없습니다: " + imageUrl);
            }
            
            // 2. 카테고리 예측 API 호출
            return categoryPredictionWebClient.post()
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("file", 
                            new org.springframework.core.io.ByteArrayResource(imageBytes) {
                                @Override
                                public String getFilename() {
                                    return "image.jpg";
                                }
                            }))
                    .retrieve()
                    .bodyToMono(CategoryPredictionResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("[CategoryPredictionUtil] URL 이미지 카테고리 예측 실패 - imageUrl={}, error={}", 
                    imageUrl, e.getMessage(), e);
            throw new CustomException(ErrorCode.CATEGORY_PREDICTION_FAILED, "의류 카테고리를 인식할 수 없습니다.");
        }
    }

    /**
     * 카테고리 예측 API 호출 (byte[] → CategoryPredictionResponse)
     */
    public CategoryPredictionResponse predictCategory(byte[] imageBytes) {
        try {
            if (imageBytes == null || imageBytes.length == 0) {
                throw new RuntimeException("이미지 바이트 배열이 비어있습니다.");
            }
            
            return categoryPredictionWebClient.post()
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("file", 
                            new org.springframework.core.io.ByteArrayResource(imageBytes) {
                                @Override
                                public String getFilename() {
                                    return "image.jpg";
                                }
                            }))
                    .retrieve()
                    .bodyToMono(CategoryPredictionResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("[CategoryPredictionUtil] byte[] 카테고리 예측 실패 - size={}bytes, error={}", 
                    imageBytes != null ? imageBytes.length : 0, e.getMessage(), e);
            throw new CustomException(ErrorCode.CATEGORY_PREDICTION_FAILED, "의류 카테고리를 인식할 수 없습니다.");
        }
    }
} 