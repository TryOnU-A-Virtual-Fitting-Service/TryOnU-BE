package tryonu.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.dto.responses.AssetResponse;
import tryonu.api.service.company.CompanyService;

/**
 * 프론트엔드 셋업 관련 API 컨트롤러
 */
@Tag(name = "셋업 API", description = "프론트엔드 초기 설정 및 리소스 조회 관련 API")
@RestController
@RequestMapping("/setup")
@RequiredArgsConstructor
public class SetupController {

    private final CompanyService companyService;

    /**
     * 회사 애셋 URL 조회
     * 
     * @param url 현재 페이지의 전체 URL
     * @return 애셋 이미지 CDN URL
     */
    @Operation(
        summary = "회사 애셋 조회",
        description = "현재 페이지의 전체 URL에서 도메인을 추출하여 해당 회사의 애셋 CDN URL을 조회합니다. " +
                     "현재는 로고 이미지를 제공하며, 향후 다른 애셋도 추가될 예정입니다. " +
                     "지원하는 도메인: musinsa.com, spao.com, zigzag.kr, ably.co.kr. " +
                     "프론트엔드에서 window.location.href로 현재 URL을 전송하면 자동으로 도메인을 파싱합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "애셋 URL 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 - url 파라미터 누락 또는 잘못된 URL 형식"),
        @ApiResponse(responseCode = "404", description = "요청한 도메인에 해당하는 회사를 찾을 수 없음")
    })
    @GetMapping
    public ApiResponseWrapper<AssetResponse> getAsset(
        @Parameter(
            description = "현재 페이지의 전체 URL", 
            example = "https://www.musinsa.com/main/musinsa/recommend?gf=A", 
            required = true
        )
        @RequestParam("url") String url
    ) {
        AssetResponse assetResponse = companyService.getAssetResponseByUrl(url);
        return ApiResponseWrapper.ofSuccess(assetResponse);
    }
}