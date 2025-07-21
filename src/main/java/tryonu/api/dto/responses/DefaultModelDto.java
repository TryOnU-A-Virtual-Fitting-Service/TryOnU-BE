package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 기본 모델 정보 응답 DTO
 */
@Schema(description = "기본 모델 정보")
public record DefaultModelDto(
    
    @Schema(description = "기본 모델 ID", example = "5")
    Long defaultModelId,
    
    @Schema(description = "기본 모델 이미지 URL", example = "https://example.com/default-model.jpg")
    String defaultModelUrl
    
) {} 