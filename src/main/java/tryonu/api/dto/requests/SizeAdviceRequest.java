package tryonu.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "SizeAdviceRequest", description = "사이즈 조언 저장 요청 DTO")
public record SizeAdviceRequest(
        @Schema(description = "가상피팅 작업 ID", example = "1c3f077f-ef18-4361-9ed0-4701904f3d90") 
        @NotBlank String tryOnJobId,

        @Schema(description = "사이즈 정보", example = "{S~~ M~~ L~~ XL~~ XXL~~ XXXL~~ 상세페이지 긁어서 주면됨}") 
        @NotBlank String sizeInfo
        ) {
}