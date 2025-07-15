package tryonu.api.repository.cloth;

import org.springframework.data.jpa.repository.JpaRepository;
import tryonu.api.domain.Cloth;

import java.util.Optional;

public interface JpaClothRepository extends JpaRepository<Cloth, Long> {
    
    /**
     * id와 삭제되지 않은 의류 조회
     */
    Optional<Cloth> findByIdAndIsDeletedFalse(Long id);
} 