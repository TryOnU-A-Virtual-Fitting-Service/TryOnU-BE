package tryonu.api.service.tryon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tryonu.api.dto.requests.VirtualFittingRequest;
import tryonu.api.dto.responses.TryOnResponse;
import tryonu.api.dto.responses.VirtualFittingResponse;
import tryonu.api.dto.responses.VirtualFittingStatusResponse;
import tryonu.api.domain.Cloth;
import tryonu.api.domain.User;
import tryonu.api.domain.DefaultModel;
import tryonu.api.repository.tryonresult.TryOnResultRepository;
import tryonu.api.repository.cloth.ClothRepository;
import tryonu.api.repository.user.UserRepository;
import tryonu.api.common.util.VirtualFittingUtil;
import tryonu.api.common.util.ImageUploadUtil;
import tryonu.api.common.util.CategoryPredictionUtil;
import tryonu.api.common.util.MemoryTracker;
import tryonu.api.dto.responses.CategoryPredictionResponse;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.domain.TryOnResult;
import tryonu.api.converter.TryOnResultConverter;
import tryonu.api.common.enums.Category;
import tryonu.api.common.auth.SecurityUtils;
import tryonu.api.dto.responses.TryOnResultDto;
import tryonu.api.dto.responses.UserInfoResponse;
import tryonu.api.repository.defaultmodel.DefaultModelRepository;
import tryonu.api.converter.UserConverter;
import tryonu.api.dto.responses.DefaultModelDto;

import java.util.Arrays;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;


