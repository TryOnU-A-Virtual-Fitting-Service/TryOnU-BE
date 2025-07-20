package tryonu.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.dto.requests.AddFittingModelRequest;
import tryonu.api.dto.responses.AddFittingModelResponse;
import tryonu.api.service.fittingmodel.FittingModelService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fitting-model")
@Tag(name = "피팅 모델 API", description = "피팅 모델 관련 API")
public class FiitngModelController {

    private final FittingModelService fittingModelService;


    @PostMapping("")
    @Operation(
        summary = "피팅 모델 추가",
        description = "기본 모델을 기반으로 새로운 피팅 모델을 추가합니다. 추가된 모델의 ID와 URL을 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "피팅 모델 추가 성공",
            content = @Content(schema = @Schema(implementation = AddFittingModelResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 (기본 모델 ID 누락 또는 유효하지 않음)"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "기본 모델을 찾을 수 없음"
        )
    })
    public ApiResponseWrapper<AddFittingModelResponse> addFittingModel(
            @Valid @RequestBody AddFittingModelRequest request
        ) {
        // 모델 리스트에 모델 추가
        AddFittingModelResponse response = fittingModelService.addFittingModel(request.defaultModelId());
        return ApiResponseWrapper.ofSuccess(response);
    }


    
    
}
