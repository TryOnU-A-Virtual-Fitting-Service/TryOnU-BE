package tryonu.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.dto.requests.UserInitRequest;
import tryonu.api.dto.responses.UserInfoResponse;
import tryonu.api.service.user.UserService;

/**
 * 사용자 관련 API 컨트롤러
 */
@Tag(name = "사용자 API", description = "사용자 정보 관리 관련 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 익명 사용자 초기화
     * 
     * @param request 초기화 요청 정보
     * @return 초기화된 사용자 정보 (기본 모델, 피팅 모델 포함)
     */
    @Operation(
        summary = "익명 사용자 초기화",
        description = "deviceId를 사용하여 익명 사용자를 초기화합니다. " +
                     "기존 사용자가 있으면 해당 정보를 반환하고, " +
                     "없으면 새로운 사용자를 생성합니다. " +
                     "응답에는 사용자의 기본 모델과 피팅 모델 목록이 id 내림차순으로 정렬되어 포함됩니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 초기화 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping("/init")
    public ApiResponseWrapper<UserInfoResponse> initializeUser(@Valid @RequestBody UserInitRequest request) {
        UserInfoResponse response = userService.initializeUser(request);
        return ApiResponseWrapper.ofSuccess(response);
    }
    
    /**
     * 현재 사용자 정보 조회
     * 
     * @return 현재 사용자 정보 (기본 모델, 피팅 모델 포함)
     */
    @Operation(
        summary = "현재 사용자 정보 조회",
        description = "X-Device-Id 헤더를 통해 현재 인증된 사용자의 정보를 조회합니다. " +
                     "응답에는 사용자의 기본 모델과 피팅 모델 목록이 id 내림차순으로 정렬되어 포함됩니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
        @ApiResponse(responseCode = "401", description = "잘못된 X-Device-Id 헤더, 또는 인증되지 않은 사용자"),
    })
    @GetMapping("/me")
    public ApiResponseWrapper<UserInfoResponse> getCurrentUserInfo() {
        UserInfoResponse response = userService.getCurrentUserInfo();
        return ApiResponseWrapper.ofSuccess(response);
    }
}
