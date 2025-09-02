package tryonu.api.dto.responses;

/**
 * 사이즈 조언 응답 DTO
 */
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SizeAdviceResponse", description = "사이즈 조언 저장 응답 DTO")
public record SizeAdviceResponse(
        @Schema(description = "가상피팅 작업 ID", example = "1c3f077f-ef18-4361-9ed0-4701904f3d90") 
        String tryOnJobId,

        @Schema(description = "사이즈 조언 내용", example = "M 사이즈를 추천합니다. 어깨와 가슴 핏이 적절합니다.") 
        String advice
        ) {
}