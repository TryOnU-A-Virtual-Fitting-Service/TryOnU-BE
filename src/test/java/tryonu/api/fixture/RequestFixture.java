package tryonu.api.fixture;

import tryonu.api.dto.requests.TryOnRequestDto;
import tryonu.api.dto.requests.UserInitRequest;
import tryonu.api.dto.requests.CompanyRequest;
import tryonu.api.dto.requests.VirtualFittingRequest;

/**
 * 테스트용 Request DTO Fixture
 */
public class RequestFixture {

    public static TryOnRequestDto createTryOnRequest() {
        return new TryOnRequestDto(
                "https://test-bucket.s3.amazonaws.com/models/default-female-model.jpg",
                1L,
                "https://test-fashion.com/products/test-shirt");
    }

    public static TryOnRequestDto createTryOnRequest(String modelUrl, Long defaultModelId) {
        return new TryOnRequestDto(
                modelUrl,
                defaultModelId,
                "https://test-fashion.com/products/test-shirt");
    }

    public static TryOnRequestDto createTryOnRequestWithoutProductUrl(String modelUrl, Long defaultModelId) {
        return new TryOnRequestDto(
                modelUrl,
                defaultModelId,
                null);
    }

    public static UserInitRequest createUserInitRequest() {
        return new UserInitRequest("test-uuid-12345");
    }

    public static UserInitRequest createUserInitRequest(String uuid) {
        return new UserInitRequest(uuid);
    }

    public static CompanyRequest createCompanyRequest() {
        return new CompanyRequest(
                "testcompany",
                "test-company.com",
                "https://cdn.example.com/company/test/logo.png",
                true);
    }

    public static CompanyRequest createCompanyRequest(String companyName, String domain) {
        return new CompanyRequest(
                companyName,
                domain,
                "https://cdn.example.com/company/" + companyName + "/logo.png",
                true);
    }

    public static VirtualFittingRequest createVirtualFittingRequest() {
        return new VirtualFittingRequest(
                "https://test-bucket.s3.amazonaws.com/models/default-model.jpg",
                "https://test-bucket.s3.amazonaws.com/clothes/test-shirt.jpg");
    }

    public static VirtualFittingRequest createVirtualFittingRequest(String modelUrl, String clothUrl) {
        return new VirtualFittingRequest(modelUrl, clothUrl);
    }
}
