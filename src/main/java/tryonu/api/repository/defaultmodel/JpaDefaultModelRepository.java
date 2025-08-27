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
     * 사용자별 기본 모델 조회 (sortOrder 오름차순, id 오름차순 정렬) - JPQL 최적화
     */
    @Query("SELECT new tryonu.api.dto.responses.DefaultModelDto(dm.id, dm.imageUrl, dm.modelName, dm.sortOrder, dm.isCustom) " +
           "FROM DefaultModel dm " +
           "WHERE dm.user.id = :userId AND dm.isDeleted = false " +
           "ORDER BY dm.sortOrder ASC, dm.id ASC")
    List<DefaultModelDto> findDefaultModelsByUserIdOrderBySortOrder(@Param("userId") Long userId);
    
    /**
     * ID 목록으로 사용자의 기본 모델들 조회
     */
    @Query("SELECT dm FROM DefaultModel dm " +
           "WHERE dm.id IN :ids AND dm.user.id = :userId AND dm.isDeleted = false")
    List<DefaultModel> findAllByIdsAndUserIdAndIsDeletedFalse(@Param("ids") List<Long> ids, @Param("userId") Long userId);
    
    /**
     * 사용자의 최대 sortOrder 조회
     */
    @Query("SELECT COALESCE(MAX(dm.sortOrder), 0) FROM DefaultModel dm " +
           "WHERE dm.user.id = :userId AND dm.isDeleted = false")
    Integer findMaxSortOrderByUserId(@Param("userId") Long userId);
} 