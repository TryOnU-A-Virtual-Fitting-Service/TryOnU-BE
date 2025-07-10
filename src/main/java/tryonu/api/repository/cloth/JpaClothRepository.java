package tryonu.api.repository.cloth;

import org.springframework.data.jpa.repository.JpaRepository;
import tryonu.api.domain.Cloth;


public interface JpaClothRepository extends JpaRepository<Cloth, Long> {
    

} 