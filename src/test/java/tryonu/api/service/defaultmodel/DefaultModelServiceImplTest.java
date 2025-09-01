package tryonu.api.service.defaultmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.web.multipart.MultipartFile;

import tryonu.api.config.BaseServiceTest;
import tryonu.api.repository.defaultmodel.DefaultModelRepository;
import tryonu.api.converter.DefaultModelConverter;
import tryonu.api.common.util.ImageUploadUtil;
import tryonu.api.common.util.BackgroundRemovalUtil;
import tryonu.api.common.auth.SecurityUtils;
import tryonu.api.domain.User;
import tryonu.api.domain.DefaultModel;
import tryonu.api.dto.requests.DefaultModelBatchUpdateRequest;
import tryonu.api.dto.requests.DefaultModelUpdateItemRequest;
import tryonu.api.dto.responses.DefaultModelResponse;
import tryonu.api.dto.responses.DefaultModelDto;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.common.enums.BatchUpdateStatus;

import tryonu.api.fixture.*;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * DefaultModelServiceImpl 단위 테스트
 */
class DefaultModelServiceImplTest extends BaseServiceTest {

    @InjectMocks
    private DefaultModelServiceImpl defaultModelService;

    @Mock
    private DefaultModelRepository defaultModelRepository;

    @Mock
    private DefaultModelConverter defaultModelConverter;

    @Mock
    private ImageUploadUtil imageUploadUtil;

    @Mock
    private BackgroundRemovalUtil backgroundRemovalUtil;

    private User testUser;
    private MultipartFile testFile;
    private DefaultModel testDefaultModel;

    @BeforeEach
    void setUp() {
        testUser = UserFixture.createUserWithId(1L, "test-uuid");
        testFile = MockFileFixture.createMockModelImageFile();
        testDefaultModel = DefaultModelFixture.createDefaultModelWithId(1L, testUser);
    }

    @Nested
    @DisplayName("기본 모델 업로드")
    class UploadDefaultModel {

        @Test
        @DisplayName("성공: 정상적인 모델 이미지 업로드")
        void uploadDefaultModel_Success() {
            // Given
            byte[] backgroundRemovedImage = MockFileFixture.createMockImageBytes();
            String uploadedImageUrl = "https://test-bucket.s3.amazonaws.com/models/uploaded-model.jpg";
            Integer nextSortOrder = 3;
            DefaultModelResponse expectedResponse = ResponseFixture.createDefaultModelResponse(
                    1L, uploadedImageUrl, "커스텀 모델", nextSortOrder, true);

            try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                given(backgroundRemovalUtil.removeBackground(any(MultipartFile.class)))
                        .willReturn(backgroundRemovedImage);
                given(imageUploadUtil.uploadModelImage(any(byte[].class))).willReturn(uploadedImageUrl);
                given(defaultModelRepository.findMaxSortOrderByUserId(testUser.getId())).willReturn(2);
                given(defaultModelConverter.createDefaultModel(testUser, uploadedImageUrl, "커스텀 모델", nextSortOrder))
                        .willReturn(testDefaultModel);
                given(defaultModelRepository.save(testDefaultModel)).willReturn(testDefaultModel);
                given(defaultModelConverter.toDefaultModelResponse(testDefaultModel)).willReturn(expectedResponse);

                // When
                DefaultModelResponse result = defaultModelService.uploadDefaultModel(testFile);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.modelName()).isEqualTo("커스텀 모델");
                assertThat(result.imageUrl()).isEqualTo(uploadedImageUrl);
                assertThat(result.sortOrder()).isEqualTo(nextSortOrder);
                assertThat(result.isCustom()).isTrue();
            }
        }

        @Test
        @DisplayName("실패: 빈 파일 업로드")
        void uploadDefaultModel_Fail_EmptyFile() {
            // Given
            MultipartFile emptyFile = MockFileFixture.createEmptyMockFile();

            try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);

                // When & Then
                assertThatThrownBy(() -> defaultModelService.uploadDefaultModel(emptyFile))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining("업로드할 파일이 비어있습니다");
            }
        }
    }

    @Nested
    @DisplayName("현재 사용자 기본 모델 조회")
    class GetCurrentUserDefaultModels {

        @Test
        @DisplayName("성공: 현재 사용자의 기본 모델 목록 조회")
        void getCurrentUserDefaultModels_Success() {
            // Given
            Long userId = 1L;
            List<DefaultModelDto> expectedModels = List.of(
                    new DefaultModelDto(1L, "https://test-bucket.s3.amazonaws.com/models/model1.jpg", "기본 여성 모델", 1,
                            false),
                    new DefaultModelDto(2L, "https://test-bucket.s3.amazonaws.com/models/model2.jpg", "기본 남성 모델", 2,
                            false));

            try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
                given(defaultModelRepository.findDefaultModelsByUserIdOrderBySortOrder(userId))
                        .willReturn(expectedModels);

                // When
                List<DefaultModelDto> result = defaultModelService.getCurrentUserDefaultModels();

                // Then
                assertThat(result).isNotNull();
                assertThat(result).hasSize(2);
                assertThat(result.get(0).modelName()).isEqualTo("기본 여성 모델");
                assertThat(result.get(1).modelName()).isEqualTo("기본 남성 모델");

                then(defaultModelRepository).should().findDefaultModelsByUserIdOrderBySortOrder(userId);
            }
        }
    }
}