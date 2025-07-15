package tryonu.api.repository.cloth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.domain.Cloth;



@Slf4j
@Repository
@RequiredArgsConstructor
public class ClothRepositoryAdapter implements ClothRepository {
    
    private final JpaClothRepository jpaClothRepository;
    
    @Override
    public Cloth save(@NonNull Cloth cloth) {
        Cloth savedCloth = jpaClothRepository.save(cloth);
        log.debug("[ClothRepositoryAdapter] 의류 저장 - category: {}, imageUrl: {}", savedCloth.getCategory(), savedCloth.getImageUrl());
        return savedCloth;
    }
    
    @Override
    public Cloth findByIdAndIsDeletedFalseOrThrow(@NonNull Long clothId) {
        return jpaClothRepository.findByIdAndIsDeletedFalse(clothId)
            .orElseThrow(() -> {
                log.error("[ClothRepositoryAdapter] 의류를 찾을 수 없음 - clothId: {}", clothId);
                return new CustomException(ErrorCode.CLOTH_NOT_FOUND, 
                    String.format("의류 ID '%d'에 해당하는 의류를 찾을 수 없습니다.", clothId));
            });
    }
    
    
    @Override
    public void softDelete(@NonNull Cloth cloth) {
        cloth.setIsDeleted(true);
        jpaClothRepository.save(cloth);
        log.debug("[ClothRepositoryAdapter] 의류 소프트 삭제 성공 - clothId: {}", cloth.getId());
    }
} 