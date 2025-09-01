package tryonu.api.service.company;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import tryonu.api.config.BaseServiceTest;
import tryonu.api.repository.company.CompanyRepository;
import tryonu.api.converter.CompanyConverter;
import tryonu.api.domain.Company;
import tryonu.api.dto.requests.CompanyRequest;
import tryonu.api.dto.responses.CompanyResponse;
import tryonu.api.dto.responses.AssetResponse;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;

import tryonu.api.fixture.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * CompanyServiceImpl 단위 테스트
 * 
 * 테스트 전략:
 * - Repository와 Converter는 Mock으로 처리
 * - URL 파싱 및 도메인 추출 로직 테스트
 * - 회사 등록 시 중복 검증 로직 테스트
 * - GWT(Given-When-Then) 패턴으로 테스트 작성
 */
class CompanyServiceImplTest extends BaseServiceTest {

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyConverter companyConverter;

    // Test Fixtures
    private Company testCompany;
    private CompanyRequest testCompanyRequest;

    @BeforeEach
    void setUp() {
        testCompany = CompanyFixture.createCompanyWithId(1L, "testcompany", "test-company.com");
        testCompanyRequest = RequestFixture.createCompanyRequest("testcompany", "test-company.com");
    }

    @Nested
    @DisplayName("URL로 Asset 응답 조회")
    class GetAssetResponseByUrl {

        @Test
        @DisplayName("성공: 일반적인 URL에서 Asset 조회")
        void getAssetResponseByUrl_Success_NormalUrl() {
            // Given
            String url = "https://test-company.com/products/123";
            String expectedDomain = "test-company.com";
            AssetResponse expectedResponse = ResponseFixture.createAssetResponse();

            given(companyRepository.findByDomainAndIsActiveTrueOrThrow(expectedDomain)).willReturn(testCompany);
            given(companyConverter.getAssetUrl(testCompany)).willReturn(expectedResponse);

            // When
            AssetResponse result = companyService.getAssetResponseByUrl(url);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.logoUrl()).isEqualTo(expectedResponse.logoUrl());

            then(companyRepository).should().findByDomainAndIsActiveTrueOrThrow(expectedDomain);
            then(companyConverter).should().getAssetUrl(testCompany);
        }

        @Test
        @DisplayName("성공: www 서브도메인이 있는 URL에서 Asset 조회")
        void getAssetResponseByUrl_Success_WwwSubdomain() {
            // Given
            String url = "https://www.test-company.com/products/123";
            String expectedDomain = "test-company.com"; // www 제거됨
            AssetResponse expectedResponse = ResponseFixture.createAssetResponse();

            given(companyRepository.findByDomainAndIsActiveTrueOrThrow(expectedDomain)).willReturn(testCompany);
            given(companyConverter.getAssetUrl(testCompany)).willReturn(expectedResponse);

            // When
            AssetResponse result = companyService.getAssetResponseByUrl(url);

            // Then
            assertThat(result).isNotNull();

            then(companyRepository).should().findByDomainAndIsActiveTrueOrThrow(expectedDomain);
        }

        @Test
        @DisplayName("성공: 모바일 서브도메인이 있는 URL에서 Asset 조회")
        void getAssetResponseByUrl_Success_MobileSubdomain() {
            // Given
            String url = "https://m.a-bly.com/products/123";
            String expectedDomain = "a-bly.com"; // 서브도메인 제거됨
            Company ablyCompany = CompanyFixture.createAblyCompany();
            AssetResponse expectedResponse = ResponseFixture.createAssetResponse();

            given(companyRepository.findByDomainAndIsActiveTrueOrThrow(expectedDomain)).willReturn(ablyCompany);
            given(companyConverter.getAssetUrl(ablyCompany)).willReturn(expectedResponse);

            // When
            AssetResponse result = companyService.getAssetResponseByUrl(url);

            // Then
            assertThat(result).isNotNull();

            then(companyRepository).should().findByDomainAndIsActiveTrueOrThrow(expectedDomain);
        }

        @Test
        @DisplayName("실패: 잘못된 URL 형식")
        void getAssetResponseByUrl_Fail_InvalidUrl() {
            // Given
            String invalidUrl = "invalid-url-format";

            // When & Then
            assertThatThrownBy(() -> companyService.getAssetResponseByUrl(invalidUrl))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INVALID_REQUEST);

