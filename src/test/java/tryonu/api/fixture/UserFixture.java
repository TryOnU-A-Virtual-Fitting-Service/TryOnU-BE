package tryonu.api.fixture;

import tryonu.api.domain.User;

/**
 * 테스트용 User 엔티티 Fixture
 */
public class UserFixture {

    public static User createUser() {
        return User.builder()
                .uuid("test-uuid-12345")
                .recentlyUsedModelUrl("https://test-bucket.s3.amazonaws.com/models/default-female-model.jpg")
                .recentlyUsedModelName("기본 여성 모델")
                .build();
    }

    public static User createUser(String uuid) {
        return User.builder()
                .uuid(uuid)
                .recentlyUsedModelUrl("https://test-bucket.s3.amazonaws.com/models/default-female-model.jpg")
                .recentlyUsedModelName("기본 여성 모델")
                .build();
    }

    public static User createUserWithId(Long id, String uuid) {
        User user = createUser(uuid);
        user.setId(id);
        return user;
    }

    public static User createUserWithModelInfo(String uuid, String modelUrl, String modelName) {
        return User.builder()
                .uuid(uuid)
                .recentlyUsedModelUrl(modelUrl)
                .recentlyUsedModelName(modelName)
                .build();
    }
}
