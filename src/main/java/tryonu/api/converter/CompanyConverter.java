package tryonu.api.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import tryonu.api.domain.Company;
import tryonu.api.dto.responses.AssetResponse;

/**
 * Company 엔티티와 관련된 변환 로직을 처리하는 컨버터
 */
@Slf4j
@Component
public class CompanyConverter {
    
    /**
     * 회사 정보와 애셋 타입에 따라 적절한 애셋 URL을 반환
     * 
     * @param company 회사 엔티티
     * @param assetType 애셋 타입 (logo, icon, background 등)
     * @return 해당 타입의 애셋 URL
     */
    public AssetResponse getAssetUrl(@NonNull Company company) {
        String assetUrl = company.getLogoUrl();
        
        log.debug("[CompanyConverter] 애셋 URL 변환 완료 - assetUrl: {}", assetUrl);

        return new AssetResponse(company.getDomain(), assetUrl);    
    }
    
}