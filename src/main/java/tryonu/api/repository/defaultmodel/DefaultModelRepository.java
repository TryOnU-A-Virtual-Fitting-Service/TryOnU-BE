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
     * 사용자별 기본 모델 목록 조회 (id 내림차순 정렬)
     */
    List<DefaultModelDto> findDefaultModelsByUserIdOrderByIdDesc(@NonNull Long userId);
    
    /**
     * 기본 모델 소프트 삭제 (예외처리 포함)
     */
    void softDelete(@NonNull DefaultModel defaultModel);
} 