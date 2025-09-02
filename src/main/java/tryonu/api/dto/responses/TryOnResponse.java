package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "가상 피팅 응답")
public record TryOnResponse(
                @Schema(description = "피팅 작업 Job ID", example = "1c3f077f-ef18-4361-9ed0-4701904f3d90") 
                String tryOnJobId,

                @Schema(description = "피팅 결과 이미지 URL", example = "https://cdn.example.com/users/models/tryonresult-1.jpg") 
                String tryOnResultUrl,

                @Schema(description = "사용된 기본 모델 ID", example = "5") 
                Long defaultModelId,

                @Schema(description = "사용된 모델 이름", example = "슬림 한국인 남성") 
                String modelName) {
}