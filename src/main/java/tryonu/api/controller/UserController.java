package tryonu.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tryonu.api.common.wrapper.ApiResponseWrapper;
import tryonu.api.dto.requests.UserInitRequest;
import tryonu.api.dto.responses.SimpleUserResponse;
import tryonu.api.service.user.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 사용자 관련 API 컨트롤러
 */
@Tag(name = "사용자 API", description = "사용자 정보 관리 관련 API")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "X-UUID")
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
        description = "uuid를 사용하여 익명 사용자를 초기화합니다. " +
                     "기존 사용자가 있으면 해당 정보를 반환하고, " +
                     "없으면 새로운 사용자를 생성합니다. " +
                     "응답에는 사용자의 기본 모델이 sortOrder 오름차순, id 오름차순으로 정렬되어 포함되며, " +
                     "피팅 결과는 id 내림차순으로 정렬되어 포함됩니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "사용자 초기화 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(
                        type = "object",
                        example = """
                        {
                          "isSuccess": false,
                          "error": {
                            "code": "INVALID_REQUEST",
                            "message": "잘못된 요청 데이터입니다.",
                            "validationErrors": {
                              "uuid": "UUID는 필수입니다."
                            }
                          }
                        }""")))
    })
    @PostMapping("/init")
    @ResponseStatus(HttpStatus.CREATED)
    public void initializeUser(@Valid @RequestBody UserInitRequest request) {
        userService.initializeUser(request);
    }
    
    /**
     * 현재 사용자 정보 조회 (간단한 정보만)
     * 
     * @return 현재 사용자 간단 정보
     */
    @Operation(
        summary = "현재 사용자 정보 조회",
        description = "X-UUID 헤더를 통해 현재 인증된 사용자의 간단한 정보를 조회합니다. " +
                     "응답에는 사용자의 ID와 UUID만 포함됩니다. " +
                     "기본 모델과 피팅 결과가 필요한 경우 `/default-model` 또는 `/try-on/list` API를 사용하세요."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공", 
                    content = @Content(schema = @Schema(
                        type = "object",
                        example = """
                        {
                          "isSuccess": true,
                          "data": {
                            "id": 1,
                            "uuid": "f1a1447f-3f20-421b-a8bb-304f35d07a54"
                          },
                        }"""))),
        @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자",
                    content = @Content(schema = @Schema(
                        type = "object",
                        example = """
                        {
                          "isSuccess": false,
                          "error": {
                            "code": "UNAUTHORIZED",
                            "message": "인증되지 않은 사용자입니다.",
                            "validationErrors": null
                          }
                        }""")))
    })
    @GetMapping("/me")
    public ApiResponseWrapper<SimpleUserResponse> getCurrentUserInfo() {
        SimpleUserResponse response = userService.getCurrentUserSimpleInfo();
        return ApiResponseWrapper.ofSuccess(response);
    }
}
