package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "가상 피팅 응답")
public record TryOnResponse(
    @Schema(description = "업데이트된 피팅 모델 이미지 URL", example = "https://cdn.thatzfit.com/users/models/updated-model.jpg")
    String fittingModelImageUrl
) {} 