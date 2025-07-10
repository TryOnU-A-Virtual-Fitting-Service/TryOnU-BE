package tryonu.api.repository.cloth;

import org.springframework.lang.NonNull;
import tryonu.api.domain.Cloth;

public interface ClothRepository {
    
    /**
     * 의류 저장 (비즈니스 로직 포함)
     */
    Cloth save(@NonNull Cloth cloth);
    
    /**
     * 의류 ID로 조회 (예외처리 포함)
     */
    Cloth findByIdOrThrow(@NonNull Long clothId);

    
    /**
     * 의류 소프트 삭제 (예외처리 포함)
     */
    void softDelete(@NonNull Cloth cloth);
} 