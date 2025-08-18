package tryonu.api.repository.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import tryonu.api.domain.Company;

import java.util.Optional;

public interface JpaCompanyRepository extends JpaRepository<Company, Long> {
    
    /**
     * 도메인으로 활성화된 회사 조회
     */
    Optional<Company> findByDomainAndIsActiveTrue(@NonNull String domain);
    
    /**
     * 플러그인 키로 활성화된 회사 조회
     */
    Optional<Company> findByPluginKeyAndIsActiveTrue(@NonNull String pluginKey);
    
    /**
     * 회사명 존재 여부 확인
     */
    boolean existsByCompanyName(@NonNull String companyName);
    
    /**
     * 도메인 존재 여부 확인
     */
    boolean existsByDomain(@NonNull String domain);
}