package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "가상 피팅 작업 생성 응답")
public record TryOnJobInitResponse(
    @Schema(description = "생성된 가상 피팅 작업 ID", example = "1c3f077f-ef18-4361-9ed0-4701904f3d90")
    String tryOnJobId
) {
}
