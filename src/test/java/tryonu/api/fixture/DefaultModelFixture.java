package tryonu.api.fixture;

import tryonu.api.domain.DefaultModel;
import tryonu.api.domain.User;
import tryonu.api.common.enums.Gender;
import tryonu.api.common.enums.SuperType;

/**
 * 테스트용 DefaultModel 엔티티 Fixture
 */
public class DefaultModelFixture {

    public static DefaultModel createDefaultModel() {
        return DefaultModel.builder()
                .user(UserFixture.createUser())
                .gender(Gender.FEMALE)
                .superType(SuperType.REALISTIC)
                .imageUrl("https://test-bucket.s3.amazonaws.com/models/default-female-model.jpg")
                .modelName("기본 여성 모델")
                .sortOrder(1)
                .isDeleted(false)
                .build();
    }

    public static DefaultModel createDefaultModel(User user) {
        return DefaultModel.builder()
                .user(user)
                .gender(Gender.FEMALE)
                .superType(SuperType.REALISTIC)
                .imageUrl("https://test-bucket.s3.amazonaws.com/models/default-female-model.jpg")
                .modelName("기본 여성 모델")
                .sortOrder(1)
                .isDeleted(false)
                .build();
    }

    public static DefaultModel createDefaultModel(User user, Gender gender, int sortOrder) {
        return DefaultModel.builder()
                .user(user)
                .gender(gender)
                .superType(SuperType.REALISTIC)
                .imageUrl("https://test-bucket.s3.amazonaws.com/models/default-" + gender.name().toLowerCase()
                        + "-model.jpg")
                .modelName("기본 " + (gender == Gender.FEMALE ? "여성" : "남성") + " 모델")
                .sortOrder(sortOrder)
                .isDeleted(false)
                .build();
    }

    public static DefaultModel createDefaultModelWithId(Long id, User user) {
        DefaultModel model = createDefaultModel(user);
        model.setId(id);
        return model;
    }

    public static DefaultModel createCustomModel(User user, String imageUrl, String modelName, int sortOrder) {
        return DefaultModel.builder()
                .user(user)
                .gender(Gender.FEMALE)
                .superType(SuperType.CUSTOM)
                .imageUrl(imageUrl)
                .modelName(modelName)
                .sortOrder(sortOrder)
                .isDeleted(false)
                .build();
    }
}
