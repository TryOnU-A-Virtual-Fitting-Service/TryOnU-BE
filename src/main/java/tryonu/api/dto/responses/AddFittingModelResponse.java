package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 피팅 모델 추가 응답 DTO
 */
@Schema(description = "피팅 모델 추가 응답")
public record AddFittingModelResponse(
    
    @Schema(description = "추가된 피팅 모델 ID", example = "12345")
    Long fittingModelId,
    
    @Schema(description = "피팅 모델 URL", example = "https://storage.example.com/fitting-models/fitting-model-12345.jpg")
    String fittingModelImageUrl
    
) {} 