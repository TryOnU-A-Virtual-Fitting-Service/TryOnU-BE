package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 기본 모델 정보 응답 DTO
 */
@Schema(description = "기본 모델 정보")
public record DefaultModelDto(
    
    @Schema(description = "기본 모델 ID", example = "5")
    Long defaultModelId,
    
    @Schema(description = "기본 모델 이미지 URL", example = "https://cdn.example.com/default-model.jpg")
    String defaultModelUrl,
    
    @Schema(description = "모델 이름 (남자 모델, 여자 모델, 커스텀 모델)", example = "남자 모델")
    String modelName,
    
    @Schema(description = "정렬 순서 (sortOrder 기준으로 정렬됨)", example = "1")
    Integer sortOrder,
    
    @Schema(description = "커스텀 모델 여부 (true: 사용자 업로드 모델, false: 기본 제공 모델)", example = "false")
    Boolean isCustom
    
) {} 