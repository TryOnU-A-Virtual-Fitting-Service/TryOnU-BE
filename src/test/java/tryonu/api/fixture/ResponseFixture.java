package tryonu.api.fixture;

import tryonu.api.dto.responses.*;
import java.util.List;

/**
 * 테스트용 Response DTO Fixture
 */
public class ResponseFixture {

    public static CategoryPredictionResponse createCategoryPredictionResponse() {
        return new CategoryPredictionResponse(0, "LONG_SLEEVE", 0.95);
    }

    public static CategoryPredictionResponse createCategoryPredictionResponse(String className, Double confidence) {
        return new CategoryPredictionResponse(0, className, confidence);
    }

    public static VirtualFittingResponse createVirtualFittingResponse() {
        return new VirtualFittingResponse("test-fitting-id-12345", null);
    }

    public static VirtualFittingResponse createVirtualFittingResponse(String id) {
        return new VirtualFittingResponse(id, null);
    }

    public static VirtualFittingResponse createVirtualFittingErrorResponse(String error) {
        return new VirtualFittingResponse(null, error);
    }

    public static VirtualFittingStatusResponse createCompletedStatusResponse() {
        return new VirtualFittingStatusResponse(
                "test-fitting-id-12345",
                "completed",
                List.of("https://fashn-ai.s3.amazonaws.com/results/test-result.jpg"),
                null);
    }

    public static VirtualFittingStatusResponse createProcessingStatusResponse() {
        return new VirtualFittingStatusResponse(
                "test-fitting-id-12345",
                "processing",
                null,
                null);
    }

    public static VirtualFittingStatusResponse createFailedStatusResponse() {
        return new VirtualFittingStatusResponse(
                "test-fitting-id-12345",
                "failed",
                null,
                new VirtualFittingStatusResponse.VirtualFittingError("ImageLoadError", "Error loading model image"));
    }

    public static VirtualFittingStatusResponse createFailedStatusResponse(String errorName, String errorMessage) {
        return new VirtualFittingStatusResponse(
                "test-fitting-id-12345",
                "failed",
                null,
                new VirtualFittingStatusResponse.VirtualFittingError(errorName, errorMessage));
    }

    public static HealthCheckResponse createHealthCheckResponse() {
        return new HealthCheckResponse(
                "UP",
                java.time.LocalDateTime.now(),
                60L);
    }

    public static AssetResponse createAssetResponse() {
        return new AssetResponse("test-company.com", "https://cdn.example.com/company/test/logo.png");
    }

    public static CompanyResponse createCompanyResponse() {
        return new CompanyResponse(
                "testcompany",
                "test-company.com",
                "test-plugin-key");
    }

    public static TryOnResponse createTryOnResponse() {
        return new TryOnResponse(
                1L,
                "https://test-bucket.s3.amazonaws.com/results/test-result.jpg",
                1L,
                "기본 여성 모델");
    }

    public static TryOnResponse createTryOnResponse(Long tryOnResultId, String resultImageUrl, Long defaultModelId,
            String modelName) {
        return new TryOnResponse(tryOnResultId, resultImageUrl, defaultModelId, modelName);
    }

    public static DefaultModelResponse createDefaultModelResponse() {
        return new DefaultModelResponse(
                1L,
                "https://test-bucket.s3.amazonaws.com/models/test-model.jpg",
                "커스텀 모델",
                3,
                true);
    }

    public static DefaultModelResponse createDefaultModelResponse(Long id, String imageUrl, String modelName,
            Integer sortOrder, Boolean isCustom) {
        return new DefaultModelResponse(id, imageUrl, modelName, sortOrder, isCustom);
    }
}
