package tryonu.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.common.wrapper.ApiResponseWrapper;

@Slf4j
@Validated
@RestController
@RequestMapping("/test-errors")
@Tag(name = "테스트 에러 API", description = "예외/알림 검증용 테스트 엔드포인트")
public class ErrorTestController {

    @GetMapping("/runtime")
    @Operation(summary = "RuntimeException 발생", description = "500 에러 발생 테스트")
    public ApiResponseWrapper<Void> throwRuntime() {
        log.info("[ErrorTestController] /runtime 호출");
        throw new RuntimeException("테스트용 런타임 예외");
    }

    @GetMapping("/custom-not-found")
    @Operation(summary = "CustomException(404) 발생", description = "리소스 미찾음 에러 발생 테스트")
    public ApiResponseWrapper<Void> throwCustomNotFound() {
        log.info("[ErrorTestController] /custom-not-found 호출");
        throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND.getMessage());
    }

    @GetMapping("/oom")
    @Operation(summary = "OutOfMemoryError 발생", description = "OOM 에러 핸들링 및 슬랙 알림 테스트")
    public ApiResponseWrapper<Void> throwOom() {
        log.info("[ErrorTestController] /oom 호출");
        throw new OutOfMemoryError("테스트용 OOM");
    }

    @GetMapping("/validation")
    @Operation(summary = "검증 실패(400) 발생", description = "파라미터 제약조건 위반으로 400 발생 테스트")
    public ApiResponseWrapper<String> validation(@RequestParam(defaultValue = "0") @Min(1) int page) {
        // page<1 인 경우 MethodArgumentNotValidException -> 400
        return ApiResponseWrapper.ofSuccess("OK: page=" + page);
    }

    @PostMapping("/only-post")
    @Operation(summary = "POST 전용(405 유발)", description = "GET으로 호출 시 405(Method Not Allowed) 발생")
    public ApiResponseWrapper<String> onlyPost() {
        return ApiResponseWrapper.ofSuccess("OK: POST accepted");
    }

    @PostMapping(value = "/expect-multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "multipart/form-data 전용(415 유발)", description = "다른 Content-Type으로 호출 시 415 발생")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "415", description = "Unsupported Media Type")
    })
    public ApiResponseWrapper<String> expectMultipart(@RequestPart("file") MultipartFile file) {
        return ApiResponseWrapper.ofSuccess("OK: received " + file.getOriginalFilename());
    }
}


