package tryonu.api.service.company;

import org.springframework.lang.NonNull;

/**
 * 회사 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface CompanyService {

    /**
     * 회사명으로 로고 URL 조회
     * 
     * @param companyName 회사명 (예: musinsa, spao, zigzag, ably)
     * @return 로고 CDN URL
     */
    String getLogoUrl(@NonNull String companyName);
    
    /**
     * 회사명으로 활성화된 회사 존재 여부 확인
     * 
     * @param companyName 회사명
     * @return 존재 여부
     */
    boolean isActiveCompany(@NonNull String companyName);
}