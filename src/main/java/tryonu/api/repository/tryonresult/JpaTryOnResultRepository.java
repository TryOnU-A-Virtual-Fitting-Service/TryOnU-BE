package tryonu.api.repository.tryonresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tryonu.api.domain.TryOnResult;
import tryonu.api.dto.responses.TryOnResultDto;

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
    
    /**
     * 사용자별 피팅 결과 조회 (id 내림차순 정렬) - JPQL 최적화
     */
    @Query("SELECT new tryonu.api.dto.responses.TryOnResultDto(tr.id, tr.imageUrl) " +
           "FROM TryOnResult tr " +
           "WHERE tr.user.id = :userId AND tr.isDeleted = false " +
           "ORDER BY tr.id DESC")
    List<TryOnResultDto> findTryOnResultsByUserIdOrderByIdDesc(@Param("userId") Long userId);
} 