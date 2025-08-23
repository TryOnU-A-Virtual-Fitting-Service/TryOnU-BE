package tryonu.api.converter;

import org.springframework.stereotype.Component;
import tryonu.api.dto.requests.VirtualFittingRequest;
import tryonu.api.dto.responses.TryOnResponse;
import tryonu.api.domain.Cloth;
import tryonu.api.domain.TryOnResult;
import tryonu.api.common.enums.Category;
import tryonu.api.domain.User;


@Component
public class TryOnResultConverter {

    /**
     * VirtualFittingRequest 객체 생성
     */
    public VirtualFittingRequest toVirtualFittingRequest(String defaultModelImageUrl, String clothImageUrl) {
        return new VirtualFittingRequest(
                "tryon-v1.6",
                new VirtualFittingRequest.VirtualFittingInputs(
                        defaultModelImageUrl,  // model_image - 모델 이미지 URL
                        clothImageUrl,         // garment_image - 의류 이미지 URL 
                        null,                  // category - null (기본값 사용)
                        null,                  // mode - null (기본값 사용)
                        null,                  // garment_photo_type - null (기본값 사용)
                        null,                  // num_samples - null (기본값 사용)
                        null                   // seed - null (기본값 사용)
                )
        );
    }

    /**
     * Cloth 엔티티 생성
     */
    public Cloth toClothEntity(String clothImageUrl, String productPageUrl, Category category) {
        return Cloth.builder()
                .imageUrl(clothImageUrl)
                .category(category)
                .productPageUrl(productPageUrl)
                .build();
    }

    /**
     * TryOnResult 엔티티 생성 (modelUrl 직접 사용)
     */
    public TryOnResult toTryOnResultEntity(Cloth cloth, User user, String modelUrl, String resultImageUrl, String virtualFittingId) {
        return TryOnResult.builder()
                .user(user)
                .cloth(cloth)
                .modelUrl(modelUrl)
                .imageUrl(resultImageUrl)
                .virtualFittingId(virtualFittingId)
                .sizeAdvice(null)
                .build();
    }

    /**
     * TryOnResponse 생성
     */
    public TryOnResponse toTryOnResponse(TryOnResult tryOnResult) {
        return new TryOnResponse(tryOnResult.getId(), tryOnResult.getImageUrl());
    }
} 