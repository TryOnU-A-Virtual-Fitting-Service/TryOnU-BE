package tryonu.api.repository.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import tryonu.api.domain.Company;

import java.util.Optional;

public interface JpaCompanyRepository extends JpaRepository<Company, Long> {
    
    /**
     * 회사명으로 활성화된 회사 조회
     */
    Optional<Company> findByCompanyNameAndIsActiveTrue(@NonNull String companyName);
    
    /**
     * 회사명으로 회사 조회 (활성화 여부 무관)
     */
    Optional<Company> findByCompanyName(@NonNull String companyName);
    
    /**
     * 회사명으로 활성화된 회사 존재 여부 확인
     */
    boolean existsByCompanyNameAndIsActiveTrue(@NonNull String companyName);
}