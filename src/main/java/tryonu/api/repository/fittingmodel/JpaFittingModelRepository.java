package tryonu.api.repository.fittingmodel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tryonu.api.domain.FittingModel;
import tryonu.api.dto.responses.FittingModelDto;

import java.util.List;
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
    
    /**
     * 사용자별 피팅 모델 조회 (id 내림차순 정렬) - JPQL 최적화
     */
    @Query("SELECT new tryonu.api.dto.responses.FittingModelDto(fm.id, fm.imageUrl) " +
           "FROM FittingModel fm " +
           "WHERE fm.user.id = :userId AND fm.isDeleted = false " +
           "ORDER BY fm.id DESC")
    List<FittingModelDto> findFittingModelsByUserIdOrderByIdDesc(@Param("userId") Long userId);
} 