package tryonu.api.repository.tryonresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tryonu.api.domain.TryOnResult;

import java.util.List;
import java.util.Optional;

public interface JpaTryOnResultRepository extends JpaRepository<TryOnResult, Long> {
    
    /**
     * 사용자 ID로 피팅 결과 조회
     */
    List<TryOnResult> findByUser_Id(Long userId);
    
    /**
     * 사용자 ID로 삭제되지 않은 피팅 결과 조회
     */
    List<TryOnResult> findByUser_IdAndIsDeletedFalse(Long userId);
    
    /**
     * id와 삭제되지 않은 피팅 결과 조회
     */
    Optional<TryOnResult> findByIdAndIsDeletedFalse(Long id);
    
    /**
     * 사용자 ID로 피팅 결과와 연결된 상세 정보 조회
     */
    @Query("SELECT t FROM TryOnResult t " +
           "LEFT JOIN FETCH t.fittingModel f " +
           "LEFT JOIN FETCH t.cloth c " +
           "WHERE t.user.id = :userId")
    List<TryOnResult> findByUserIdWithDetails(@Param("userId") Long userId);
    
    /**
     * 사용자 ID로 삭제되지 않은 피팅 결과와 연결된 상세 정보 조회
     */
    @Query("SELECT t FROM TryOnResult t " +
           "LEFT JOIN FETCH t.fittingModel f " +
           "LEFT JOIN FETCH t.cloth c " +
           "WHERE t.user.id = :userId AND t.isDeleted = false")
    List<TryOnResult> findByUserIdWithDetailsAndIsDeletedFalse(@Param("userId") Long userId);
} 