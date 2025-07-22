package tryonu.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.service.defaultmodel.DefaultModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import tryonu.api.dto.responses.DefaultModelResponse;
import org.springframework.validation.annotation.Validated;
import tryonu.api.common.validation.NotEmptyFile;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RequiredArgsConstructor
@RestController
@Tag(name = "기본 모델 API", description = "기본 모델 관련 API")
@Validated
@RequestMapping("/default-model")
@SecurityRequirement(name = "DeviceId")
public class DefaultModelController {

    private final DefaultModelService defaultModelService;

    @Operation(
        summary = "기본 모델 업로드",
        description = "X-Device-Id 헤더에서 deviceId를 추출하고, 업로드할 사진 파일을 받아 기본 모델 이미지를 등록합니다.",
        requestBody = @RequestBody(
            description = "업로드할 기본 모델 이미지 파일 (multipart/form-data)",
            required = true,
            content = @Content(schema = @Schema(type = "string", format = "binary"))
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "기본 모델 업로드 성공", content = @Content(schema = @Schema(implementation = DefaultModelResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 파일 업로드 실패")
    })
    @PostMapping(value = "", consumes = "multipart/form-data")
    public ApiResponseWrapper<DefaultModelResponse> uploadDefaultModel(
            @NotEmptyFile @RequestParam("file") MultipartFile file
    ) {
        DefaultModelResponse response = defaultModelService.uploadDefaultModel(file);
        return ApiResponseWrapper.ofSuccess(response);
    }

}
