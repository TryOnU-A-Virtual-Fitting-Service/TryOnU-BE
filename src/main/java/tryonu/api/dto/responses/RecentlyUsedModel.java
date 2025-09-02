package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 최근 사용한 모델 정보 DTO
 */
@Schema(description = "최근 사용한 모델 정보")
public record RecentlyUsedModel(
    
    @Schema(description = "모델 ID", example = "1")
    Long defaultModelId,    

    @Schema(description = "모델 이미지 URL", example = "https://cdn.thatzfit.com/default/models/slim-korean-male.png")
    String defaultModelUrl,
    
    @Schema(description = "이미지 파일명", example = "slim-korean-male.png")
    String imageName,
    
    @Schema(description = "모델 이름", example = "커스텀 모델")
    String modelName
    
) {}
