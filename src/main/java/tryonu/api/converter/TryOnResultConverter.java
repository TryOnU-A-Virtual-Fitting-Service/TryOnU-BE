package tryonu.api.converter;

import org.springframework.stereotype.Component;
import tryonu.api.dto.requests.VirtualFittingRequest;
import tryonu.api.dto.responses.TryOnResponse;
import tryonu.api.domain.Cloth;
import tryonu.api.domain.TryOnResult;
import tryonu.api.common.enums.Category;
import tryonu.api.domain.DefaultModel;

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
     * TryOnResult 엔티티 생성
     */
    public TryOnResult toTryOnResultEntity(Cloth cloth, DefaultModel defaultModel, String backgroundRemovedImageUrl, String virtualFittingId) {
        return TryOnResult.builder()
                .user(defaultModel.getUser())  // DefaultModel에서 User 가져오기
                .cloth(cloth)
                .defaultModel(defaultModel)
                .imageUrl(backgroundRemovedImageUrl)
                .virtualFittingId(virtualFittingId)  // 가상 피팅 API 응답 ID
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