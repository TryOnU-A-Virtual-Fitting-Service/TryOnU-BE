package tryonu.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tryonu.api.common.auth.SecurityUtils;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.dto.requests.UserInitRequest;
import tryonu.api.dto.responses.UserResponse;
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
     * @return 초기화된 사용자 정보
     */
    @Operation(
        summary = "익명 사용자 초기화",
        description = "deviceId를 사용하여 익명 사용자를 초기화합니다. " +
                     "기존 사용자가 있으면 해당 정보를 반환하고, " +
                     "없으면 새로운 사용자를 생성합니다."
    )
    @PostMapping("/init")
    public ApiResponseWrapper<UserResponse> initializeUser(@Valid @RequestBody UserInitRequest request) {
        UserResponse userResponse = userService.initializeUser(request);
        return ApiResponseWrapper.ofSuccess(userResponse);
    }

    /**
     * 현재 사용자 정보 조회
     * 
     * @return 현재 사용자 정보
     */
    @Operation(
        summary = "현재 사용자 정보 조회",
        description = "SecurityContext에서 현재 사용자의 정보를 조회합니다. " +
                     "X-Device-ID 헤더나 deviceId 쿼리 파라미터를 통해 인증된 사용자 정보를 반환합니다."
    )
    @GetMapping("/me")
    public ApiResponseWrapper<UserResponse> getCurrentUser() {
        return ApiResponseWrapper.ofSuccess(UserResponse.from(SecurityUtils.getCurrentUser()));
    }
}
