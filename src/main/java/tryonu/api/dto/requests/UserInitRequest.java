package tryonu.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 익명 사용자 초기화 요청 DTO
 */
@Schema(description = "익명 사용자 초기화 요청")
public record UserInitRequest(
    
    @Schema(description = "디바이스 ID", example = "device-12345")
    @NotBlank(message = "디바이스 ID는 필수입니다.")
    String deviceId
    
) {} 