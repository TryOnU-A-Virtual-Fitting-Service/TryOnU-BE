package tryonu.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;

/**
 * 가상 피팅 요청 DTO
 */
@Schema(description = "가상 피팅 요청")
public record TryOnRequestDto(
        @Schema(description = "가상 피팅 작업 ID", example = "1234567890", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "가상 피팅 작업 ID는 필수입니다") String tryOnJobId,

        @Schema(description = "모델 이미지 URL", example = "https://cdn.example.com/model.jpg", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "모델 이미지 URL은 필수입니다") @URL(message = "올바른 URL 형식이어야 합니다") String modelUrl,

        @Schema(description = "기본 모델 ID", example = "5", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull(message = "기본 모델 ID는 필수입니다") @Positive(message = "기본 모델 ID는 양수여야 합니다") Long defaultModelId,

        @Schema(description = "상품 상세 페이지 URL (선택)", example = "https://cdn.example.com/product/123") @URL(message = "올바른 URL 형식이어야 합니다") String productPageUrl

) {
}
