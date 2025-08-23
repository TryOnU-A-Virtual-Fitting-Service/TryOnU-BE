package tryonu.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import tryonu.api.common.enums.BatchUpdateStatus;

/**
 * 개별 기본 모델 수정 요청 DTO
 */
@Schema(description = "개별 기본 모델 수정 요청")
public record DefaultModelUpdateItemRequest(
    
    @Schema(description = "기본 모델 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "기본 모델 ID는 필수입니다.")
    @Positive(message = "기본 모델 ID는 양수여야 합니다.")
    Long id,
    
    @Schema(description = "모델 이름 (UPDATE 시에만 필요, DELETE 시에는 무시됨)", example = "내 커스텀 모델")
    String modelName,
    
    @Schema(description = "모델 정렬 순서 (UPDATE 시에만 필요, DELETE 시에는 무시됨)", example = "1")
    @Positive(message = "정렬 순서는 양수여야 합니다.")
    Integer sortOrder,
    
    @Schema(description = "작업 상태 - UPDATE: 모델 정보 변경, DELETE: 모델 삭제", example = "UPDATE", allowableValues = {"UPDATE", "DELETE"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "작업 상태는 필수입니다.")
    BatchUpdateStatus status
    
) {}
