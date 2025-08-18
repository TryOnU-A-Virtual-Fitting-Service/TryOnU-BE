package tryonu.api.repository.company;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.domain.Company;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CompanyRepositoryAdapter implements CompanyRepository {
    
    private final JpaCompanyRepository jpaCompanyRepository;

    @Override
    public Company save(@NonNull Company company) {
        Company savedCompany = jpaCompanyRepository.save(company);
        log.debug("[CompanyRepositoryAdapter] 회사 정보 저장 - companyName: {}", savedCompany.getCompanyName());
        return savedCompany;
    }
    
    @Override
    public Company findByDomainAndIsActiveTrueOrThrow(@NonNull String domain) {
        return jpaCompanyRepository.findByDomainAndIsActiveTrue(domain)
            .orElseThrow(() -> {
                log.error("[CompanyRepositoryAdapter] 활성화된 회사를 찾을 수 없음 - domain: {}", domain);
                return new CustomException(ErrorCode.COMPANY_NOT_FOUND, 
                    String.format("도메인 '%s'에 해당하는 활성화된 회사를 찾을 수 없습니다.", domain));
            });
    }

    @Override
    public Company findByPluginKeyAndIsActiveTrueOrThrow(@NonNull String pluginKey) {
        return jpaCompanyRepository.findByPluginKeyAndIsActiveTrue(pluginKey)
            .orElseThrow(() -> {
                log.error("[CompanyRepositoryAdapter] 활성화된 회사를 찾을 수 없음 - pluginKey: {}", pluginKey);
                return new CustomException(ErrorCode.COMPANY_NOT_FOUND, 
                    String.format("플러그인 키 '%s'에 해당하는 활성화된 회사를 찾을 수 없습니다.", pluginKey));
            });
    }
    
    @Override
    public boolean existsByCompanyName(@NonNull String companyName) {
        boolean exists = jpaCompanyRepository.existsByCompanyName(companyName);
        log.debug("[CompanyRepositoryAdapter] 회사명 존재 여부 확인 - companyName: {}, exists: {}", companyName, exists);
        return exists;
    }
    
    @Override
    public boolean existsByDomain(@NonNull String domain) {
        boolean exists = jpaCompanyRepository.existsByDomain(domain);
        log.debug("[CompanyRepositoryAdapter] 도메인 존재 여부 확인 - domain: {}, exists: {}", domain, exists);
        return exists;
    }
}