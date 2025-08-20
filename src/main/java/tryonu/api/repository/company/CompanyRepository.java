package tryonu.api.repository.company;

import org.springframework.lang.NonNull;
import tryonu.api.domain.Company;

public interface CompanyRepository {
    
    /**
     * 회사 저장 (로깅 포함)
     */
    Company save(@NonNull Company company);
    
    /**
     * 도메인으로 활성화된 회사 조회 (예외처리 포함)
     */
    Company findByDomainAndIsActiveTrueOrThrow(@NonNull String domain);
    
    /**
     * 플러그인 키로 활성화된 회사 조회 (예외처리 포함)
     */
    Company findByPluginKeyAndIsActiveTrueOrThrow(@NonNull String pluginKey);

    /**
     * 회사명 존재 여부 확인
     */
    boolean existsByCompanyName(@NonNull String companyName);
    
    /**
     * 도메인 존재 여부 확인
     */
    boolean existsByDomain(@NonNull String domain);
}