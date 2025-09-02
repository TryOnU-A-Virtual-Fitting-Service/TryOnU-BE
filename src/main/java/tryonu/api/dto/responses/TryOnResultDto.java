package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 피팅 결과 정보 응답 DTO
 */
@Schema(description = "피팅 결과 정보")
public record TryOnResultDto(
        @Schema(description = "피팅 작업 Job ID", example = "1c3f077f-ef18-4361-9ed0-4701904f3d90") String tryOnJobId,

        @Schema(description = "피팅 결과 이미지 URL", example = "https://cdn.example.com/try-on-result.jpg") String tryOnResultUrl,

        @Schema(description = "사용된 기본 모델 ID", example = "5") Long defaultModelId,

        @Schema(description = "사용된 모델 이름", example = "슬림 한국인 남성") String modelName) {
}