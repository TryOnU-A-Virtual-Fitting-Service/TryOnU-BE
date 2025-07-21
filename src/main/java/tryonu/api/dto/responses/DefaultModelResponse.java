package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 기본 모델 업로드 응답 DTO
 */
@Schema(description = "기본 모델 업로드 응답")
public record DefaultModelResponse(
    @Schema(description = "업로드된 기본 모델 ID", example = "12345")
    Long defaultModelId,

    @Schema(description = "기본 모델 이미지 URL", example = "https://storage.example.com/default-models/default-model-12345.jpg")
    String defaultModelImageUrl
) {} 