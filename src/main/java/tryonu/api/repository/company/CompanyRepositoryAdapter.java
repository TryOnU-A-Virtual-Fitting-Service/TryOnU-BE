package tryonu.api.repository.company;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.domain.Company;
import java.util.Optional;

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
    public Optional<Company> findByCompanyNameAndIsActiveTrue(@NonNull String companyName) {
        return jpaCompanyRepository.findByCompanyNameAndIsActiveTrue(companyName);
    }
    
    @Override
    public Company findByCompanyNameAndIsActiveTrueOrThrow(@NonNull String companyName) {
        return jpaCompanyRepository.findByCompanyNameAndIsActiveTrue(companyName)
            .orElseThrow(() -> {
                log.error("[CompanyRepositoryAdapter] 활성화된 회사를 찾을 수 없음 - companyName: {}", companyName);
                return new CustomException(ErrorCode.COMPANY_NOT_FOUND, 
                    String.format("회사명 '%s'에 해당하는 활성화된 회사를 찾을 수 없습니다.", companyName));
            });
    }
    
    @Override
    public boolean existsByCompanyNameAndIsActiveTrue(@NonNull String companyName) {
        boolean exists = jpaCompanyRepository.existsByCompanyNameAndIsActiveTrue(companyName);
        log.debug("[CompanyRepositoryAdapter] 활성화된 회사 존재 여부 확인 - companyName: {}, exists: {}", companyName, exists);
        return exists;
    }
}