package tryonu.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.dto.responses.TryOnResponse;
import tryonu.api.dto.responses.TryOnResultDto;
import tryonu.api.service.tryon.TryOnService;
import tryonu.api.common.validation.NotEmptyFile;
import tryonu.api.dto.responses.UserInfoResponse;
import tryonu.api.dto.requests.TryOnRequestDto;
import jakarta.validation.Valid;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import tryonu.api.dto.responses.TryOnJobInitResponse;

@Validated
@RestController
@RequestMapping("/try-on")
@RequiredArgsConstructor
@Tag(name = "가상 피팅 API", description = "가상 피팅 실행 및 결과 관리 API - 피팅 실행, 결과 조회, 전체 데이터 조회")
@SecurityRequirement(name = "X-UUID")
public class TryOnController {
    private final TryOnService tryOnService;

    @PostMapping(value = "/job")
    public TryOnJobInitResponse createTryOnJob() {
        return tryOnService.createTryOnJob();
    }

    /**
     * 가상 피팅 실행
     * 
     * @param request 가상 피팅 요청 정보 (JSON)
     * @param file    의류 이미지 파일
     * @return 가상 피팅 결과
     */
    @Operation(summary = "가상 피팅 실행", description = "의류 이미지와 모델 정보를 받아 가상 피팅을 실행합니다.\n\n" +
            "- request: 가상 피팅 요청 정보 (JSON part)\n" +
            "  - modelUrl: 모델 이미지 URL\n" +
            "  - defaultModelId: 기본 모델 ID\n" +
            "  - productPageUrl: 상품 상세 페이지 URL (선택)\n" +
            "- file: 의류 이미지 파일 (multipart/form-data)\n" +
            "\nJSON 데이터와 파일을 multipart/form-data로 함께 전송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가상 피팅 성공", content = @Content(schema = @Schema(implementation = TryOnResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자")
    })
    @PostMapping(value = "/fitting", consumes = "multipart/form-data")
    public ApiResponseWrapper<TryOnResponse> tryOnWithImage(
            @Parameter(description = "가상 피팅 요청 정보 (JSON)", required = true) @RequestPart(value = "request") @Valid TryOnRequestDto request,

            @Parameter(description = "의류 이미지 파일 (10MB 이하, jpg/png/jpeg)", required = true) @RequestPart("file") @NotEmptyFile MultipartFile file) {
        TryOnResponse response = tryOnService.tryOn(request, file);
        return ApiResponseWrapper.ofSuccess(response);
    }

    /**
     * 현재 사용자의 피팅 결과 목록 조회
     * 
     * @return 현재 사용자의 피팅 결과 목록
     */
    @Operation(summary = "피팅 결과 목록 조회", description = "X-UUID 헤더를 통해 현재 인증된 사용자의 피팅 결과 목록을 조회합니다. " +
            "응답에는 사용자의 피팅 결과 목록이 id 내림차순으로 정렬되어 포함됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "피팅 결과 목록 조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TryOnResultDto.class)))),
            @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자")
    })
    @GetMapping("/list")
    public ApiResponseWrapper<List<TryOnResultDto>> getCurrentUserTryOnResults() {
        List<TryOnResultDto> response = tryOnService.getCurrentUserTryOnResults();
        return ApiResponseWrapper.ofSuccess(response);
    }

    /**
     * 현재 사용자의 기본 모델과 피팅 결과 목록 조회
     * 
     * @return 현재 사용자의 기본 모델과 피팅 결과 목록
     */
    @Operation(summary = "기본 모델 및 피팅 결과 목록 조회", description = "X-UUID 헤더를 통해 현재 인증된 사용자의 기본 모델과 피팅 결과 목록을 함께 조회합니다. " +
            "응답에는 사용자의 기본 모델과 피팅 결과 목록이 id 내림차순으로 정렬되어 포함됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "기본 모델 및 피팅 결과 목록 조회 성공", content = @Content(schema = @Schema(implementation = UserInfoResponse.class))),
            @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자")
    })
    @GetMapping("/with-default-model/list")
    public ApiResponseWrapper<UserInfoResponse> getCurrentUserAllData() {
        UserInfoResponse response = tryOnService.getCurrentUserAllData();
        return ApiResponseWrapper.ofSuccess(response);
    }

}
