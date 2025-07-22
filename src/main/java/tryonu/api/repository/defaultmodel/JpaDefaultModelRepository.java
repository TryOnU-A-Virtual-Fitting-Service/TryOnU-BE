package tryonu.api.repository.defaultmodel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tryonu.api.domain.DefaultModel;
import tryonu.api.dto.responses.DefaultModelDto;

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
    
    /**
     * 사용자별 기본 모델 조회 (id 내림차순 정렬) - JPQL 최적화
     */
    @Query("SELECT new tryonu.api.dto.responses.DefaultModelDto(dm.id, dm.imageUrl) " +
           "FROM DefaultModel dm " +
           "WHERE dm.user.id = :userId AND dm.isDeleted = false " +
           "ORDER BY dm.id DESC")
    List<DefaultModelDto> findDefaultModelsByUserIdOrderByIdDesc(@Param("userId") Long userId);
} 