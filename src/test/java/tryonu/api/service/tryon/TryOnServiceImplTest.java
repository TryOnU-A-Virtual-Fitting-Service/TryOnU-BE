package tryonu.api.service.tryon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.web.multipart.MultipartFile;

import tryonu.api.config.BaseServiceTest;
import tryonu.api.common.util.VirtualFittingUtil;
import tryonu.api.common.util.ImageUploadUtil;
import tryonu.api.common.util.CategoryPredictionUtil;
import tryonu.api.repository.tryonresult.TryOnResultRepository;
import tryonu.api.repository.defaultmodel.DefaultModelRepository;
import tryonu.api.converter.TryOnResultConverter;
import tryonu.api.converter.UserConverter;
import tryonu.api.common.auth.SecurityUtils;
import tryonu.api.domain.User;
import tryonu.api.domain.DefaultModel;
import tryonu.api.dto.requests.TryOnRequestDto;
import tryonu.api.dto.requests.VirtualFittingRequest;
import tryonu.api.dto.responses.TryOnResponse;
import tryonu.api.dto.responses.CategoryPredictionResponse;
import tryonu.api.dto.responses.VirtualFittingResponse;
import tryonu.api.dto.responses.VirtualFittingStatusResponse;
import tryonu.api.dto.responses.TryOnResultDto;
import tryonu.api.dto.responses.UserInfoResponse;
import tryonu.api.dto.responses.DefaultModelDto;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.common.enums.Category;

import tryonu.api.fixture.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * TryOnServiceImpl 단위 테스트
 */
class TryOnServiceImplTest extends BaseServiceTest {

    @InjectMocks
    private TryOnServiceImpl tryOnService;

    @Mock
    private VirtualFittingUtil virtualFittingUtil;

    @Mock
    private ImageUploadUtil imageUploadUtil;

    @Mock
    private CategoryPredictionUtil categoryPredictionUtil;

    @Mock
    private TryOnResultRepository tryOnResultRepository;

    @Mock
    private TryOnResultConverter tryOnResultConverter;

    @Mock
    private DefaultModelRepository defaultModelRepository;

    @Mock
    private UserConverter userConverter;

    @Mock
    private TryOnWriteService tryOnWriteService;

    private User testUser;
    private DefaultModel testDefaultModel;
    private TryOnRequestDto testRequest;
    private MultipartFile testFile;

    @BeforeEach
    void setUp() {
        testUser = UserFixture.createUserWithId(1L, "test-uuid");
        testDefaultModel = DefaultModelFixture.createDefaultModelWithId(1L, testUser);
        testRequest = RequestFixture.createTryOnRequest(testDefaultModel.getImageUrl(), testDefaultModel.getId());
        testFile = MockFileFixture.createMockClothImageFile();
    }

    @Nested
    @DisplayName("가상 피팅 실행")
    class TryOn {

        @Test
        @DisplayName("성공: 정상적인 가상 피팅 요청")
        void tryOn_Success() {
            // Given
            CategoryPredictionResponse categoryResponse = ResponseFixture.createCategoryPredictionResponse();
            String clothImageUrl = "https://test-bucket.s3.amazonaws.com/clothes/test-cloth.jpg";
            VirtualFittingRequest virtualFittingRequest = RequestFixture.createVirtualFittingRequest();
            VirtualFittingResponse virtualFittingResponse = ResponseFixture.createVirtualFittingResponse();
            VirtualFittingStatusResponse completedStatus = ResponseFixture.createCompletedStatusResponse();
            String uploadedResultImageUrl = "https://test-bucket.s3.amazonaws.com/results/test-result.jpg";
            TryOnResponse expectedResponse = ResponseFixture.createTryOnResponse(
                    1L,
                    uploadedResultImageUrl,
                    testDefaultModel.getId(),
                    testDefaultModel.getModelName());

            try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                given(defaultModelRepository.findByIdAndIsDeletedFalseOrThrow(testRequest.defaultModelId()))
                        .willReturn(testDefaultModel);
                given(categoryPredictionUtil.predictCategory(any(MultipartFile.class))).willReturn(categoryResponse);
                given(imageUploadUtil.uploadClothImage(any(MultipartFile.class))).willReturn(clothImageUrl);
                given(tryOnResultConverter.toVirtualFittingRequest(testRequest.modelUrl(), clothImageUrl))
                        .willReturn(virtualFittingRequest);
                given(virtualFittingUtil.runVirtualFitting(virtualFittingRequest)).willReturn(virtualFittingResponse);
                given(virtualFittingUtil.waitForCompletion(eq(virtualFittingResponse.id()), anyLong(), anyLong()))
                        .willReturn(completedStatus);
                given(imageUploadUtil.uploadTryOnResultImageFromUrl(completedStatus.output().get(0)))
                        .willReturn(uploadedResultImageUrl);
                given(tryOnWriteService.saveAndBuildResponse(
                        eq(Category.TOP),
                        eq(clothImageUrl),
                        eq(testRequest.productPageUrl()),
                        eq(testRequest.modelUrl()),
                        eq(uploadedResultImageUrl),
                        eq(virtualFittingResponse.id()),
                        eq(testDefaultModel),
                        eq(testUser))).willReturn(expectedResponse);

                // When
                TryOnResponse result = tryOnService.tryOn(testRequest, testFile);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.modelName()).isEqualTo(testDefaultModel.getModelName());
                assertThat(result.tryOnResultImageUrl()).isEqualTo(uploadedResultImageUrl);
                assertThat(result.defaultModelId()).isEqualTo(testDefaultModel.getId());

