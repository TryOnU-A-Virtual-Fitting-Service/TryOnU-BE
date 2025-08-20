package tryonu.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 익명 사용자 초기화 요청 DTO
 */
@Schema(description = "익명 사용자 초기화 요청")
public record UserInitRequest(
    
    @Schema(description = "uuid", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotBlank(message = "uuid는 필수입니다.")
    String uuid
    
) {} 