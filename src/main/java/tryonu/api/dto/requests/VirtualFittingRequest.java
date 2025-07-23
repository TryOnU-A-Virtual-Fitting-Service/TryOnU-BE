package tryonu.api.dto.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "가상피팅 요청")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record VirtualFittingRequest(
    @JsonProperty("model_name")
    @Schema(description = "모델 버전", example = "tryon-v1.6", allowableValues = {"tryon-v1.6", "tryon-v1.5"})
    String modelName,
    
    @Schema(description = "입력 파라미터")
    VirtualFittingInputs inputs
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record VirtualFittingInputs(
        @JsonProperty("model_image")
        @Schema(description = "모델 이미지 URL", example = "https://example.com/model.jpg")
        String modelImage,
        
        @JsonProperty("garment_image")
        @Schema(description = "의류 이미지 URL", example = "https://example.com/garment.jpg")
        String garmentImage,
        
        @Schema(description = "의류 카테고리", example = "auto", allowableValues = {"auto", "tops", "bottoms", "one-pieces"})
        String category,
        
        @Schema(description = "처리 모드", example = "balanced", allowableValues = {"performance", "balanced", "quality"})
        String mode,
        
        @JsonProperty("garment_photo_type")
        @Schema(description = "의류 사진 타입", example = "auto", allowableValues = {"auto", "model", "flat-lay"})
        String garmentPhotoType,
        
        @JsonProperty("num_samples")
        @Schema(description = "생성할 샘플 수", example = "1")
        Integer numSamples,
        
        @Schema(description = "시드값", example = "42")
        Integer seed
    ) {}
} 