package tryonu.api.service.tryon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tryonu.api.dto.requests.SizeAdviceRequest;
import tryonu.api.dto.requests.TryOnRequestDto;
import tryonu.api.dto.requests.VirtualFittingRequest;
import tryonu.api.dto.requests.ImageUrlRequest;
import tryonu.api.dto.responses.*;
import tryonu.api.dto.responses.ImageDataUrlResponse;
import tryonu.api.domain.User;
import tryonu.api.domain.DefaultModel;
import tryonu.api.domain.TryOnResult;
import tryonu.api.repository.tryonresult.TryOnResultRepository;
import tryonu.api.common.util.VirtualFittingUtil;
import tryonu.api.common.util.ImageUploadUtil;
import tryonu.api.common.util.BackgroundRemovalUtil;
import tryonu.api.common.util.CategoryPredictionUtil;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.converter.TryOnResultConverter;
import tryonu.api.common.enums.Category;
import tryonu.api.common.auth.SecurityUtils;
import tryonu.api.repository.defaultmodel.DefaultModelRepository;
import tryonu.api.converter.UserConverter;
import tryonu.api.repository.sizeadvice.SizeAdviceRepository;
import tryonu.api.converter.SizeAdviceConverter;
import tryonu.api.domain.SizeAdvice;
import tryonu.api.analyzer.SizeAnalyzer;
import tryonu.api.analyzer.SizeAnalyzeRequest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.Base64;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import org.springframework.transaction.annotation.Transactional;

