package tryonu.api.service.company;

import org.springframework.lang.NonNull;
import tryonu.api.dto.requests.CompanyRequest;
import tryonu.api.dto.responses.AssetResponse;
import tryonu.api.dto.responses.CompanyResponse;

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

    /**
     * 회사 등록
     * 새로운 회사 정보를 등록하고 생성된 회사 정보를 반환
     * 
     * @param request 회사 등록 요청 DTO
     * @return CompanyResponse 등록된 회사 정보
     */
    CompanyResponse registerCompany(@NonNull CompanyRequest request);

    /**
     * 플러그인 키로 애셋 응답 조회
     * 
     * @param pluginKey 플러그인 키
     * @return AssetResponse
     */
    AssetResponse getAssetResponseByPluginKey(@NonNull String pluginKey);

}