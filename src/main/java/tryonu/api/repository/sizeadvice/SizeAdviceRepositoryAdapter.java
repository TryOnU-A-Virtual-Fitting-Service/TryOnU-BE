package tryonu.api.repository.sizeadvice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.domain.SizeAdvice;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SizeAdviceRepositoryAdapter implements SizeAdviceRepository {

    private final JpaSizeAdviceRepository jpaSizeAdviceRepository;

    @Override
    public SizeAdvice save(@NonNull SizeAdvice sizeAdvice) {
        SizeAdvice savedSizeAdvice = jpaSizeAdviceRepository.save(sizeAdvice);
        log.debug("[SizeAdviceRepositoryAdapter] 사이즈 조언 저장 - tryOnJobId: {}", savedSizeAdvice.getTryOnJobId());
        return savedSizeAdvice;
    }

    @Override
    public SizeAdvice findByIdAndIsDeletedFalseOrThrow(@NonNull Long sizeAdviceId) {
        return jpaSizeAdviceRepository.findByIdAndIsDeletedFalse(sizeAdviceId)
                .orElseThrow(() -> {
                    log.error("[SizeAdviceRepositoryAdapter] 사이즈 조언을 찾을 수 없음 - sizeAdviceId: {}", sizeAdviceId);
                    return new CustomException(ErrorCode.RESOURCE_NOT_FOUND,
                            String.format("사이즈 조언 ID '%d'에 해당하는 사이즈 조언을 찾을 수 없습니다.", sizeAdviceId));
                });
    }

    @Override
    public Optional<SizeAdvice> findByTryOnJobId(@NonNull String tryOnJobId) {
        return jpaSizeAdviceRepository.findByTryOnJobId(tryOnJobId);
    }

    @Override
    public SizeAdvice findByTryOnJobIdAndIsDeletedFalseOrThrow(@NonNull String tryOnJobId) {
        return jpaSizeAdviceRepository.findByTryOnJobIdAndIsDeletedFalse(tryOnJobId)
                .orElseThrow(() -> {
                    log.error("[SizeAdviceRepositoryAdapter] 사이즈 조언을 찾을 수 없음 - tryOnJobId: {}", tryOnJobId);
                    return new CustomException(ErrorCode.RESOURCE_NOT_FOUND,
                            String.format("tryOnJobId '%s'에 해당하는 사이즈 조언을 찾을 수 없습니다.", tryOnJobId));
                });
    }

    @Override
    public boolean existsByTryOnJobIdAndIsDeletedFalse(@NonNull String tryOnJobId) {
        boolean exists = jpaSizeAdviceRepository.existsByTryOnJobIdAndIsDeletedFalse(tryOnJobId);
        log.debug("[SizeAdviceRepositoryAdapter] 사이즈 조언 존재 여부 확인 - tryOnJobId: {}, exists: {}", tryOnJobId, exists);
        return exists;
    }

    @Override
    public List<SizeAdvice> findByUserId(@NonNull Long userId) {
        return jpaSizeAdviceRepository.findByUser_Id(userId);
    }

    @Override
    public List<SizeAdvice> findByUserIdAndIsDeletedFalse(@NonNull Long userId) {
        return jpaSizeAdviceRepository.findByUser_IdAndIsDeletedFalse(userId);
    }

    @Override
    public List<SizeAdvice> findByUserIdWithDetails(@NonNull Long userId) {
        return jpaSizeAdviceRepository.findByUserIdWithDetails(userId);
    }

    @Override
    public List<SizeAdvice> findByUserIdWithDetailsAndIsDeletedFalse(@NonNull Long userId) {
        return jpaSizeAdviceRepository.findByUserIdWithDetailsAndIsDeletedFalse(userId);
    }

    @Override
    public void softDelete(@NonNull SizeAdvice sizeAdvice) {
        sizeAdvice.setIsDeleted(true);
        jpaSizeAdviceRepository.save(sizeAdvice);
        log.debug("[SizeAdviceRepositoryAdapter] 사이즈 조언 소프트 삭제 - tryOnJobId: {}", sizeAdvice.getTryOnJobId());
    }

}