/**
 * 가상 피팅 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TryOnServiceImpl implements TryOnService {

    private final VirtualFittingUtil virtualFittingUtil;
    private final ImageUploadUtil imageUploadUtil;
    private final CategoryPredictionUtil categoryPredictionUtil;
    private final TryOnResultRepository tryOnResultRepository;
    private final TryOnResultConverter tryOnResultConverter;
    private final DefaultModelRepository defaultModelRepository;
    private final UserConverter userConverter;
    private final TryOnWriteService tryOnWriteService;
    private final SizeAdviceRepository sizeAdviceRepository;
    private final SizeAdviceConverter sizeAdviceConverter;
    private final SizeAnalyzer sizeAnalyzer;
    private final BackgroundRemovalUtil backgroundRemovalUtil;
    private final org.springframework.web.reactive.function.client.WebClient imageDownloadWebClient;

    @Value("${virtual-fitting.polling.max-wait-time-ms:60000}") // 기본 1분
    private long maxWaitTimeMs;

    @Value("${virtual-fitting.polling.interval-ms:1000}") // 기본 1초 간격
    private long pollIntervalMs;

    @Override
    @Transactional(readOnly = false)
    public TryOnJobInitResponse createTryOnJob() {
        String tryOnJobId = UUID.randomUUID().toString();
        User currentUser = SecurityUtils.getCurrentUser();

        log.info("[TryOnService] Created Try-On Job with ID: {}", tryOnJobId);
        tryOnResultRepository.save(tryOnResultConverter.toTryOnResultEntity(tryOnJobId, currentUser));
        sizeAdviceRepository.save(sizeAdviceConverter.toSizeAdviceEntity(tryOnJobId, currentUser));
        return new TryOnJobInitResponse(tryOnJobId);

    }

    @Override
    @Transactional
    public SizeAdviceResponse giveSizeAdvice(SizeAdviceRequest request) {
        String tryOnJobId = request.tryOnJobId();
        String sizeInfo = request.sizeInfo();        
       
        SizeAdvice sizeAdvice = sizeAdviceRepository.findByTryOnJobIdAndIsDeletedFalseOrThrow(tryOnJobId);
        
        String advice = sizeAnalyzer.analyze(new SizeAnalyzeRequest(tryOnJobId, sizeInfo)).advice();

        sizeAdvice.updateSizeInfoAndAdvice(sizeInfo, advice);
        sizeAdviceRepository.save(sizeAdvice);

        return sizeAdviceConverter.toSizeAdviceResponse(sizeAdvice);
    }


    @Override
    public TryOnResponse tryOn(TryOnRequestDto request, MultipartFile file) {
        // DTO에서 개별 필드 추출
        String modelUrl = request.modelUrl();
        Long defaultModelId = request.defaultModelId();
        String productPageUrl = request.productPageUrl();
        String tryOnJobId = request.tryOnJobId();

        // 현재 인증된 사용자 및 기본 모델 조회
        User currentUser = SecurityUtils.getCurrentUser();
        DefaultModel defaultModel = defaultModelRepository.findByIdAndIsDeletedFalseOrThrow(defaultModelId); // 검증
        TryOnResult tryOnResult = tryOnResultRepository.findByTryOnJobIdOrThrow(tryOnJobId);

        try {
            // 의류 이미지 카테고리 예측
            CategoryPredictionResponse categoryPredictionResponse = categoryPredictionUtil.predictCategory(file);
            log.info("[TryOnService] 카테고리 예측 완료 - className={}, confidence={}", categoryPredictionResponse.className(),
                    categoryPredictionResponse.confidence());

            // 지원하지 않는 카테고리 검증
            validateSupportedCategory(categoryPredictionResponse.className());

            // 의류 이미지 업로드
            String clothImageUrl = imageUploadUtil.uploadClothImage(file);

            // 가상 피팅 API 요청 생성
            VirtualFittingRequest virtualFittingRequest = tryOnResultConverter.toVirtualFittingRequest(modelUrl,
                    clothImageUrl);

            // 가상 피팅 실행 (폴링 방식)
            VirtualFittingResponse virtualFittingResponse = virtualFittingUtil.runVirtualFitting(virtualFittingRequest);

            // 폴링으로 완료 대기
            VirtualFittingStatusResponse finalStatus = virtualFittingUtil.waitForCompletion(virtualFittingResponse.id(),
                    maxWaitTimeMs, pollIntervalMs);

            // 결과 처리
            if ("completed".equals(finalStatus.status())) {
                // 안전한 결과 추출 - null/empty 체크
                if (finalStatus.output() == null || finalStatus.output().isEmpty()) {
                    log.error("[TryOnService] 가상 피팅 완료되었으나 결과 이미지가 없음 - modelUrl={}, output={}", modelUrl,
                            finalStatus.output());
                    throw new CustomException(ErrorCode.VIRTUAL_FITTING_FAILED, "가상피팅이 완료되었으나 결과 이미지를 받지 못했습니다.");
                }

                String resultImageUrl = finalStatus.output().get(0); // 첫 번째 결과 이미지

                // fashn.ai 결과 이미지를 다운로드하여 S3에 업로드
                String uploadedResultImageUrl = imageUploadUtil.uploadTryOnResultImage(backgroundRemovalUtil.removeBackground(resultImageUrl));
                log.info("[TryOnService] 가상 피팅 결과 S3 업로드 완료 - originalUrl={}, s3Url={}", resultImageUrl,
                        uploadedResultImageUrl);

                // 저장 및 응답 생성은 짧은 쓰기 트랜잭션으로 분리
                Category category = parseCategory(categoryPredictionResponse.className());
                TryOnResponse response = tryOnWriteService.saveAndBuildResponse(
                        tryOnResult,
                        category,
                        clothImageUrl,
                        productPageUrl,
                        modelUrl,
                        uploadedResultImageUrl,
                        virtualFittingResponse.id(),
                        defaultModel,
                        currentUser);

                return response;
            } else {
                // fashn.ai API 에러 구체적 로깅 및 처리
                handleVirtualFittingError(modelUrl, finalStatus);
                // 이 코드는 절대 실행되지 않음 (handleVirtualFittingError가 항상 예외를 던짐)
                throw new RuntimeException("Unreachable code");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 지원하지 않는 카테고리 검증
     */
    private void validateSupportedCategory(String className) {
        if ("ACCESSORY".equalsIgnoreCase(className) || "SHOES".equalsIgnoreCase(className)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST,
                    "액세서리와 신발은 가상피팅에서 지원하지 않습니다. (감지된 카테고리: " + className + ")");
        }
    }

    /**
     * fashn.ai API 에러를 구체적으로 로깅하고 적절한 예외로 변환
     */
    private void handleVirtualFittingError(String modelUrl, VirtualFittingStatusResponse finalStatus) {
        if (finalStatus.error() == null) {
            log.error("[TryOnService] 가상 피팅 실패 - modelUrl={}, status={}, error=null", modelUrl, finalStatus.status());
            throw new CustomException(ErrorCode.VIRTUAL_FITTING_FAILED, "가상피팅에 실패했습니다.");
        }

        String errorName = finalStatus.error().name();
        String errorMessage = finalStatus.error().message();

        // 구체적인 에러 로깅
        log.error("[TryOnService] fashn.ai API 가상 피팅 실패 - modelUrl={}, status={}, errorName={}, errorMessage={}",
                modelUrl, finalStatus.status(), errorName, errorMessage);

        // 에러 타입별 처리 및 적절한 ErrorCode 선택
        ErrorCode errorCode = switch (errorName) {
            case "ImageLoadError" -> {
                log.error("[TryOnService] 이미지 로드 실패 - 이미지 URL 접근성 또는 형식 문제: {}", errorMessage);
                yield ErrorCode.IMAGE_LOAD_ERROR;
            }
            case "ContentModerationError" -> {
                log.error("[TryOnService] 콘텐츠 모더레이션 실패 - 부적절한 콘텐츠 감지: {}", errorMessage);
                yield ErrorCode.CONTENT_MODERATION_ERROR;
            }
            case "PhotoTypeError" -> {
                log.error("[TryOnService] 이미지 타입 감지 실패 - garment_photo_type 자동 감지 불가: {}", errorMessage);
                yield ErrorCode.PHOTO_TYPE_ERROR;
            }
            case "PoseError" -> {
                log.error("[TryOnService] 자세 감지 실패 - 모델 또는 의류 이미지에서 자세 인식 불가: {}", errorMessage);
                yield ErrorCode.POSE_ERROR;
            }
            case "PipelineError" -> {
                log.error("[TryOnService] 파이프라인 처리 실패 - 예상치 못한 내부 에러: {}", errorMessage);
                yield ErrorCode.PIPELINE_ERROR;
            }
            default -> {
                log.error("[TryOnService] 알 수 없는 fashn.ai API 에러 - errorName={}, errorMessage={}", errorName,
                        errorMessage);
                yield ErrorCode.VIRTUAL_FITTING_FAILED;
            }
        };

        // 구체적인 에러 메시지와 함께 예외 발생
        String detailedMessage = String.format("%s (fashn.ai 에러: %s)", errorCode.getMessage(), errorMessage);
        throw new CustomException(errorCode, detailedMessage);
    }

    /**
     * 외부 API 카테고리 응답을 안전하게 Category enum으로 변환
     */
    private Category parseCategory(String className) {
        try {
            // 대소문자 정규화 및 안전한 변환
            String normalizedClassName = className.trim().toUpperCase();
            log.debug("[TryOnService] 카테고리 변환 시도 - original: {}, normalized: {}", className, normalizedClassName);

            return Category.valueOf(normalizedClassName);
        } catch (IllegalArgumentException e) {
            log.error("[TryOnService] 지원하지 않는 카테고리 - className: {}, supportedCategories: {}",
                    className, Arrays.toString(Category.values()));
            throw new CustomException(ErrorCode.INVALID_REQUEST,
                    "지원하지 않는 의류 카테고리입니다. (감지된 카테고리: " + className + ")");
        } catch (NullPointerException e) {
            log.error("[TryOnService] 카테고리가 null임 - className: {}", className);
            throw new CustomException(ErrorCode.INVALID_REQUEST, "의류 카테고리 정보가 없습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TryOnResultDto> getCurrentUserTryOnResults() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<TryOnResultDto> tryOnResults = tryOnResultRepository.findTryOnResultsByUserIdOrderByIdDesc(currentUserId);
        return tryOnResults;
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getCurrentUserAllData() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<DefaultModelDto> defaultModels = defaultModelRepository
                .findDefaultModelsByUserIdOrderBySortOrder(currentUserId);
        List<TryOnResultDto> tryOnResults = tryOnResultRepository.findTryOnResultsByUserIdOrderByIdDesc(currentUserId);
        return userConverter.toUserInfoResponse(defaultModels, tryOnResults);
    }

    @Override
    public ImageDataUrlResponse convertImageUrlToDataUrl(ImageUrlRequest request) {
        String imageUrl = request.imageUrl();
        log.info("[TryOnService] 이미지 URL을 Data URL로 변환 시작 - url={}", imageUrl);

        try {
            ResponseEntity<byte[]> responseEntity = imageDownloadWebClient
                    .get()
                    .uri(imageUrl)
                    .retrieve()
                    .toEntity(byte[].class)
                    .block();

            byte[] imageBytes = responseEntity.getBody();
            if (imageBytes == null || imageBytes.length == 0) {
                throw new CustomException(ErrorCode.IMAGE_LOAD_ERROR, "다운로드된 이미지가 비어있습니다.");
            }

            log.info("[TryOnService] 이미지 다운로드 완료 - size={}KB", imageBytes.length / 1024);

            // Content-Type 헤더에서 MIME 타입 가져오기 (더 신뢰성 높음)
            String mimeType = Optional.ofNullable(responseEntity.getHeaders().getContentType())
                    .map(Object::toString)
                    .orElseGet(() -> detectMimeType(imageUrl)); // fallback

            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            String dataUrl = String.format("data:%s;base64,%s", mimeType, base64);

            log.info("[TryOnService] Data URL 변환 완료 - mimeType={}, dataUrlLength={}",
                    mimeType, dataUrl.length());

            return new ImageDataUrlResponse(dataUrl);

        } catch (WebClientResponseException e) {
            log.error("[TryOnService] 이미지 URL 변환 실패 (WebClient) - url={}, status={}, body={}", imageUrl, e.getStatusCode(), e.getResponseBodyAsString(), e);
            if (e.getStatusCode().is4xxClientError()) {
                throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "이미지를 찾을 수 없거나 접근할 수 없습니다: " + e.getStatusCode());
            } else {
                throw new CustomException(ErrorCode.VIRTUAL_FITTING_API_ERROR, "이미지 서버에서 오류가 발생했습니다: " + e.getStatusCode());
            }
        } catch (Exception e) {
            log.error("[TryOnService] 이미지 URL 변환 실패 - url={}, error={}", imageUrl, e.getMessage(), e);
            throw new CustomException(ErrorCode.IMAGE_LOAD_ERROR, "이미지 URL을 Data URL로 변환하는데 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * URL에서 MIME 타입을 감지합니다.
     * 
     * @param imageUrl 이미지 URL
     * @return MIME 타입
     */
    private String detectMimeType(String imageUrl) {
        String lowerUrl = imageUrl.toLowerCase();
        
        if (lowerUrl.contains(".jpg") || lowerUrl.contains(".jpeg")) {
            return "image/jpeg";
        } else if (lowerUrl.contains(".png")) {
            return "image/png";
        } else if (lowerUrl.contains(".gif")) {
            return "image/gif";
        } else if (lowerUrl.contains(".webp")) {
            return "image/webp";
        } else if (lowerUrl.contains(".bmp")) {
            return "image/bmp";
        } else if (lowerUrl.contains(".svg")) {
            return "image/svg+xml";
        } else {
            // 기본값으로 JPEG 사용
            return "image/jpeg";
        }
    }
}