package tryonu.api.service.tryon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import tryonu.api.config.BaseServiceTest;
import tryonu.api.repository.cloth.ClothRepository;
import tryonu.api.repository.tryonresult.TryOnResultRepository;
import tryonu.api.repository.user.UserRepository;
import tryonu.api.converter.TryOnResultConverter;
import tryonu.api.domain.User;
import tryonu.api.domain.DefaultModel;
import tryonu.api.domain.Cloth;
import tryonu.api.domain.TryOnResult;
import tryonu.api.dto.responses.TryOnResponse;
import tryonu.api.common.enums.Category;

import tryonu.api.fixture.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * TryOnWriteServiceImpl 단위 테스트
 * 
 * 테스트 전략:
 * - Repository와 Converter는 Mock으로 처리
 * - 트랜잭션 분리된 쓰기 로직 테스트
 * - GWT(Given-When-Then) 패턴으로 테스트 작성
 */
class TryOnWriteServiceImplTest extends BaseServiceTest {

        @InjectMocks
        private TryOnWriteServiceImpl tryOnWriteService;

        @Mock
        private TryOnResultRepository tryOnResultRepository;

        @Mock
        private ClothRepository clothRepository;

        @Mock
        private TryOnResultConverter tryOnResultConverter;

        @Mock
        private UserRepository userRepository;

        // Test Fixtures
        private User testUser;
        private DefaultModel testDefaultModel;
        private Cloth testCloth;
        private TryOnResult testTryOnResult;

        @BeforeEach
        void setUp() {
                testUser = UserFixture.createUserWithId(1L, "test-uuid");
                testDefaultModel = DefaultModelFixture.createDefaultModelWithId(1L, testUser);
                testCloth = ClothFixture.createClothWithId(1L, Category.LONG_SLEEVE);
                testTryOnResult = TryOnResultFixture.createTryOnResultWithId(1L, testCloth, testUser, testDefaultModel);
        }

        @Nested
        @DisplayName("트라이온 결과 저장 및 응답 생성")
        class SaveAndBuildResponse {

                @Test
                @DisplayName("성공: 완전한 트라이온 결과 저장 및 응답 생성")
                void saveAndBuildResponse_Success() {
                        // Given
                        Category category = Category.LONG_SLEEVE;
                        String clothImageUrl = "https://test-bucket.s3.amazonaws.com/clothes/test-cloth.jpg";
                        String productPageUrl = "https://test-shop.com/product/123";
                        String modelUrl = "https://test-bucket.s3.amazonaws.com/models/test-model.jpg";
                        String uploadedResultImageUrl = "https://test-bucket.s3.amazonaws.com/results/test-result.jpg";
                        String virtualFittingId = "test-fitting-id-12345";
                        String defaultModelName = testDefaultModel.getModelName();

                        TryOnResponse expectedResponse = ResponseFixture.createTryOnResponse(
                                        1L,
                                        uploadedResultImageUrl,
                                        testDefaultModel.getId(),
                                        defaultModelName);

                        given(tryOnResultConverter.toClothEntity(clothImageUrl, productPageUrl, category))
                                        .willReturn(testCloth);
                        given(clothRepository.save(testCloth)).willReturn(testCloth);
                        given(tryOnResultConverter.toTryOnResultEntity(
                                        testCloth, testUser, modelUrl, uploadedResultImageUrl, virtualFittingId,
                                        testDefaultModel))
                                        .willReturn(testTryOnResult);
                        given(tryOnResultRepository.save(testTryOnResult)).willReturn(testTryOnResult);
                        given(userRepository.save(testUser)).willReturn(testUser);
                        given(tryOnResultConverter.toTryOnResponse(testTryOnResult, defaultModelName))
                                        .willReturn(expectedResponse);

                        // When
                        TryOnResponse result = tryOnWriteService.saveAndBuildResponse(
                                        category,
                                        clothImageUrl,
                                        productPageUrl,
                                        modelUrl,
                                        uploadedResultImageUrl,
                                        virtualFittingId,
                                        testDefaultModel,
                                        testUser);

                        // Then
                        assertThat(result).isNotNull();
                        assertThat(result.tryOnResultId()).isEqualTo(1L);
                        assertThat(result.modelName()).isEqualTo(defaultModelName);
                        assertThat(result.tryOnResultImageUrl()).isEqualTo(uploadedResultImageUrl);
                        assertThat(result.defaultModelId()).isEqualTo(testDefaultModel.getId());

                        // Verify interactions
                        then(tryOnResultConverter).should().toClothEntity(clothImageUrl, productPageUrl, category);
                        then(clothRepository).should().save(testCloth);
                        then(tryOnResultConverter).should().toTryOnResultEntity(
                                        testCloth, testUser, modelUrl, uploadedResultImageUrl, virtualFittingId,
                                        testDefaultModel);
                        then(tryOnResultRepository).should().save(testTryOnResult);
                        then(userRepository).should().save(testUser);
                        then(tryOnResultConverter).should().toTryOnResponse(testTryOnResult, defaultModelName);

                        // Verify user information update
                        assertThat(testUser.getRecentlyUsedModelUrl()).isEqualTo(uploadedResultImageUrl);
                        assertThat(testUser.getRecentlyUsedModelName()).isEqualTo(defaultModelName);
                }

