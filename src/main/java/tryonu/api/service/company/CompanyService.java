package tryonu.api.service.company;

import org.springframework.lang.NonNull;
import tryonu.api.dto.responses.AssetResponse;

/**
 * 회사 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface CompanyService {

    /**
     * 전체 URL로 애셋 응답 조회
     * URL에서 도메인을 추출하여 해당 회사의 애셋 응답을 반환
     * 
     * @param url 전체 URL (예: https://www.musinsa.com/main/musinsa/recommend?gf=A)
     * @return AssetResponse
     */
    AssetResponse getAssetResponseByUrl(@NonNull String url);
    

}