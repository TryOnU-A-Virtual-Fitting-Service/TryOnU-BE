package tryonu.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.dto.responses.LogoResponse;
import tryonu.api.service.company.CompanyService;

/**
 * 프론트엔드 셋업 관련 API 컨트롤러
 */
@Tag(name = "셋업 API", description = "프론트엔드 초기 설정 및 리소스 조회 관련 API")
@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class SetupController {

    private final CompanyService companyService;

    /**
     * 회사 로고 URL 조회
     * 
     * @param logo 회사명 (예: musinsa, spao, zigzag, ably)
     * @return 로고 이미지 CDN URL
     */
    @Operation(
        summary = "회사 로고 URL 조회",
        description = "회사명을 통해 해당 회사의 로고 이미지 CDN URL을 조회합니다. " +
                     "지원하는 회사: musinsa, spao, zigzag, ably. " +
                     "프론트엔드에서 이 URL을 사용하여 직접 CDN에서 이미지를 로드할 수 있습니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로고 URL 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 - logo 파라미터 누락"),
        @ApiResponse(responseCode = "404", description = "요청한 회사를 찾을 수 없음")
    })
    @GetMapping
    public ApiResponseWrapper<LogoResponse> getLogo(
        @Parameter(description = "회사명", example = "musinsa", required = true)
        @RequestParam("logo") String logo
    ) {
        String logoUrl = companyService.getLogoUrl(logo);
        LogoResponse response = new LogoResponse(logo, logoUrl);
        return ApiResponseWrapper.ofSuccess(response);
    }
}