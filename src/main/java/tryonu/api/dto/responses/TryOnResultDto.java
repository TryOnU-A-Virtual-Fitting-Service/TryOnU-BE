package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 피팅 결과 정보 응답 DTO
 */
@Schema(description = "피팅 결과 정보")
public record TryOnResultDto(
    
    @Schema(description = "피팅 결과 ID", example = "10")
    Long tryOnResultId,
    
    @Schema(description = "피팅 결과 이미지 URL", example = "https://example.com/try-on-result.jpg")
    String tryOnResultUrl
    
) {} 