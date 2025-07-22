package tryonu.api.repository.fittingmodel;

import org.springframework.lang.NonNull;
import tryonu.api.domain.FittingModel;
import tryonu.api.dto.responses.FittingModelDto;

import java.util.List;


public interface FittingModelRepository {
    
    /**
     * 피팅 모델 저장 (비즈니스 로직 포함)
     */
    FittingModel save(@NonNull FittingModel fittingModel);
    
    /**
     * 피팅 모델 ID로 조회 (예외처리 포함)
     */
    FittingModel findByIdAndIsDeletedFalseOrThrow(@NonNull Long id);
    
    /**
     * userId로 피팅 모델 조회 (예외처리 포함)
     */
    FittingModel findAllByUserIdAndIsDeletedFalseOrThrow(@NonNull Long userId);
    
    /**
     * 사용자별 피팅 모델 목록 조회 (id 내림차순 정렬)
     */
    List<FittingModelDto> findFittingModelsByUserIdOrderByIdDesc(@NonNull Long userId);
    
    /**
     * 피팅 모델 소프트 삭제 (예외처리 포함)
     */
    void softDelete(@NonNull FittingModel fittingModel);
} 