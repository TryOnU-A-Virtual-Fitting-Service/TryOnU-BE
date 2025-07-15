package tryonu.api.repository.defaultmodel;

import org.springframework.data.jpa.repository.JpaRepository;
import tryonu.api.domain.DefaultModel;

import java.util.List;
import java.util.Optional;

public interface JpaDefaultModelRepository extends JpaRepository<DefaultModel, Long> {
    


    /**
     * id와 삭제되지 않은 기본 모델 조회
     */
    Optional<DefaultModel> findByIdAndIsDeletedFalse(Long id);
    
    /**
     * userId로 삭제되지 않은 기본 모델 목록 조회
     */
    List<DefaultModel> findAllByUser_IdAndIsDeletedFalse(Long userId);
} 