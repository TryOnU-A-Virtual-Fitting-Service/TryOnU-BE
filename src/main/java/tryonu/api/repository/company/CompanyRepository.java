package tryonu.api.repository.company;

import org.springframework.lang.NonNull;
import tryonu.api.domain.Company;
import java.util.Optional;

public interface CompanyRepository {
    
    /**
     * 회사 저장 (로깅 포함)
     */
    Company save(@NonNull Company company);
    
    /**
     * 회사명으로 활성화된 회사 조회
     */
    Optional<Company> findByCompanyNameAndIsActiveTrue(@NonNull String companyName);
    
    /**
     * 회사명으로 활성화된 회사 조회 (예외처리 포함)
     */
    Company findByCompanyNameAndIsActiveTrueOrThrow(@NonNull String companyName);
    
    /**
     * 회사명으로 활성화된 회사 존재 여부 확인
     */
    boolean existsByCompanyNameAndIsActiveTrue(@NonNull String companyName);
}