                // Verify interactions
                then(categoryPredictionUtil).should().predictCategory(any(MultipartFile.class));
                then(imageUploadUtil).should().uploadClothImage(any(MultipartFile.class));
                then(virtualFittingUtil).should().runVirtualFitting(virtualFittingRequest);
                then(virtualFittingUtil).should().waitForCompletion(eq(virtualFittingResponse.id()), anyLong(),
                        anyLong());
                then(imageUploadUtil).should().uploadTryOnResultImageFromUrl(completedStatus.output().get(0));
            }
        }

        @Test
        @DisplayName("실패: 지원하지 않는 카테고리 (액세서리)")
        void tryOn_Fail_UnsupportedCategory_Accessory() {
            // Given
            CategoryPredictionResponse accessoryResponse = ResponseFixture.createCategoryPredictionResponse("ACCESSORY",
                    0.98);

            try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                given(defaultModelRepository.findByIdAndIsDeletedFalseOrThrow(testRequest.defaultModelId()))
                        .willReturn(testDefaultModel);
                given(categoryPredictionUtil.predictCategory(any(MultipartFile.class))).willReturn(accessoryResponse);

                // When & Then
                assertThatThrownBy(() -> tryOnService.tryOn(testRequest, testFile))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining("액세서리와 신발은 가상피팅에서 지원하지 않습니다");

                then(imageUploadUtil).should(never()).uploadClothImage(any(MultipartFile.class));
                then(virtualFittingUtil).should(never()).runVirtualFitting(any());
            }
        }
    }

    @Nested
    @DisplayName("사용자 트라이온 결과 조회")
    class GetTryOnResults {

        @Test
        @DisplayName("성공: 현재 사용자의 트라이온 결과 조회")
        void getCurrentUserTryOnResults_Success() {
            // Given
            Long userId = 1L;
            List<TryOnResultDto> expectedResults = List.of(
                    new TryOnResultDto(
                            1L,
                            "https://test-bucket.s3.amazonaws.com/results/result1.jpg",
                            1L,
                            "기본 여성 모델"));

            try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
                given(tryOnResultRepository.findTryOnResultsByUserIdOrderByIdDesc(userId))
                        .willReturn(expectedResults);

                // When
                List<TryOnResultDto> results = tryOnService.getCurrentUserTryOnResults();

                // Then
                assertThat(results).isNotNull();
                assertThat(results).hasSize(1);
                assertThat(results.get(0).tryOnResultUrl()).isEqualTo(expectedResults.get(0).tryOnResultUrl());
                assertThat(results.get(0).modelName()).isEqualTo("기본 여성 모델");

                then(tryOnResultRepository).should().findTryOnResultsByUserIdOrderByIdDesc(userId);
            }
        }
    }

    @Nested
    @DisplayName("사용자 전체 데이터 조회")
    class GetCurrentUserAllData {

        @Test
        @DisplayName("성공: 현재 사용자의 모든 데이터 조회")
        void getCurrentUserAllData_Success() {
            // Given
            Long userId = 1L;
            List<DefaultModelDto> defaultModels = List.of(
                    new DefaultModelDto(1L, "https://test-bucket.s3.amazonaws.com/models/model1.jpg", "기본 여성 모델", 1,
                            false));
            List<TryOnResultDto> tryOnResults = List.of(
                    new TryOnResultDto(
                            1L,
                            "https://test-bucket.s3.amazonaws.com/results/result1.jpg",
                            1L,
                            "기본 여성 모델"));
            UserInfoResponse expectedResponse = new UserInfoResponse(defaultModels, tryOnResults);

            try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
                given(defaultModelRepository.findDefaultModelsByUserIdOrderBySortOrder(userId))
                        .willReturn(defaultModels);
                given(tryOnResultRepository.findTryOnResultsByUserIdOrderByIdDesc(userId))
                        .willReturn(tryOnResults);
                given(userConverter.toUserInfoResponse(defaultModels, tryOnResults))
                        .willReturn(expectedResponse);

                // When
                UserInfoResponse result = tryOnService.getCurrentUserAllData();

                // Then
                assertThat(result).isNotNull();
                assertThat(result.defaultModels()).hasSize(1);
                assertThat(result.tryOnResults()).hasSize(1);

                then(defaultModelRepository).should().findDefaultModelsByUserIdOrderBySortOrder(userId);
                then(tryOnResultRepository).should().findTryOnResultsByUserIdOrderByIdDesc(userId);
                then(userConverter).should().toUserInfoResponse(defaultModels, tryOnResults);
            }
        }
    }
}
