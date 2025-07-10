package tryonu.api.repository.fittingmodel;

import org.springframework.data.jpa.repository.JpaRepository;
import tryonu.api.domain.FittingModel;

public interface JpaFittingModelRepository extends JpaRepository<FittingModel, Long> {
    
    /**
     * userId로 피팅 모델 조회
     */
    FittingModel findAllByUser_Id(Long userId);
} 