package tryonu.api.fixture;

import tryonu.api.domain.TryOnResult;
import tryonu.api.domain.Cloth;
import tryonu.api.domain.User;
import tryonu.api.domain.DefaultModel;

/**
 * 테스트용 TryOnResult 엔티티 Fixture
 */
public class TryOnResultFixture {

    public static TryOnResult createTryOnResult() {
        return TryOnResult.builder()
                .cloth(ClothFixture.createCloth())
                .user(UserFixture.createUser())
                .modelUrl("https://test-bucket.s3.amazonaws.com/models/default-model.jpg")
                .imageUrl("https://test-bucket.s3.amazonaws.com/results/test-result.jpg")
                .virtualFittingId("test-fitting-id-12345")
                .defaultModelId(1L)
                .build();
    }

    public static TryOnResult createTryOnResult(Cloth cloth, User user, DefaultModel defaultModel) {
        return TryOnResult.builder()
                .cloth(cloth)
                .user(user)
                .modelUrl(defaultModel.getImageUrl())
                .imageUrl("https://test-bucket.s3.amazonaws.com/results/test-result.jpg")
                .virtualFittingId("test-fitting-id-12345")
                .defaultModelId(defaultModel.getId())
                .build();
    }

    public static TryOnResult createTryOnResultWithId(Long id, Cloth cloth, User user, DefaultModel defaultModel) {
        TryOnResult result = createTryOnResult(cloth, user, defaultModel);
        try {
            var idField = TryOnResult.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(result, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
        return result;
    }

    public static TryOnResult createTryOnResultWithUrls(String modelUrl, String resultImageUrl,
            String virtualFittingId) {
        return TryOnResult.builder()
                .cloth(ClothFixture.createCloth())
                .user(UserFixture.createUser())
                .modelUrl(modelUrl)
                .imageUrl(resultImageUrl)
                .virtualFittingId(virtualFittingId)
                .defaultModelId(1L)
                .build();
    }
}
