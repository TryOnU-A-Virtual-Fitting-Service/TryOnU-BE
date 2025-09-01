package tryonu.api.fixture;

import tryonu.api.domain.Company;

/**
 * 테스트용 Company 엔티티 Fixture
 */
public class CompanyFixture {

    public static Company createCompany() {
        return Company.builder()
                .companyName("테스트 패션 컴퍼니")
                .domain("test-fashion.com")
                .pluginKey("test-plugin-key")
                .logoUrl("https://test-bucket.s3.amazonaws.com/logos/test-logo.png")
                .isActive(true)
                .build();
    }

    public static Company createCompany(String companyName, String domain) {
        return Company.builder()
                .companyName(companyName)
                .domain(domain)
                .pluginKey("plugin-" + domain.replace(".", "-"))
                .logoUrl("https://test-bucket.s3.amazonaws.com/logos/" + domain + "-logo.png")
                .isActive(true)
                .build();
    }

    public static Company createCompanyWithId(Long id, String companyName, String domain) {
        Company company = createCompany(companyName, domain);
        company.setId(id);
        return company;
    }

    public static Company createInactiveCompany() {
        return Company.builder()
                .companyName("비활성 컴퍼니")
                .domain("inactive-company.com")
                .pluginKey("inactive-plugin-key")
                .logoUrl("https://test-bucket.s3.amazonaws.com/logos/inactive-logo.png")
                .isActive(false)
                .build();
    }

    public static Company createMusinsaCompany() {
        return createCompany("무신사", "musinsa.com");
    }

    public static Company createAblyCompany() {
        return createCompany("에이블리", "a-bly.com");
    }
}