            then(companyRepository).should(never()).findByDomainAndIsActiveTrueOrThrow(any());
        }

        @Test
        @DisplayName("실패: 호스트가 없는 URL")
        void getAssetResponseByUrl_Fail_NoHost() {
            // Given
            String urlWithoutHost = "file:///local/file.html";

            // When & Then
            assertThatThrownBy(() -> companyService.getAssetResponseByUrl(urlWithoutHost))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INVALID_REQUEST);
        }
    }

    @Nested
    @DisplayName("Plugin Key로 Asset 응답 조회")
    class GetAssetResponseByPluginKey {

        @Test
        @DisplayName("성공: Plugin Key로 Asset 조회")
        void getAssetResponseByPluginKey_Success() {
            // Given
            String pluginKey = "test-plugin-key";
            AssetResponse expectedResponse = ResponseFixture.createAssetResponse();

            given(companyRepository.findByPluginKeyAndIsActiveTrueOrThrow(pluginKey)).willReturn(testCompany);
            given(companyConverter.getAssetUrl(testCompany)).willReturn(expectedResponse);

            // When
            AssetResponse result = companyService.getAssetResponseByPluginKey(pluginKey);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.logoUrl()).isEqualTo(expectedResponse.logoUrl());

            then(companyRepository).should().findByPluginKeyAndIsActiveTrueOrThrow(pluginKey);
            then(companyConverter).should().getAssetUrl(testCompany);
        }
    }

    @Nested
    @DisplayName("회사 등록")
    class RegisterCompany {

        @Test
        @DisplayName("성공: 새로운 회사 등록")
        void registerCompany_Success() {
            // Given
            CompanyResponse expectedResponse = ResponseFixture.createCompanyResponse();

            given(companyRepository.existsByCompanyName(testCompanyRequest.companyName())).willReturn(false);
            given(companyRepository.existsByDomain(testCompanyRequest.domain())).willReturn(false);
            given(companyConverter.toEntity(testCompanyRequest)).willReturn(testCompany);
            given(companyRepository.save(testCompany)).willReturn(testCompany);
            given(companyConverter.toResponse(testCompany)).willReturn(expectedResponse);

            // When
            CompanyResponse result = companyService.registerCompany(testCompanyRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.companyName()).isEqualTo(testCompanyRequest.companyName());
            assertThat(result.domain()).isEqualTo(testCompanyRequest.domain());

            // Verify interactions
            then(companyRepository).should().existsByCompanyName(testCompanyRequest.companyName());
            then(companyRepository).should().existsByDomain(testCompanyRequest.domain());
            then(companyRepository).should().save(testCompany);
            then(companyConverter).should().toEntity(testCompanyRequest);
            then(companyConverter).should().toResponse(testCompany);
        }

        @Test
        @DisplayName("실패: 중복된 회사명")
        void registerCompany_Fail_DuplicateCompanyName() {
            // Given
            given(companyRepository.existsByCompanyName(testCompanyRequest.companyName())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> companyService.registerCompany(testCompanyRequest))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.COMPANY_ALREADY_EXISTS);

            // Verify that save was not called
            then(companyRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("실패: 중복된 도메인")
        void registerCompany_Fail_DuplicateDomain() {
            // Given
            given(companyRepository.existsByCompanyName(testCompanyRequest.companyName())).willReturn(false);
            given(companyRepository.existsByDomain(testCompanyRequest.domain())).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> companyService.registerCompany(testCompanyRequest))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.COMPANY_ALREADY_EXISTS);

            // Verify that save was not called
            then(companyRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("도메인 추출 테스트")
    class DomainExtraction {

        @Test
        @DisplayName("성공: 다양한 URL 패턴에서 도메인 추출")
        void domainExtraction_VariousPatterns() {
            // Given & When & Then
            testDomainExtraction("https://musinsa.com/product/123", "musinsa.com");
            testDomainExtraction("https://www.musinsa.com/product/123", "musinsa.com");
            testDomainExtraction("https://m.a-bly.com/product/123", "a-bly.com");
            testDomainExtraction("https://shop.example.com/product/123", "example.com");
            testDomainExtraction("http://subdomain.test-site.co.kr/path", "test-site.co.kr");
        }

        private void testDomainExtraction(String url, String expectedDomain) {
            Company mockCompany = CompanyFixture.createCompany("test", expectedDomain);
            AssetResponse mockResponse = ResponseFixture.createAssetResponse();

            given(companyRepository.findByDomainAndIsActiveTrueOrThrow(expectedDomain)).willReturn(mockCompany);
            given(companyConverter.getAssetUrl(mockCompany)).willReturn(mockResponse);

            AssetResponse result = companyService.getAssetResponseByUrl(url);

            assertThat(result).isNotNull();
            then(companyRepository).should().findByDomainAndIsActiveTrueOrThrow(expectedDomain);

            // Reset mocks for next test
            reset(companyRepository, companyConverter);
        }
    }
}