/**
 * 가상 피팅 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TryOnServiceImpl implements TryOnService {

    private final VirtualFittingUtil virtualFittingUtil;
    private final MemoryTracker memoryTracker;
    private final ImageUploadUtil imageUploadUtil;
    private final CategoryPredictionUtil categoryPredictionUtil;
    private final TryOnResultRepository tryOnResultRepository;
    private final ClothRepository clothRepository;
    private final UserRepository userRepository;
    private final TryOnResultConverter tryOnResultConverter;
    private final DefaultModelRepository defaultModelRepository;
    private final UserConverter userConverter;

    @Value("${virtual-fitting.polling.max-wait-time-ms:60000}")  // 기본 1분
    private long maxWaitTimeMs;
    
    @Value("${virtual-fitting.polling.interval-ms:1000}")        // 기본 1초 간격
    private long pollIntervalMs;

    

    @Override
    public TryOnResponse tryOn(String modelUrl, Long defaultModelId, String productPageUrl, MultipartFile file) {
        log.info("[TryOnService] 가상 피팅 시작 - modelUrl={}, defaultModelId={}, productPageUrl={}", modelUrl, defaultModelId, productPageUrl);
        
        // 현재 인증된 사용자 조회
        User currentUser = SecurityUtils.getCurrentUser();
        
        
        
        // 전체 가상 피팅 프로세스 메모리 추적 시작
        String fileSizeStr = String.format("%.1fMB", file.getSize() / 1024.0 / 1024.0);
        memoryTracker.startTracking("VirtualFitting-Process", fileSizeStr);
        memoryTracker.logCurrentMemoryStatus("가상 피팅 프로세스 시작");
        
        try {
            // 의류 이미지 카테고리 예측
        CategoryPredictionResponse categoryPredictionResponse = categoryPredictionUtil.predictCategory(file);
        log.info("[TryOnService] 카테고리 예측 완료 - className={}, confidence={}", categoryPredictionResponse.className(), categoryPredictionResponse.confidence());
        
        // 지원하지 않는 카테고리 검증
        validateSupportedCategory(categoryPredictionResponse.className());

        // 의류 이미지 업로드
        String clothImageUrl = imageUploadUtil.uploadClothImage(file);

        // 가상 피팅 API 요청 생성
        VirtualFittingRequest virtualFittingRequest = tryOnResultConverter.toVirtualFittingRequest(modelUrl, clothImageUrl);

        // 가상 피팅 실행 (폴링 방식)
        VirtualFittingResponse virtualFittingResponse = virtualFittingUtil.runVirtualFitting(virtualFittingRequest);

        // 폴링으로 완료 대기
        VirtualFittingStatusResponse finalStatus = virtualFittingUtil.waitForCompletion(virtualFittingResponse.id(), maxWaitTimeMs, pollIntervalMs);

        // 결과 처리
        if ("completed".equals(finalStatus.status())) {
            // 안전한 결과 추출 - null/empty 체크
            if (finalStatus.output() == null || finalStatus.output().isEmpty()) {
                log.error("[TryOnService] 가상 피팅 완료되었으나 결과 이미지가 없음 - modelUrl={}, output={}", modelUrl, finalStatus.output());
                throw new CustomException(ErrorCode.VIRTUAL_FITTING_FAILED, "가상피팅이 완료되었으나 결과 이미지를 받지 못했습니다.");
            }
            
            String resultImageUrl = finalStatus.output().get(0);  // 첫 번째 결과 이미지
            
            // fashn.ai 결과 이미지를 다운로드하여 S3에 업로드
            String uploadedResultImageUrl = imageUploadUtil.uploadTryOnResultImageFromUrl(resultImageUrl);
            log.info("[TryOnService] 가상 피팅 결과 S3 업로드 완료 - originalUrl={}, s3Url={}", resultImageUrl, uploadedResultImageUrl);

            // 의류 저장 - 안전한 카테고리 변환
            Category category = parseCategory(categoryPredictionResponse.className());
            Cloth cloth = tryOnResultConverter.toClothEntity(clothImageUrl, productPageUrl, category);
            clothRepository.save(cloth);

            // 피팅 결과 저장 (defaultModelId 포함, S3 URL 사용)
            TryOnResult tryOnResult = tryOnResultConverter.toTryOnResultEntity(cloth, currentUser, modelUrl, uploadedResultImageUrl, virtualFittingResponse.id(), defaultModelId);
            tryOnResultRepository.save(tryOnResult);
            
<<<<<<< HEAD
            // 사용자의 최근 사용한 모델 URL과 modelName 업데이트 (S3 URL 사용)
            DefaultModel defaultModel = defaultModelRepository.findByIdAndIsDeletedFalseOrThrow(defaultModelId);
            currentUser.updateRecentlyUsedModelUrl(uploadedResultImageUrl);
            currentUser.updateRecentlyUsedModelName(defaultModel.getModelName());   

=======
            // 사용자의 최근 사용한 모델 URL과 modelName 업데이트
            currentUser.updateRecentlyUsedModelUrl(resultImageUrl);
            currentUser.updateRecentlyUsedModelName(modelName);
>>>>>>> 59ea5d7fa344c751b55d1c7729df9daa9066c521
            userRepository.save(currentUser);
            log.info("[TryOnService] 사용자 최근 사용 모델 정보 업데이트 완료 - userId={}, recentlyUsedModelUrl={}, modelName={}", 
                    currentUser.getId(), uploadedResultImageUrl, defaultModel.getModelName());
            
            // 메모리 추적 종료 (성공)
            memoryTracker.endTracking("VirtualFitting-Process", true);
            memoryTracker.logCurrentMemoryStatus("가상 피팅 프로세스 완료");
            
            return tryOnResultConverter.toTryOnResponse(tryOnResult, defaultModel.getModelName());    
        } else {    
            // 메모리 추적 종료 (실패)
            memoryTracker.endTracking("VirtualFitting-Process", false);
            
            // fashn.ai API 에러 구체적 로깅 및 처리
            handleVirtualFittingError(modelUrl, finalStatus);
            throw new RuntimeException("This should never be reached"); // handleVirtualFittingError always throws
        }
        } catch (Exception e) {
            // 예외 발생시 메모리 추적 종료
            memoryTracker.endTracking("VirtualFitting-Process", false);
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
        log.error("[TryOnService] fashn.ai API 가상 피팅 실패 - modelUrl={}, status={}, errorName={}, errorMessage={}", modelUrl, finalStatus.status(), errorName, errorMessage);

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
                log.error("[TryOnService] 알 수 없는 fashn.ai API 에러 - errorName={}, errorMessage={}", errorName, errorMessage);
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
        List<DefaultModelDto> defaultModels = defaultModelRepository.findDefaultModelsByUserIdOrderBySortOrder(currentUserId);
        List<TryOnResultDto> tryOnResults = tryOnResultRepository.findTryOnResultsByUserIdOrderByIdDesc(currentUserId);
        return userConverter.toUserInfoResponse(defaultModels, tryOnResults);
    }
} 