package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 간단한 사용자 정보 응답 DTO
 */
@Schema(description = "간단한 사용자 정보")
public record SimpleUserResponse(
    
    @Schema(description = "최근 사용한 모델 정보")
    RecentlyUsedModel recentlyUsedModel
    
) {}
