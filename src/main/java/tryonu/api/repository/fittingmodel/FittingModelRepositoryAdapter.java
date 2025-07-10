package tryonu.api.repository.fittingmodel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.domain.FittingModel;


@Slf4j
@Repository
@RequiredArgsConstructor
public class FittingModelRepositoryAdapter implements FittingModelRepository {
    
    private final JpaFittingModelRepository jpaFittingModelRepository;
    
    @Override
    public FittingModel save(@NonNull FittingModel fittingModel) {
        FittingModel savedFittingModel = jpaFittingModelRepository.save(fittingModel);
        log.debug("[FittingModelRepositoryAdapter] 피팅 모델 저장 - fittingModelId: {}", savedFittingModel.getId());
        return savedFittingModel;
    }
    
    @Override
    public FittingModel findByIdOrThrow(@NonNull Long id) {
        return jpaFittingModelRepository.findById(id)
            .orElseThrow(() -> {
                log.error("[FittingModelRepositoryAdapter] 피팅 모델을 찾을 수 없음 - id: {}", id);
                return new CustomException(ErrorCode.FITTING_MODEL_NOT_FOUND, 
                    String.format("피팅 모델 ID '%d'에 해당하는 피팅 모델을 찾을 수 없습니다.", id));
            });
    }
    
    @Override
    public FittingModel findAllByUserIdOrThrow(@NonNull Long userId) {
        FittingModel fittingModel = jpaFittingModelRepository.findAllByUser_Id(userId);
        if (fittingModel == null) {
            log.error("[FittingModelRepositoryAdapter] 피팅 모델을 찾을 수 없음 - userId: {}", userId);
            throw new CustomException(ErrorCode.FITTING_MODEL_NOT_FOUND, 
                String.format("사용자 ID '%d'에 해당하는 피팅 모델을 찾을 수 없습니다.", userId));
        }
        log.debug("[FittingModelRepositoryAdapter] 피팅 모델 조회 성공 - userId: {}", userId);
        return fittingModel;
    }
    

    
    @Override
    public void softDelete(@NonNull FittingModel fittingModel) {
        fittingModel.setIsDeleted(true);
        jpaFittingModelRepository.save(fittingModel);
        log.debug("[FittingModelRepositoryAdapter] 피팅 모델 소프트 삭제 성공 - fittingModelId: {}", fittingModel.getId());
    }
} 