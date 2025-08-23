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
    public List<DefaultModelDto> findDefaultModelsByUserIdOrderBySortOrder(@NonNull Long userId) {
        List<DefaultModelDto> defaultModels = jpaDefaultModelRepository.findDefaultModelsByUserIdOrderBySortOrder(userId);
        log.debug("[DefaultModelRepositoryAdapter] 기본 모델 목록 조회 성공 (sortOrder 정렬) - userId: {}, count: {}", userId, defaultModels.size());
        return defaultModels;
    }
    
    /**
     * ID 목록으로 사용자의 기본 모델들 조회
     * 
     * <p>빈 ID 리스트가 전달되는 경우 JPA "IN ()" 쿼리 오류를 방지하기 위해
     * 즉시 빈 결과를 반환합니다. 또한 중복된 ID를 제거하여 정확한 결과를 보장합니다.</p>
     * 
     * @param ids 조회할 기본 모델 ID 목록
     * @param userId 사용자 ID
     * @return 해당 ID들에 해당하는 기본 모델 목록 (빈 리스트인 경우 빈 결과 반환)
     */
    @Override
    public List<DefaultModel> findAllByIdsAndUserIdAndIsDeletedFalse(@NonNull List<Long> ids, @NonNull Long userId) {
        if (ids.isEmpty()) {
            log.debug("[DefaultModelRepositoryAdapter] ID 목록이 비어 있음 - userId: {}, ids: []", userId);
            return List.of();
        }
        
        List<Long> distinctIds = ids.stream().distinct().toList();
        List<DefaultModel> defaultModels = jpaDefaultModelRepository.findAllByIdsAndUserIdAndIsDeletedFalse(distinctIds, userId);
        log.debug("[DefaultModelRepositoryAdapter] ID 목록으로 기본 모델 조회 - userId: {}, ids: {}, found: {}", userId, distinctIds, defaultModels.size());
        return defaultModels;
    }
    
    @Override
    public void softDelete(@NonNull DefaultModel defaultModel) {
        defaultModel.setIsDeleted(true);
        jpaDefaultModelRepository.save(defaultModel);
        log.debug("[DefaultModelRepositoryAdapter] 기본 모델 소프트 삭제 성공 - defaultModelId: {}", defaultModel.getId());
    }
    
    @Override
    public List<DefaultModel> saveAll(@NonNull List<DefaultModel> defaultModels) {
        List<DefaultModel> savedModels = jpaDefaultModelRepository.saveAll(defaultModels);
        log.debug("[DefaultModelRepositoryAdapter] 기본 모델 일괄 저장 성공 - count: {}", savedModels.size());
        return savedModels;
    }
    
    @Override
    public Integer findMaxSortOrderByUserId(@NonNull Long userId) {
        Integer maxSortOrder = jpaDefaultModelRepository.findMaxSortOrderByUserId(userId);
        log.debug("[DefaultModelRepositoryAdapter] 최대 sortOrder 조회 - userId: {}, maxSortOrder: {}", userId, maxSortOrder);
        return maxSortOrder;
    }
} 