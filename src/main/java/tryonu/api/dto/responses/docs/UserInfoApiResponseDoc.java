package tryonu.api.dto.responses.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.dto.responses.UserInfoResponse;

@Schema(description = "유저 기본 모델 및 피팅 결과 목록 API 응답 예시용 문서 DTO")
public record UserInfoApiResponseDoc(
    @Schema(description = "요청 성공 여부", example = "true")
    boolean isSuccess,

    @Schema(implementation = UserInfoResponse.class)
    UserInfoResponse data,

    @Schema(description = "에러 정보", implementation = ApiResponseWrapper.ErrorResponse.class)
    ApiResponseWrapper.ErrorResponse error
) {}


