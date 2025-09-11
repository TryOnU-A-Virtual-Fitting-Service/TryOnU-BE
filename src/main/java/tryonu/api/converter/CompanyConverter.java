package tryonu.api.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import tryonu.api.domain.Company;
import tryonu.api.dto.requests.CompanyRequest;
import tryonu.api.dto.responses.AssetResponse;
import tryonu.api.dto.responses.CompanyResponse;

/**
 * Company 엔티티와 관련된 변환 로직을 처리하는 컨버터
 */
@Slf4j
@Component
public class CompanyConverter {
    
    @Value("${assets.fallback.logo.url}")
    private String fallbackLogoUrl;

    @Value("${assets.fallback.slogan.url}")
    private String fallbackSloganUrl;

    @Value("${assets.fallback.btn.url}")
    private String fallbackBtnUrl;
    
    /**
     * 회사 정보와 애셋 타입에 따라 적절한 애셋 URL을 반환
     * 
     * @param company 회사 엔티티
     * @return 해당 타입의 애셋 URL
     */
    public AssetResponse getAssetUrl(@NonNull Company company) {
        return new AssetResponse(company.getLogoUrl(), company.getSloganUrl(), company.getBtnUrl());    
    }
    
    /**
     * 회사 조회 실패 시 fallback URL을 사용하여 AssetResponse를 생성
     * 
     * @return fallback URL을 사용한 AssetResponse
     */
    public AssetResponse getFallbackAssetUrl() {
        log.debug("[CompanyConverter] Fallback URL을 사용하여 AssetResponse 생성 - logoUrl: {}, sloganUrl: {}", 
                 fallbackLogoUrl, fallbackSloganUrl, fallbackBtnUrl);
        return new AssetResponse(fallbackLogoUrl, fallbackSloganUrl, fallbackBtnUrl);
    }
    
    /**
     * CompanyRequest를 Company 엔티티로 변환
     * 
     * @param request 회사 등록 요청 DTO
     * @return Company 엔티티
     */
    public Company toEntity(@NonNull CompanyRequest request) {
        log.debug("[CompanyConverter] CompanyRequest -> Company 엔티티 변환 시작 - companyName: {}", request.companyName());
        
        Company company = Company.builder()
                .companyName(request.companyName())
                .domain(request.domain())
                .logoUrl(request.logoUrl())
                .sloganUrl(request.sloganUrl())
                .isActive(request.isActive())
                .build();
        
        log.debug("[CompanyConverter] CompanyRequest -> Company 엔티티 변환 완료 - companyName: {}", request.companyName());
        return company;
    }
    
    /**
     * Company 엔티티를 CompanyResponse로 변환
     * 
     * @param company 회사 엔티티
     * @return CompanyResponse DTO
     */
    public CompanyResponse toResponse(@NonNull Company company) {
        log.debug("[CompanyConverter] Company 엔티티 -> CompanyResponse 변환 시작 - companyName: {}", company.getCompanyName());
        
        CompanyResponse response = new CompanyResponse(
                company.getCompanyName(),
                company.getDomain(),
                company.getSloganUrl(),
                company.getPluginKey()
        );
        
        log.debug("[CompanyConverter] Company 엔티티 -> CompanyResponse 변환 완료 - companyName: {}", company.getCompanyName());
        return response;
    }
    
}