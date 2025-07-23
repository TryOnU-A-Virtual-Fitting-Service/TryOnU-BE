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
import tryonu.api.domain.FittingModel;
import tryonu.api.domain.Cloth;
import tryonu.api.repository.fittingmodel.FittingModelRepository;   
import tryonu.api.repository.tryonresult.TryOnResultRepository;
import tryonu.api.repository.cloth.ClothRepository;
import tryonu.api.common.util.BackgroundRemovalUtil;
import tryonu.api.common.util.VirtualFittingUtil;
import tryonu.api.common.util.ImageUploadUtil;
import tryonu.api.common.util.CategoryPredictionUtil;
import tryonu.api.dto.responses.CategoryPredictionResponse;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.domain.TryOnResult;
import tryonu.api.converter.TryOnConverter;
import tryonu.api.common.enums.Category;

import java.util.Arrays;


/**
 * 가상 피팅 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TryOnServiceImpl implements TryOnService {

    private final VirtualFittingUtil virtualFittingUtil;
    private final BackgroundRemovalUtil backgroundRemovalUtil;
    private final ImageUploadUtil imageUploadUtil;
    private final CategoryPredictionUtil categoryPredictionUtil;
    private final FittingModelRepository fittingModelRepository;
    private final TryOnResultRepository tryOnResultRepository;
    private final ClothRepository clothRepository;
    private final TryOnConverter tryOnConverter;


    @Value("${virtual-fitting.polling.max-wait-time-ms:60000}")  // 기본 1분
    private long maxWaitTimeMs;
    
    @Value("${virtual-fitting.polling.interval-ms:1000}")        // 기본 1초 간격
    private long pollIntervalMs;

    

    @Override
    public TryOnResponse tryOn(Long fittingModelId, String productPageUrl, MultipartFile file) {
        log.info("[TryOnService] 가상 피팅 시작 - fittingModelId={}", fittingModelId);
        
        // 의류 이미지 카테고리 예측
        CategoryPredictionResponse categoryPredictionResponse = categoryPredictionUtil.predictCategory(file);
        log.info("[TryOnService] 카테고리 예측 완료 - className={}, confidence={}", 
                categoryPredictionResponse.className(), categoryPredictionResponse.confidence());
        
        // 지원하지 않는 카테고리 검증
        validateSupportedCategory(categoryPredictionResponse.className());

        // 모델 조회
        FittingModel fittingModel = fittingModelRepository.findByIdAndIsDeletedFalseOrThrow(fittingModelId);
        String fittingModelImageUrl = fittingModel.getImageUrl();

        // 의류 이미지 업로드
        String clothImageUrl = imageUploadUtil.uploadClothImage(file);

        // 가상 피팅 API 요청 생성
        VirtualFittingRequest virtualFittingRequest = tryOnConverter.toVirtualFittingRequest(fittingModelImageUrl, clothImageUrl);

        // 가상 피팅 실행 (폴링 방식)
        VirtualFittingResponse virtualFittingResponse = virtualFittingUtil.runVirtualFitting(virtualFittingRequest);

        // 폴링으로 완료 대기
        VirtualFittingStatusResponse finalStatus = virtualFittingUtil.waitForCompletion(virtualFittingResponse.id(), maxWaitTimeMs, pollIntervalMs);

        // 결과 처리
        if ("completed".equals(finalStatus.status())) {
            // 안전한 결과 추출 - null/empty 체크
            if (finalStatus.output() == null || finalStatus.output().isEmpty()) {
                log.error("[TryOnService] 가상 피팅 완료되었으나 결과 이미지가 없음 - fittingModelId={}, output={}", 
                        fittingModelId, finalStatus.output());
                throw new CustomException(ErrorCode.VIRTUAL_FITTING_FAILED, "가상피팅이 완료되었으나 결과 이미지를 받지 못했습니다.");
            }
            
            String resultImageUrl = finalStatus.output().get(0);  // 첫 번째 결과 이미지

            log.info("[TryOnService] 가상 피팅 성공 - fittingModelId={}, resultUrl={}", 
                    fittingModelId, resultImageUrl);

            // 의류 저장 - 안전한 카테고리 변환
            Category category = parseCategory(categoryPredictionResponse.className());
            Cloth cloth = tryOnConverter.toClothEntity(clothImageUrl, productPageUrl, category);
            clothRepository.save(cloth);

            // 피팅 결과 저장
            byte[] backgroundRemovedImage = backgroundRemovalUtil.removeBackground(resultImageUrl);
            String backgroundRemovedImageUrl = imageUploadUtil.uploadTryOnResultImage(backgroundRemovedImage);
            
            TryOnResult tryOnResult = tryOnConverter.toTryOnResultEntity(cloth, fittingModel, backgroundRemovedImageUrl);
            tryOnResultRepository.save(tryOnResult);

            // 피팅 모델 상태 업데이트
            fittingModel.updateImageUrl(backgroundRemovedImageUrl);
            FittingModel updatedFittingModel = fittingModelRepository.save(fittingModel);
            
            return tryOnConverter.toTryOnResponse(updatedFittingModel.getImageUrl());
        } else {
            log.error("[TryOnService] 가상 피팅 실패 - fittingModelId={}, error={}", 
                    fittingModelId, finalStatus.error());
            throw new CustomException(
                ErrorCode.VIRTUAL_FITTING_FAILED, 
                finalStatus.error() != null ? finalStatus.error().message() : "알 수 없는 오류"
            );
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
} 