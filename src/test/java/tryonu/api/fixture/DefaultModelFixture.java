package tryonu.api.fixture;

import tryonu.api.domain.DefaultModel;
import tryonu.api.domain.User;
import tryonu.api.common.enums.Gender;

/**
 * 테스트용 DefaultModel 엔티티 Fixture
 */
public class DefaultModelFixture {

    public static DefaultModel createDefaultModel() {
        return DefaultModel.builder()
                .user(UserFixture.createUser())
                .imageUrl("https://test-bucket.s3.amazonaws.com/models/default-female-model.jpg")
                .modelName("기본 여성 모델")
                .sortOrder(1)
                .isCustom(false)
                .isDeleted(false)
                .build();
    }

    public static DefaultModel createDefaultModel(User user) {
        return DefaultModel.builder()
                .user(user)
                .imageUrl("https://test-bucket.s3.amazonaws.com/models/default-female-model.jpg")
                .modelName("기본 여성 모델")
                .sortOrder(1)
                .isCustom(false)
                .isDeleted(false)
                .build();
    }

    public static DefaultModel createDefaultModel(User user, Gender gender, int sortOrder) {
        return DefaultModel.builder()
                .user(user)
                .imageUrl("https://test-bucket.s3.amazonaws.com/models/default-" + gender.name().toLowerCase()
                        + "-model.jpg")
                .modelName("기본 " + (gender == Gender.FEMALE ? "여성" : "남성") + " 모델")
                .sortOrder(sortOrder)
                .isCustom(false)
                .isDeleted(false)
                .build();
    }

    public static DefaultModel createDefaultModelWithId(Long id, User user) {
        DefaultModel model = createDefaultModel(user);
        try {
            var idField = DefaultModel.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(model, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
        return model;
    }

    public static DefaultModel createCustomModel(User user, String imageUrl, String modelName, int sortOrder) {
        return DefaultModel.builder()
                .user(user)
                .imageUrl(imageUrl)
                .modelName(modelName)
                .sortOrder(sortOrder)
                .isCustom(true)
                .isDeleted(false)
                .build();
    }
}
