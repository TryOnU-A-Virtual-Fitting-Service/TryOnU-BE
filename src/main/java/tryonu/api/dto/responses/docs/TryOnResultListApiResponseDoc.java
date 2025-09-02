package tryonu.api.dto.responses.docs;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.dto.responses.TryOnResultDto;

@Schema(description = "피팅 결과 목록 API 응답 예시용 문서 DTO")
public record TryOnResultListApiResponseDoc(
    @Schema(description = "요청 성공 여부", example = "true")
    boolean isSuccess,

    @ArraySchema(schema = @Schema(implementation = TryOnResultDto.class))
    List<TryOnResultDto> data,

    @Schema(description = "에러 정보", implementation = ApiResponseWrapper.ErrorResponse.class)
    ApiResponseWrapper.ErrorResponse error
) {}


