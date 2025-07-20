package tryonu.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.dto.requests.UserInitRequest;
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
    public ApiResponseWrapper<Void> initializeUser(@Valid @RequestBody UserInitRequest request) {
        userService.initializeUser(request);
        return ApiResponseWrapper.ofSuccess();
    }
}
