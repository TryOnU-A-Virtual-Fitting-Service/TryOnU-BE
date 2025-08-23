package tryonu.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.service.defaultmodel.DefaultModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import tryonu.api.dto.responses.DefaultModelResponse;
import tryonu.api.dto.responses.DefaultModelDto;
import org.springframework.validation.annotation.Validated;
import tryonu.api.common.validation.NotEmptyFile;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "기본 모델 API", description = "사용자의 기본 모델 이미지 관리 API - 업로드 및 조회")
@Validated
@RequestMapping("/default-model")
@SecurityRequirement(name = "X-UUID")
public class DefaultModelController {

    private final DefaultModelService defaultModelService;

    /**
     * 기본 모델 업로드
     * 
     * @param file 업로드할 기본 모델 이미지 파일
     * @return 업로드된 기본 모델 정보
     */
    @Operation(
        summary = "기본 모델 업로드",
        description = "X-UUID 헤더에서 uuid를 추출하고, 업로드할 사진 파일을 받아 기본 모델 이미지를 등록합니다. " +
                     "업로드된 이미지는 자동으로 배경이 제거되고 S3에 저장됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "기본 모델 업로드 성공", 
                    content = @Content(schema = @Schema(implementation = DefaultModelResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 파일 업로드 실패"),
        @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자")
    })
    @PostMapping(value = "", consumes = "multipart/form-data")
    public ApiResponseWrapper<DefaultModelResponse> uploadDefaultModel(
            @NotEmptyFile @RequestParam("file") MultipartFile file
    ) {
        DefaultModelResponse response = defaultModelService.uploadDefaultModel(file);
        return ApiResponseWrapper.ofSuccess(response);
    }

    /**
     * 현재 사용자의 기본 모델 목록 조회
     * 
     * @return 현재 사용자의 기본 모델 목록
     */
    @Operation(
        summary = "기본 모델 목록 조회",
        description = "X-UUID 헤더를 통해 현재 인증된 사용자의 기본 모델 목록을 조회합니다. " +
                     "응답에는 사용자의 기본 모델 목록이 id 내림차순으로 정렬되어 포함됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "기본 모델 목록 조회 성공", 
                    content = @Content(schema = @Schema(implementation = DefaultModelDto.class))),
        @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자")
    })
    @GetMapping("")
    public ApiResponseWrapper<List<DefaultModelDto>> getCurrentUserDefaultModels() {
        List<DefaultModelDto> response = defaultModelService.getCurrentUserDefaultModels();
        return ApiResponseWrapper.ofSuccess(response);
    }

}
