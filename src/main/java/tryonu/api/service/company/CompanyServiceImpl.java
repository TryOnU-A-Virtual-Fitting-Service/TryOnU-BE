package tryonu.api.service.company;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tryonu.api.domain.Company;
import tryonu.api.repository.company.CompanyRepository;

/**
 * 회사 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor    
@Transactional(readOnly = true) 
public class CompanyServiceImpl implements CompanyService {
    
    private final CompanyRepository companyRepository;

    @Override
    public String getLogoUrl(@NonNull String companyName) {
        log.info("[CompanyService] 회사 로고 URL 조회 시작 - companyName: {}", companyName);
        
        Company company = companyRepository.findByCompanyNameAndIsActiveTrueOrThrow(companyName);
        String logoUrl = company.getLogoUrl();
        
        log.info("[CompanyService] 회사 로고 URL 조회 완료 - companyName: {}, logoUrl: {}", companyName, logoUrl);
        return logoUrl;
    }
    
    @Override
    public boolean isActiveCompany(@NonNull String companyName) {
        boolean isActive = companyRepository.existsByCompanyNameAndIsActiveTrue(companyName);
        log.debug("[CompanyService] 회사 활성화 상태 확인 - companyName: {}, isActive: {}", companyName, isActive);
        return isActive;
    }
}