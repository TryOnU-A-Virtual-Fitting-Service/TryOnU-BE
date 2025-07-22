package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "가상피팅 상태 응답")
public record VirtualFittingStatusResponse(
    @Schema(description = "예측 ID", example = "123a87r9-4129-4bb3-be18-9c9fb5bd7fc1-u1")
    String id,
    
    @Schema(description = "처리 상태", example = "completed", allowableValues = {"starting", "in_queue", "processing", "completed", "failed"})
    String status,
    
    @Schema(description = "결과 이미지 URL 목록")
    List<String> output,
    
    @Schema(description = "에러 정보")
    VirtualFittingError error
) {
    public record VirtualFittingError(
        @Schema(description = "에러명", example = "ImageLoadError")
        String name,
        
        @Schema(description = "에러 메시지", example = "Error loading model image")
        String message
    ) {}
} 