package tryonu.api.repository.fittingmodel;

import org.springframework.data.jpa.repository.JpaRepository;
import tryonu.api.domain.FittingModel;

import java.util.Optional;

public interface JpaFittingModelRepository extends JpaRepository<FittingModel, Long> {
    

    
    /**
     * id와 삭제되지 않은 피팅 모델 조회
     */
    Optional<FittingModel> findByIdAndIsDeletedFalse(Long id);
    
    /**
     * userId로 삭제되지 않은 피팅 모델 조회
     */
    FittingModel findAllByUser_IdAndIsDeletedFalse(Long userId);
} 