package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 유저 정보 조회 응답 DTO
 */
@Schema(description = "유저 정보 조회 응답")
public record UserInfoResponse(

    @Schema(description = "기본 모델 리스트", example = "[{\"defaultModelId\": 5, \"defaultModelUrl\": \"https://example.com/default-model.jpg\"}]")
    List<DefaultModelDto> defaultModels,

    @Schema(description = "피팅 결과 리스트", example = "[{\"tryOnResultId\": 10, \"tryOnResultUrl\": \"https://example.com/try-on-result.jpg\"}]")
    List<TryOnResultDto> tryOnResults
    
) {} 