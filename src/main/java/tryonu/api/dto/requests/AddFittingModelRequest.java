package tryonu.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 피팅 모델 추가 요청 DTO
 */
@Schema(description = "피팅 모델 추가 요청")
public record AddFittingModelRequest(
    
    @Schema(description = "기본 모델 ID", example = "12345")
    @NotNull(message = "기본 모델 ID는 필수입니다.")
    Long defaultModelId
    
) {} 