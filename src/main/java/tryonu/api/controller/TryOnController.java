package tryonu.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
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

import java.util.List;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

@Validated
@RestController
@RequestMapping("/try-on")
@RequiredArgsConstructor
@Tag(name = "가상 피팅 API", description = "가상 피팅 실행 및 결과 관리 API - 피팅 실행, 결과 조회, 전체 데이터 조회")
@SecurityRequirement(name = "X-UUID")
public class TryOnController {
    private final TryOnService tryOnService;

    /**
     * 가상 피팅 실행
     * 
     * @param modelUrl 모델 이미지의 URL
     * @param productPageUrl 상품 상세 페이지 URL (선택)
     * @param file 의류 이미지 파일
     * @return 가상 피팅 결과
     */
    @Operation(
        summary = "가상 피팅 실행", 
        description = "의류 이미지와 모델 정보를 받아 가상 피팅을 실행합니다.\n\n" +
                     "- modelUrl: 모델 이미지의 URL (쿼리 파라미터)\n" +
                     "- productPageUrl: 상품 상세 페이지 URL (쿼리 파라미터, 선택)\n" +
                     "- file: 의류 이미지 파일 (multipart/form-data)\n" +
                     "\n파일 업로드 API에서 메타데이터(모델URL, 상품URL 등)는 쿼리 파라미터로, 이미지는 file 파트로 분리."
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "가상 피팅 성공", 
                    content = @Content(schema = @Schema(implementation = TryOnResponse.class))), 
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자")
    })
    @PostMapping(value = "/fitting", consumes = "multipart/form-data")
    public ApiResponseWrapper<TryOnResponse> tryOnWithImage(
        @Parameter(description = "모델 이미지의 URL", required = true, example = "https://example.com/model.jpg") 
        @RequestParam @NotBlank(message = "모델 이미지 URL은 필수입니다") @URL(message = "올바른 URL 형식이어야 합니다") String modelUrl,
        
        @Parameter(description = "상품 상세 페이지 URL (선택)", example = "https://example.com/product/123") 
        @RequestParam(required = false) @URL(message = "올바른 URL 형식이어야 합니다") String productPageUrl,
        
        @Parameter(description = "의류 이미지 파일 (10MB 이하, jpg/png/webp)", required = true) 
        @RequestParam("file") @NotEmptyFile MultipartFile file
    ) {
        TryOnResponse response = tryOnService.tryOn(modelUrl, productPageUrl, file);
        return ApiResponseWrapper.ofSuccess(response);
    }

    /**
     * 현재 사용자의 피팅 결과 목록 조회
     * 
     * @return 현재 사용자의 피팅 결과 목록
     */
    @Operation(
        summary = "피팅 결과 목록 조회",
        description = "X-UUID 헤더를 통해 현재 인증된 사용자의 피팅 결과 목록을 조회합니다. " +
                     "응답에는 사용자의 피팅 결과 목록이 id 내림차순으로 정렬되어 포함됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "피팅 결과 목록 조회 성공", 
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TryOnResultDto.class)))),
        @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자")
    })
    @GetMapping("/results")
    public ApiResponseWrapper<List<TryOnResultDto>> getCurrentUserTryOnResults() {
        List<TryOnResultDto> response = tryOnService.getCurrentUserTryOnResults();
        return ApiResponseWrapper.ofSuccess(response);
    }

    /**
     * 현재 사용자의 기본 모델과 피팅 결과 목록 조회
     * 
     * @return 현재 사용자의 기본 모델과 피팅 결과 목록
     */
    @Operation(
        summary = "기본 모델 및 피팅 결과 목록 조회",
        description = "X-UUID 헤더를 통해 현재 인증된 사용자의 기본 모델과 피팅 결과 목록을 함께 조회합니다. " +
                     "응답에는 사용자의 기본 모델과 피팅 결과 목록이 id 내림차순으로 정렬되어 포함됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "기본 모델 및 피팅 결과 목록 조회 성공", 
                    content = @Content(schema = @Schema(implementation = UserInfoResponse.class))),
        @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자")
    })
    @GetMapping("/all")
    public ApiResponseWrapper<UserInfoResponse> getCurrentUserAllData() {
        UserInfoResponse response = tryOnService.getCurrentUserAllData();
        return ApiResponseWrapper.ofSuccess(response);
    }

}
