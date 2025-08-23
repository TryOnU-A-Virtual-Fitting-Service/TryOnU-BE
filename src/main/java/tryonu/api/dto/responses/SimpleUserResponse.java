package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 간단한 사용자 정보 응답 DTO
 */
@Schema(description = "간단한 사용자 정보")
public record SimpleUserResponse(
    
    @Schema(description = "사용자 ID", example = "1")
    Long userId,
    
    @Schema(description = "사용자 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    String uuid
    
) {}
