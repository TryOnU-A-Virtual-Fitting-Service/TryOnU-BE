package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 유저 정보 조회 응답 DTO
 */
@Schema(description = "유저 정보 조회 응답")
public record UserInfoResponse(
    
    @Schema(description = "피팅 모델 리스트", example = "[{\"fittingModelId\": 10, \"fittingModelUrl\": \"https://example.com/fitting-model.jpg\"}]")
    List<FittingModelDto> fittingModels,
    
    @Schema(description = "기본 모델 리스트", example = "[{\"defaultModelId\": 5, \"defaultModelUrl\": \"https://example.com/default-model.jpg\"}]")
    List<DefaultModelDto> defaultModels
    
) {} 