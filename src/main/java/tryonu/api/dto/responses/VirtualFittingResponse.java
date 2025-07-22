package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "가상피팅 응답")
public record VirtualFittingResponse(
    @Schema(description = "예측 ID", example = "123a87r9-4129-4bb3-be18-9c9fb5bd7fc1-u1")
    String id,
    
    @Schema(description = "에러 정보")
    String error
) {} 