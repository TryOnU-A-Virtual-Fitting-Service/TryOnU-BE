package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 피팅 모델 정보 응답 DTO
 */
@Schema(description = "피팅 모델 정보")
public record FittingModelDto(
    
    @Schema(description = "피팅 모델 ID", example = "10")
    Long fittingModelId,
    
    @Schema(description = "피팅 모델 이미지 URL", example = "https://example.com/fitting-model.jpg")
    String fittingModelUrl
    
) {} 