package tryonu.api.repository.defaultmodel;

import org.springframework.lang.NonNull;
import tryonu.api.domain.DefaultModel;
import tryonu.api.dto.responses.DefaultModelDto;

import java.util.List;

public interface DefaultModelRepository {
    
    /**
     * 기본 모델 저장 (비즈니스 로직 포함)
     */
    DefaultModel save(@NonNull DefaultModel defaultModel);
    
    /**
     * 기본 모델 ID로 조회 (예외처리 포함)
     */
    DefaultModel findByIdAndIsDeletedFalseOrThrow(@NonNull Long id);
    
    /**
     * userId로 기본 모델 목록 조회 (예외처리 포함)
     */
    List<DefaultModel> findAllByUserIdAndIsDeletedFalseOrThrow(@NonNull Long userId);
    
    /**
     * userId로 삭제되지 않은 기본 모델 목록 조회
     */
    List<DefaultModel> findAllByUserIdAndIsDeletedFalse(@NonNull Long userId);
    
    /**
     * 사용자별 기본 모델 목록 조회 (sortOrder 오름차순, id 오름차순 정렬)
     */
    List<DefaultModelDto> findDefaultModelsByUserIdOrderBySortOrder(@NonNull Long userId);
    
    /**
     * ID 목록으로 사용자의 기본 모델들 조회
     */
    List<DefaultModel> findAllByIdsAndUserIdAndIsDeletedFalse(@NonNull List<Long> ids, @NonNull Long userId);
    
    /**
     * 기본 모델 소프트 삭제 (예외처리 포함)
     */
    void softDelete(@NonNull DefaultModel defaultModel);
    
    /**
     * 기본 모델 일괄 저장
     */
    List<DefaultModel> saveAll(@NonNull List<DefaultModel> defaultModels);
    
    /**
     * 사용자의 최대 sortOrder 조회
     */
    Integer findMaxSortOrderByUserId(@NonNull Long userId);
} 