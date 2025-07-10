package tryonu.api.repository.tryonresult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.domain.TryOnResult;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TryOnResultRepositoryAdapter implements TryOnResultRepository {
    
    private final JpaTryOnResultRepository jpaTryOnResultRepository;
    
    @Override
    public TryOnResult save(@NonNull TryOnResult tryOnResult) {
        TryOnResult savedTryOnResult = jpaTryOnResultRepository.save(tryOnResult);
        log.debug("[TryOnResultRepositoryAdapter] 피팅 결과 저장 - tryOnResultId: {}, userId: {}", 
            savedTryOnResult.getId(), savedTryOnResult.getUser().getId());
        return savedTryOnResult;
    }
    
    @Override
    public TryOnResult findByIdOrThrow(@NonNull Long id) {
        return jpaTryOnResultRepository.findById(id)
            .orElseThrow(() -> {
                log.error("[TryOnResultRepositoryAdapter] 피팅 결과를 찾을 수 없음 - id: {}", id);
                return new CustomException(ErrorCode.TRY_ON_RESULT_NOT_FOUND, 
                    String.format("피팅 결과 ID '%d'에 해당하는 피팅 결과를 찾을 수 없습니다.", id));
            });
    }
    
    @Override
    public List<TryOnResult> findAllByUserIdOrThrow(@NonNull Long userId) {
        List<TryOnResult> tryOnResults = jpaTryOnResultRepository.findByUser_Id(userId);
        log.debug("[TryOnResultRepositoryAdapter] 사용자별 피팅 결과 조회 - userId: {}, count: {}", userId, tryOnResults.size());
        return tryOnResults;
    }
    
    @Override
    public List<TryOnResult> findAllByUserIdWithDetailsOrThrow(@NonNull Long userId) {
        List<TryOnResult> tryOnResults = jpaTryOnResultRepository.findByUser_Id(userId);
        log.debug("[TryOnResultRepositoryAdapter] 사용자별 피팅 결과 상세 조회 - userId: {}, count: {}", userId, tryOnResults.size());
        return tryOnResults;
    }
    
    
    @Override
    public void softDelete(@NonNull TryOnResult tryOnResult) {
        tryOnResult.setIsDeleted(true);
        jpaTryOnResultRepository.save(tryOnResult);
        log.debug("[TryOnResultRepositoryAdapter] 피팅 결과 소프트 삭제 성공 - tryOnResultId: {}", tryOnResult.getId());
    }
} 