package tryonu.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.service.defaultmodel.DefaultModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import tryonu.api.dto.requests.DefaultModelBatchUpdateRequest;
import tryonu.api.dto.responses.DefaultModelResponse;
import tryonu.api.dto.responses.DefaultModelDto;
import org.springframework.validation.annotation.Validated;
import tryonu.api.common.validation.NotEmptyFile;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;


import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "기본 모델 API", description = "사용자의 기본 모델 이미지 관리 API - 업로드, 조회, 정렬 순서 변경 및 삭제")
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
                     "업로드된 이미지는 자동으로 배경이 제거되고 S3에 저장됩니다. " +
                     "모델 이름은 '커스텀 모델'로 설정되며, 정렬 순서는 자동으로 다음 순서가 할당됩니다."
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
                     "응답에는 사용자의 기본 모델 목록이 sortOrder 오름차순, id 오름차순으로 정렬되어 포함됩니다. " +
                     "기본 모델(남자 모델, 여자 모델)과 커스텀 모델이 모두 표시됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "기본 모델 목록 조회 성공", 
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DefaultModelDto.class)))),
        @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자")
    })
    @GetMapping("/list")
    public ApiResponseWrapper<List<DefaultModelDto>> getCurrentUserDefaultModels() {
        List<DefaultModelDto> response = defaultModelService.getCurrentUserDefaultModels();
        return ApiResponseWrapper.ofSuccess(response);
    }

    /**
     * 기본 모델 일괄 수정 (정렬 순서 변경, 삭제)
     * 
     * @param request 일괄 수정 요청
     * @return 204 No Content
     */
    @Operation(
        summary = "기본 모델 일괄 수정",
        description = "X-UUID 헤더를 통해 현재 인증된 사용자의 기본 모델들을 일괄 수정합니다. " +
                     "모델 정보 변경(UPDATE) 또는 삭제(DELETE) 작업을 수행할 수 있습니다. " +
                     "UPDATE 작업 시에는 modelName과 sortOrder를 선택적으로 변경할 수 있으며, null인 필드는 기존 값이 유지됩니다. " +
                     "DELETE 작업 시에는 modelName과 sortOrder가 무시됩니다. " +
                     "여러 모델의 정보를 한 번에 변경하여 효율적인 관리가 가능합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "기본 모델 일괄 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효하지 않은 모델 ID, 잘못된 status 값 등"),
        @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자"),
        @ApiResponse(responseCode = "404", description = "사용자가 소유하지 않은 기본 모델 ID 포함")
    })
    @PatchMapping("/batch-update")
    public ResponseEntity<Void> batchUpdateDefaultModels(
            @Valid @RequestBody DefaultModelBatchUpdateRequest request
    ) {
        defaultModelService.batchUpdateDefaultModels(request);
        return ResponseEntity.noContent().build();
    }

}
