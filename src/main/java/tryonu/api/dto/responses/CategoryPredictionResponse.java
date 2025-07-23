package tryonu.api.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 예측 응답")
public record CategoryPredictionResponse(
    @JsonProperty("class_idx")
    @Schema(description = "예측된 클래스 인덱스", example = "0")
    Integer classIdx,
    
    @JsonProperty("class_name")
    @Schema(description = "예측된 클래스명", example = "shirt")
    String className,
    
    @Schema(description = "예측 신뢰도", example = "0.95")
    Double confidence
) {} 