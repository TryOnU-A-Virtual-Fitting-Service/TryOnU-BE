package tryonu.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 기본 모델 일괄 수정 요청 DTO
 */
@Schema(description = "기본 모델 일괄 수정 요청", example = """
    {
      "defaultModels": [
        { 
          "id": 1, 
          "modelName": "내 커스텀 모델", 
          "sortOrder": 3, 
          "status": "UPDATE" 
        },
        { 
          "id": 2, 
          "modelName": null, 
          "sortOrder": 1, 
          "status": "UPDATE" 
        },
        { 
          "id": 3, 
          "status": "DELETE" 
        }
      ]
    }
    """)
public record DefaultModelBatchUpdateRequest(
    
    @Schema(description = "수정할 기본 모델 목록 - 정렬 순서 변경 및 삭제 작업을 포함", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "수정할 기본 모델 목록은 비어있을 수 없습니다.")
    @Valid
    List<DefaultModelUpdateItemRequest> defaultModels
    
) {}
