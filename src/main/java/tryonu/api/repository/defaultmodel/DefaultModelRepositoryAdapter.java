package tryonu.api.repository.defaultmodel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.domain.DefaultModel;
import tryonu.api.dto.responses.DefaultModelDto;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DefaultModelRepositoryAdapter implements DefaultModelRepository {
    
    private final JpaDefaultModelRepository jpaDefaultModelRepository;
    
    @Override
    public DefaultModel save(@NonNull DefaultModel defaultModel) {
        DefaultModel savedDefaultModel = jpaDefaultModelRepository.save(defaultModel);
        log.debug("[DefaultModelRepositoryAdapter] 기본 모델 저장 - defaultModelId: {}", savedDefaultModel.getId());
        return savedDefaultModel;
    }
    
    @Override
    public DefaultModel findByIdAndIsDeletedFalseOrThrow(@NonNull Long id) {
        return jpaDefaultModelRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> {
                log.error("[DefaultModelRepositoryAdapter] 기본 모델을 찾을 수 없음 - id: {}", id);
                return new CustomException(ErrorCode.DEFAULT_MODEL_NOT_FOUND, 
                    String.format("기본 모델 ID '%d'에 해당하는 기본 모델을 찾을 수 없습니다.", id));
            });
    }
    
    @Override
    public List<DefaultModel> findAllByUserIdAndIsDeletedFalseOrThrow(@NonNull Long userId) {
        List<DefaultModel> defaultModels = jpaDefaultModelRepository.findAllByUser_IdAndIsDeletedFalse(userId);
        if (defaultModels.isEmpty()) {
            log.error("[DefaultModelRepositoryAdapter] 기본 모델을 찾을 수 없음 - userId: {}", userId);
            throw new CustomException(ErrorCode.DEFAULT_MODEL_NOT_FOUND, 
                String.format("사용자 ID '%d'에 해당하는 기본 모델을 찾을 수 없습니다.", userId));
        }
        log.debug("[DefaultModelRepositoryAdapter] 기본 모델 목록 조회 성공 - userId: {}, count: {}", userId, defaultModels.size());
        return defaultModels;
    }
    
    @Override
    public List<DefaultModel> findAllByUserIdAndIsDeletedFalse(@NonNull Long userId) {
        List<DefaultModel> defaultModels = jpaDefaultModelRepository.findAllByUser_IdAndIsDeletedFalse(userId);
        log.debug("[DefaultModelRepositoryAdapter] 삭제되지 않은 기본 모델 목록 조회 - userId: {}, count: {}", userId, defaultModels.size());
        return defaultModels;
    }
    
    @Override
    public List<DefaultModelDto> findDefaultModelsByUserIdOrderByIdDesc(@NonNull Long userId) {
        List<DefaultModelDto> defaultModels = jpaDefaultModelRepository.findDefaultModelsByUserIdOrderByIdDesc(userId);
        log.debug("[DefaultModelRepositoryAdapter] 기본 모델 목록 조회 성공 (정렬) - userId: {}, count: {}", userId, defaultModels.size());
        return defaultModels;
    }
    
    @Override
    public void softDelete(@NonNull DefaultModel defaultModel) {
        defaultModel.setIsDeleted(true);
        jpaDefaultModelRepository.save(defaultModel);
        log.debug("[DefaultModelRepositoryAdapter] 기본 모델 소프트 삭제 성공 - defaultModelId: {}", defaultModel.getId());
    }
} 