                @Test
                @DisplayName("성공: productPageUrl이 null인 경우 처리")
                void saveAndBuildResponse_Success_NullProductPageUrl() {
                        // Given
                        Category category = Category.LONG_SLEEVE;
                        String clothImageUrl = "https://test-bucket.s3.amazonaws.com/clothes/test-dress.jpg";
                        String productPageUrl = null; // null 값
                        String modelUrl = "https://test-bucket.s3.amazonaws.com/models/test-model.jpg";
                        String uploadedResultImageUrl = "https://test-bucket.s3.amazonaws.com/results/test-result.jpg";
                        String virtualFittingId = "test-fitting-id-12345";
                        String defaultModelName = testDefaultModel.getModelName();

                        Cloth clothWithNullUrl = ClothFixture.createCloth(clothImageUrl, null, category);
                        TryOnResult tryOnResultWithNullUrl = TryOnResultFixture.createTryOnResult(clothWithNullUrl,
                                        testUser,
                                        testDefaultModel);

                        TryOnResponse expectedResponse = ResponseFixture.createTryOnResponse(
                                        1L,
                                        uploadedResultImageUrl,
                                        testDefaultModel.getId(),
                                        defaultModelName);

                        given(tryOnResultConverter.toClothEntity(clothImageUrl, productPageUrl, category))
                                        .willReturn(clothWithNullUrl);
                        given(clothRepository.save(clothWithNullUrl)).willReturn(clothWithNullUrl);
                        given(tryOnResultConverter.toTryOnResultEntity(
                                        clothWithNullUrl, testUser, modelUrl, uploadedResultImageUrl, virtualFittingId,
                                        testDefaultModel))
                                        .willReturn(tryOnResultWithNullUrl);
                        given(tryOnResultRepository.save(tryOnResultWithNullUrl)).willReturn(tryOnResultWithNullUrl);
                        given(userRepository.save(testUser)).willReturn(testUser);
                        given(tryOnResultConverter.toTryOnResponse(tryOnResultWithNullUrl, defaultModelName))
                                        .willReturn(expectedResponse);

                        // When
                        TryOnResponse result = tryOnWriteService.saveAndBuildResponse(
                                        category,
                                        clothImageUrl,
                                        productPageUrl, // null
                                        modelUrl,
                                        uploadedResultImageUrl,
                                        virtualFittingId,
                                        testDefaultModel,
                                        testUser);

                        // Then
                        assertThat(result).isNotNull();
                        assertThat(result.modelName()).isEqualTo(defaultModelName);
                        assertThat(result.tryOnResultImageUrl()).isEqualTo(uploadedResultImageUrl);

                        then(tryOnResultConverter).should().toClothEntity(clothImageUrl, null, category);
                }

                @Test
                @DisplayName("성공: 다양한 카테고리 처리")
                void saveAndBuildResponse_Success_VariousCategories() {
                        // Given
                        testCategoryProcessing(Category.LONG_SLEEVE);
                        testCategoryProcessing(Category.LONG_PANTS);
                        testCategoryProcessing(Category.SLEEVELESS);
                }

                private void testCategoryProcessing(Category category) {
                        // Setup common test data
                        String clothImageUrl = "https://test-bucket.s3.amazonaws.com/clothes/test-"
                                        + category.name().toLowerCase()
                                        + ".jpg";
                        String productPageUrl = "https://test-shop.com/product/" + category.name().toLowerCase();
                        String modelUrl = testDefaultModel.getImageUrl();
                        String uploadedResultImageUrl = "https://test-bucket.s3.amazonaws.com/results/test-result.jpg";
                        String virtualFittingId = "test-fitting-id-" + category.name();

                        Cloth cloth = ClothFixture.createCloth(clothImageUrl, productPageUrl, category);
                        TryOnResult tryOnResult = TryOnResultFixture.createTryOnResult(cloth, testUser,
                                        testDefaultModel);
                        TryOnResponse expectedResponse = ResponseFixture.createTryOnResponse(
                                        1L,
                                        uploadedResultImageUrl,
                                        testDefaultModel.getId(),
                                        testDefaultModel.getModelName());

                        // Reset mocks for clean test
                        reset(tryOnResultConverter, clothRepository, tryOnResultRepository, userRepository);

                        given(tryOnResultConverter.toClothEntity(clothImageUrl, productPageUrl, category))
                                        .willReturn(cloth);
                        given(clothRepository.save(cloth)).willReturn(cloth);
                        given(tryOnResultConverter.toTryOnResultEntity(
                                        cloth, testUser, modelUrl, uploadedResultImageUrl, virtualFittingId,
                                        testDefaultModel))
                                        .willReturn(tryOnResult);
                        given(tryOnResultRepository.save(tryOnResult)).willReturn(tryOnResult);
                        given(userRepository.save(testUser)).willReturn(testUser);
                        given(tryOnResultConverter.toTryOnResponse(tryOnResult, testDefaultModel.getModelName()))
                                        .willReturn(expectedResponse);

                        // When
                        TryOnResponse result = tryOnWriteService.saveAndBuildResponse(
                                        category,
                                        clothImageUrl,
                                        productPageUrl,
                                        modelUrl,
                                        uploadedResultImageUrl,
                                        virtualFittingId,
                                        testDefaultModel,
                                        testUser);

                        // Then
                        assertThat(result).isNotNull();
                        assertThat(result.modelName()).isEqualTo(testDefaultModel.getModelName());
                        assertThat(result.tryOnResultImageUrl()).isEqualTo(uploadedResultImageUrl);

                        then(tryOnResultConverter).should().toClothEntity(clothImageUrl, productPageUrl, category);
                }
        }
}
