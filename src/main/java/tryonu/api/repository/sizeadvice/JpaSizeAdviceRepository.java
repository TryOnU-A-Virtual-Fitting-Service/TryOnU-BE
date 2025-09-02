package tryonu.api.repository.sizeadvice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tryonu.api.domain.SizeAdvice;

import java.util.List;
import java.util.Optional;

public interface JpaSizeAdviceRepository extends JpaRepository<SizeAdvice, Long> {

    /**
     * tryOnJobId로 사이즈 조언 조회
     */
    Optional<SizeAdvice> findByTryOnJobId(String tryOnJobId);

    /**
     * tryOnJobId로 삭제되지 않은 사이즈 조언 조회
     */
    Optional<SizeAdvice> findByTryOnJobIdAndIsDeletedFalse(String tryOnJobId);

    /**
     * tryOnJobId로 사이즈 조언 존재 여부 확인
     */
    boolean existsByTryOnJobId(String tryOnJobId);

    /**
     * tryOnJobId로 삭제되지 않은 사이즈 조언 존재 여부 확인
     */
    boolean existsByTryOnJobIdAndIsDeletedFalse(String tryOnJobId);

    /**
     * 사용자 ID로 사이즈 조언 조회
     */
    List<SizeAdvice> findByUser_Id(Long userId);

    /**
     * 사용자 ID로 삭제되지 않은 사이즈 조언 조회
     */
    List<SizeAdvice> findByUser_IdAndIsDeletedFalse(Long userId);

    /**
     * id와 삭제되지 않은 사이즈 조언 조회
     */
    Optional<SizeAdvice> findByIdAndIsDeletedFalse(Long id);

    /**
     * 사용자 ID로 사이즈 조언과 연결된 상세 정보 조회
     */
    @Query("SELECT s FROM SizeAdvice s " +
            "LEFT JOIN FETCH s.user u " +
            "WHERE s.user.id = :userId AND s.advice IS NOT NULL")
    List<SizeAdvice> findByUserIdWithDetails(@Param("userId") Long userId);

    /**
     * 사용자 ID로 삭제되지 않은 사이즈 조언과 연결된 상세 정보 조회
     */
    @Query("SELECT s FROM SizeAdvice s " +
            "LEFT JOIN FETCH s.user u " +
            "WHERE s.user.id = :userId AND s.isDeleted = false AND s.advice IS NOT NULL")
    List<SizeAdvice> findByUserIdWithDetailsAndIsDeletedFalse(@Param("userId") Long userId);
}
