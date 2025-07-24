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
import tryonu.api.service.tryon.TryOnService;
import tryonu.api.common.validation.NotEmptyFile;

@Validated
@RestController
@RequestMapping("/try-on")
@RequiredArgsConstructor
@Tag(name = "가상 피팅 API", description = "가상 피팅 관련 API")
@SecurityRequirement(name = "DeviceId")
public class TryOnController {
    private final TryOnService tryOnService;

    @Operation(
        summary = "가상 피팅 실행", 
        description = "의류 이미지와 모델 정보를 받아 가상 피팅을 실행합니다.\n\n" +
                     "- modelUrl: 모델 이미지의 URL (쿼리 파라미터)\n" +
                     "- productPageUrl: 상품 상세 페이지 URL (쿼리 파라미터, 선택)\n" +
                     "- file: 의류 이미지 파일 (multipart/form-data)\n" +
                     "\n실무에서는 파일 업로드 API에서 메타데이터(모델URL, 상품URL 등)는 쿼리 파라미터로, 이미지는 file 파트로 분리하는 것이 표준적입니다."
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "가상 피팅 성공"), 
        @ApiResponse(responseCode = "400", description = "잘못된 요청") 
    })
    @PostMapping(value = "/fitting", consumes = "multipart/form-data")
    public ApiResponseWrapper<TryOnResponse> tryOnWithImage(
        @Parameter(description = "모델 이미지의 URL", required = true, example = "https://example.com/model.jpg") 
        @RequestParam String modelUrl,
        
        @Parameter(description = "상품 상세 페이지 URL (선택)", example = "https://example.com/product/123") 
        @RequestParam(required = false) String productPageUrl,
        
        @Parameter(description = "의류 이미지 파일 (10MB 이하, jpg/png/webp)", required = true) 
        @RequestParam("file") @NotEmptyFile MultipartFile file
    ) {
        TryOnResponse response = tryOnService.tryOn(modelUrl, productPageUrl, file);
        return ApiResponseWrapper.ofSuccess(response);
    }


}
