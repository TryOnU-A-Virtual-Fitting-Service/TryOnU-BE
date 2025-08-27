package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 기본 모델 업로드 응답 DTO
 */
@Schema(description = "기본 모델 업로드 응답")
public record DefaultModelResponse(
    
    @Schema(description = "기본 모델 ID", example = "12345")
    Long id,
    
    @Schema(description = "기본 모델 이미지 URL", example = "https://cdn.example.com/default-models/default-model-12345.jpg")
    String imageUrl,
    
    @Schema(description = "모델 이름 (업로드 시 '커스텀 모델'로 설정)", example = "커스텀 모델")
    String modelName,

    @Schema(description = "정렬 순서 (자동 할당됨)", example = "3")
    Integer sortOrder,
    
    @Schema(description = "커스텀 모델 여부 (true: 사용자 업로드 모델, false: 기본 제공 모델)", example = "true")
    Boolean isCustom
) {} 