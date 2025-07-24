package tryonu.api.repository.tryonresult;

import org.springframework.lang.NonNull;
import tryonu.api.domain.TryOnResult;
import tryonu.api.dto.responses.TryOnResultDto;

import java.util.List;

public interface TryOnResultRepository {
    
    /**
     * 피팅 결과 저장 (비즈니스 로직 포함)
     */
    TryOnResult save(@NonNull TryOnResult tryOnResult);
    
    /**
     * 피팅 결과 ID로 조회 (예외처리 포함)
     */
    TryOnResult findByIdAndIsDeletedFalseOrThrow(@NonNull Long id);
    
    /**
     * 사용자 ID로 전체 피팅 결과 조회 (예외처리 포함)
     */
    List<TryOnResult> findAllByUserIdAndIsDeletedFalseOrThrow(@NonNull Long userId);
    
    /**
     * 사용자 ID로 전체 피팅 결과와 연결된 상세 정보 조회 (예외처리 포함)
     */
    List<TryOnResult> findAllByUserIdWithDetailsAndIsDeletedFalseOrThrow(@NonNull Long userId);
    
    
    /**
     * 피팅 결과 소프트 삭제 (예외처리 포함)
     */
    void softDelete(@NonNull TryOnResult tryOnResult);
    
    /**
     * 사용자별 피팅 결과 목록 조회 (id 내림차순 정렬)
     */
    List<TryOnResultDto> findTryOnResultsByUserIdOrderByIdDesc(@NonNull Long userId);
} 