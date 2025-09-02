package tryonu.api.repository.sizeadvice;

import org.springframework.lang.NonNull;
import tryonu.api.domain.SizeAdvice;
import java.util.List;
import java.util.Optional;

public interface SizeAdviceRepository {

    /**
     * 사이즈 조언 저장 (로깅 포함)
     */
    SizeAdvice save(@NonNull SizeAdvice sizeAdvice);

    /**
     * 사이즈 조언 ID로 조회 (예외처리 포함)
     */
    SizeAdvice findByIdAndIsDeletedFalseOrThrow(@NonNull Long sizeAdviceId);

    /**
     * tryOnJobId로 사이즈 조언 조회 (일반 조회)
     */
    Optional<SizeAdvice> findByTryOnJobId(@NonNull String tryOnJobId);

    /**
     * tryOnJobId로 사이즈 조언 조회 (예외처리 포함)
     */
    SizeAdvice findByTryOnJobIdAndIsDeletedFalseOrThrow(@NonNull String tryOnJobId);

    /**
     * tryOnJobId로 사이즈 조언 존재 여부 확인
     */
    boolean existsByTryOnJobIdAndIsDeletedFalse(@NonNull String tryOnJobId);

    /**
     * 사용자 ID로 사이즈 조언 목록 조회
     */
    List<SizeAdvice> findByUserId(@NonNull Long userId);

    /**
     * 사용자 ID로 삭제되지 않은 사이즈 조언 목록 조회
     */
    List<SizeAdvice> findByUserIdAndIsDeletedFalse(@NonNull Long userId);

    /**
     * 사용자 ID로 사이즈 조언과 상세 정보 조회
     */
    List<SizeAdvice> findByUserIdWithDetails(@NonNull Long userId);

    /**
     * 사용자 ID로 삭제되지 않은 사이즈 조언과 상세 정보 조회
     */
    List<SizeAdvice> findByUserIdWithDetailsAndIsDeletedFalse(@NonNull Long userId);

    /**
     * 사이즈 조언 소프트 삭제 (예외처리 포함)
     */
    void softDelete(@NonNull SizeAdvice sizeAdvice);

}
