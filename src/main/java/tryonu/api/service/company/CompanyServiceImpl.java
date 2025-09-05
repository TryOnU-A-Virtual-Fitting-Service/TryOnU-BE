package tryonu.api.service.company;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.converter.CompanyConverter;
import tryonu.api.domain.Company;
import tryonu.api.dto.requests.CompanyRequest;
import tryonu.api.dto.responses.AssetResponse;
import tryonu.api.dto.responses.CompanyResponse;
import tryonu.api.repository.company.CompanyRepository;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 회사 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyConverter companyConverter;

    @Override
    public AssetResponse getAssetResponseByUrl(@NonNull String url) {
        try {
            String domain = extractDomainFromUrl(url);
            Company company = companyRepository.findByDomainAndIsActiveTrueOrThrow(domain);
            return companyConverter.getAssetUrl(company);
        } catch (CustomException e) {
            log.warn("[CompanyService] 회사 조회 실패, fallback URL 사용 - url: {}, error: {}", url, e.getMessage());
            return companyConverter.getFallbackAssetUrl();
        }
    }

    @Override
    public AssetResponse getAssetResponseByPluginKey(@NonNull String pluginKey) {
        try {
            Company company = companyRepository.findByPluginKeyAndIsActiveTrueOrThrow(pluginKey);
            return companyConverter.getAssetUrl(company);
        } catch (CustomException e) {
            log.warn("[CompanyService] 회사 조회 실패, fallback URL 사용 - pluginKey: {}, error: {}", pluginKey, e.getMessage());
            return companyConverter.getFallbackAssetUrl();
        }
    }

    @Override
    @Transactional
    public CompanyResponse registerCompany(@NonNull CompanyRequest request) {
        // 중복 검증
        validateDuplicateCompany(request);

        // 엔티티 변환 및 저장
        Company company = companyConverter.toEntity(request);
        Company savedCompany = companyRepository.save(company);

        return companyConverter.toResponse(savedCompany);
    }


    /**
     * URL에서 도메인을 추출하는 메서드
     * 
     * @param url 전체 URL
     * @return 정제된 도메인 (www. 제거)
     */
    private String extractDomainFromUrl(@NonNull String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();

            if (host == null) {
                log.error("[CompanyService] URL에서 도메인 추출 실패 - 잘못된 URL 형식: {}", url);
                throw new CustomException(ErrorCode.INVALID_REQUEST, "잘못된 URL 형식입니다.");
            }

            // www. 및 서브도메인 제거
            String cleanDomain = extractMainDomain(host);
            return cleanDomain;

        } catch (URISyntaxException e) {
            log.error("[CompanyService] URL 파싱 실패 - url: {}, error: {}", url, e.getMessage());
            throw new CustomException(ErrorCode.INVALID_REQUEST, "잘못된 URL 형식입니다.");
        }
    }

    /**
     * 호스트에서 메인 도메인을 추출하는 메서드
     * 서브도메인과 www.를 제거하여 메인 도메인만 반환
     * 
     * @param host 호스트 문자열 (예: m.a-bly.com, www.musinsa.com)
     * @return 메인 도메인 (예: a-bly.com, musinsa.com)
     */
    private String extractMainDomain(String host) {
        // www. 제거
        String domain = host.replaceFirst("^www\\.", "");

        // 서브도메인 제거 (첫 번째 점 이전 부분 제거)
        String[] parts = domain.split("\\.");
        if (parts.length > 2) {
            // 3개 이상의 부분이 있으면 서브도메인이 있는 것
            // 예: m.a-bly.com → [m, a-bly, com] → a-bly.com
            return String.join(".", parts[1], parts[2]);
        }

        return domain;
    }

    /**
     * 회사 중복 검증
     * 회사명, 도메인의 중복을 검사합니다.
     * 
     * @param request 회사 등록 요청
     */
    private void validateDuplicateCompany(@NonNull CompanyRequest request) {
        // 회사명 중복 검증
        if (companyRepository.existsByCompanyName(request.companyName())) {
            log.warn("[CompanyService] 회사명 중복 - companyName: {}", request.companyName());
            throw new CustomException(ErrorCode.COMPANY_ALREADY_EXISTS, "이미 존재하는 회사명입니다.");
        }

        // 도메인 중복 검증
        if (companyRepository.existsByDomain(request.domain())) {
            log.warn("[CompanyService] 도메인 중복 - domain: {}", request.domain());
            throw new CustomException(ErrorCode.COMPANY_ALREADY_EXISTS, "이미 존재하는 도메인입니다.");
        }
    }


}