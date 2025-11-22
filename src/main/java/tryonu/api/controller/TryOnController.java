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
import tryonu.api.dto.responses.TryOnJobInitResponse;
import tryonu.api.dto.responses.SizeAdviceResponse;
import tryonu.api.dto.requests.SizeAdviceRequest;
import tryonu.api.dto.requests.ImageUrlRequest;
import tryonu.api.dto.responses.ImageDataUrlResponse;

@Validated
@RestController
@RequestMapping("/try-on")
@RequiredArgsConstructor
@Tag(name = "가상 피팅 API", description = "가상 피팅 실행 및 결과 관리 API - 피팅 실행, 결과 조회, 전체 데이터 조회")
@SecurityRequirement(name = "X-UUID")
public class TryOnController {
  private final TryOnService tryOnService;

  /**
   * 가상 피팅 작업(Job) 생성
   *
   * 새로운 가상 피팅 작업을 생성합니다. 클라이언트는 이 엔드포인트를 호출하여 피팅 작업을 시작할 수 있습니다.
   *
   * @return 생성된 피팅 작업 정보
   */
  @Operation(summary = "가상 피팅 작업 생성", description = "새로운 가상 피팅 작업(Job)을 생성합니다. 클라이언트는 이 엔드포인트를 호출하여 피팅 작업을 시작할 수 있습니다.", responses = {
      @ApiResponse(responseCode = "200", description = "피팅 작업 생성 성공", content = @Content(schema = @Schema(implementation = TryOnJobInitResponse.class))),
      @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자")
  })
  @PostMapping(value = "/job")
  public ApiResponseWrapper<TryOnJobInitResponse> createTryOnJob() {
    TryOnJobInitResponse response = tryOnService.createTryOnJob();
    return ApiResponseWrapper.ofSuccess(response);
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
      "  - tryOnJobId: 가상 피팅 작업 ID\n" +
      "  - modelUrl: 모델 이미지 URL\n" +
      "  - defaultModelId: 기본 모델 ID\n" +
      "  - productPageUrl: 상품 상세 페이지 URL (선택)\n" +
      "- file: 의류 이미지 파일 (multipart/form-data)\n" +
      "\nJSON 데이터와 파일을 multipart/form-data로 함께 전송합니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "가상 피팅 성공", content = @Content(schema = @Schema(type = "object", example = """
          {
            "isSuccess": true,
            "data": {
              "tryOnJobId": "1c3f077f-ef18-4361-9ed0-4701904f3d90",
              "tryOnResultUrl": "https://cdn.example.com/users/models/tryonresult-1.jpg",
              "defaultModelId": 5,
              "modelName": "슬림 한국인 남성"
            }
          }"""))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(type = "object", example = """
          {
            "isSuccess": false,
            "error": {
              "code": "INVALID_REQUEST",
              "message": "잘못된 요청입니다.",
              "validationErrors": null
            }
          }"""))),
      @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자", content = @Content(schema = @Schema(type = "object", example = """
          {
            "isSuccess": false,
            "error": {
              "code": "UNAUTHORIZED",
              "message": "인증되지 않은 사용자입니다.",
              "validationErrors": null
            }
          }""")))
  })
  @PostMapping(value = "/fitting", consumes = "multipart/form-data")
  public ApiResponseWrapper<TryOnResponse> tryOnWithImage(
      @Parameter(description = "가상 피팅 요청 정보 (JSON)", required = true) @RequestPart(value = "request") @Valid TryOnRequestDto request,

      @Parameter(description = "의류 이미지 파일 (10MB 이하, jpg/png/jpeg)", required = true) @RequestPart("file") @NotEmptyFile MultipartFile file) {
    TryOnResponse response = tryOnService.tryOn(request, file);
    return ApiResponseWrapper.ofSuccess(response);
  }

  @PostMapping("/size-advice")
  public ApiResponseWrapper<SizeAdviceResponse> giveSizeAdvice(
      @Parameter(description = "사이즈 조언 정보 (JSON)", required = true) @RequestBody @Valid SizeAdviceRequest request) {
    SizeAdviceResponse response = tryOnService.giveSizeAdvice(request);
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
      @ApiResponse(responseCode = "200", description = "피팅 결과 목록 조회 성공", content = @Content(schema = @Schema(type = "object", example = """
          {
            "isSuccess": true,
            "data": [
              {
                "id": 1,
                "tryOnResultUrl": "https://cdn.example.com/tryon/result1.jpg",
                "createdAt": "2024-01-01T00:00:00"
              }
            ],
          }"""))),
      @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자", content = @Content(schema = @Schema(type = "object", example = """
          {
            "isSuccess": false,
            "error": {
              "code": "UNAUTHORIZED",
              "message": "인증되지 않은 사용자입니다.",
              "validationErrors": null
            }
          }""")))
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
  @Operation(summary = "기본 모델 및 피팅 결과 목록 조회", description = "X-UUID 헤더를 통해 현재 인증된 사용자의 기본 모델과 피팅 결과 목록을 함께 조회합니다. "
      +
      "응답에는 사용자의 기본 모델과 피팅 결과 목록이 id 내림차순으로 정렬되어 포함됩니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "기본 모델 및 피팅 결과 목록 조회 성공"),
      @ApiResponse(responseCode = "401", description = "잘못된 X-UUID 헤더, 또는 인증되지 않은 사용자")
  })
  @GetMapping("/with-default-model/list")
  public ApiResponseWrapper<UserInfoResponse> getCurrentUserAllData() {
    UserInfoResponse response = tryOnService.getCurrentUserAllData();
    return ApiResponseWrapper.ofSuccess(response);
  }

  /**
   * 이미지 URL을 Data URL로 변환
   * 
   * @param request 이미지 URL 요청
   * @return Data URL 형태의 이미지 데이터
   */
  @Operation(summary = "이미지 URL을 Data URL로 변환", description = "이미지 URL을 받아서 Base64로 인코딩된 Data URL로 변환합니다. " +
      "지원하는 이미지 형식: JPG, JPEG, PNG, GIF, WebP, BMP, SVG. " +
      "캡처 로직 최적화를 위한 API입니다.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "변환 성공", content = @Content(schema = @Schema(implementation = ImageDataUrlResponse.class))),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효하지 않은 URL 형식"),
      @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "이미지 다운로드 또는 변환 실패")
  })
  @PostMapping("/image")
  public ApiResponseWrapper<ImageDataUrlResponse> convertImageUrlToDataUrl(
      @Parameter(description = "이미지 URL 변환 요청", required = true) @Valid @RequestBody ImageUrlRequest request) {
    ImageDataUrlResponse response = tryOnService.convertImageUrlToDataUrl(request);
    return ApiResponseWrapper.ofSuccess(response);
  }

}
