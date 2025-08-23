package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "가상 피팅 응답")
public record TryOnResponse(
    @Schema(description = "피팅 결과 ID", example = "1")
    Long tryOnResultId,

    @Schema(description = "피팅 결과 이미지 URL", example = "https://cdn.example.com/users/models/tryonresult-1.jpg")
    String tryOnResultImageUrl
) {} 