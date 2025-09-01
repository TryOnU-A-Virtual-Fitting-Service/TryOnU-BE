package tryonu.api.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.dao.DataIntegrityViolationException;

import tryonu.api.config.BaseServiceTest;
import tryonu.api.repository.user.UserRepository;
import tryonu.api.repository.defaultmodel.DefaultModelRepository;
import tryonu.api.repository.tryonresult.TryOnResultRepository;
import tryonu.api.converter.DefaultModelConverter;
import tryonu.api.converter.UserConverter;
import tryonu.api.common.auth.SecurityUtils;
import tryonu.api.domain.User;
import tryonu.api.domain.DefaultModel;
import tryonu.api.dto.requests.UserInitRequest;
import tryonu.api.dto.responses.UserInfoResponse;
import tryonu.api.dto.responses.SimpleUserResponse;
import tryonu.api.dto.responses.DefaultModelDto;
import tryonu.api.dto.responses.TryOnResultDto;
import tryonu.api.dto.responses.RecentlyUsedModel;
import tryonu.api.common.enums.Gender;

import tryonu.api.fixture.*;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * UserServiceImpl 단위 테스트
 */
class UserServiceImplTest extends BaseServiceTest {

        @InjectMocks
        private UserServiceImpl userService;

        @Mock
        private UserRepository userRepository;

        @Mock
        private DefaultModelRepository defaultModelRepository;

        @Mock
        private TryOnResultRepository tryOnResultRepository;

        @Mock
        private DefaultModelConverter defaultModelConverter;

        @Mock
        private UserConverter userConverter;

        private User testUser;
        private UserInitRequest testUserInitRequest;
        private List<DefaultModel> initialModels;
        private List<DefaultModelDto> defaultModelDtos;
        private List<TryOnResultDto> tryOnResultDtos;

        @BeforeEach
        void setUp() {
                testUser = UserFixture.createUserWithId(1L, "test-uuid-12345");
                testUserInitRequest = RequestFixture.createUserInitRequest("test-uuid-12345");

                initialModels = new ArrayList<>();
                DefaultModel femaleModel = DefaultModelFixture.createDefaultModel(testUser, Gender.FEMALE, 1);
                DefaultModel maleModel = DefaultModelFixture.createDefaultModel(testUser, Gender.MALE, 2);
                initialModels.add(femaleModel);
                initialModels.add(maleModel);

                defaultModelDtos = List.of(
                                new DefaultModelDto(1L,
                                                "https://test-bucket.s3.amazonaws.com/models/default-female-model.jpg",
                                                "기본 여성 모델", 1, false),
                                new DefaultModelDto(2L,
                                                "https://test-bucket.s3.amazonaws.com/models/default-male-model.jpg",
                                                "기본 남성 모델", 2, false));

                tryOnResultDtos = List.of();
        }

        @Nested
        @DisplayName("사용자 초기화")
        class InitializeUser {

                @Test
                @DisplayName("성공: 새로운 사용자 생성")
                void initializeUser_Success_NewUser() {
                        // Given
                        String uuid = "new-user-uuid";
                        UserInitRequest request = RequestFixture.createUserInitRequest(uuid);
                        User newUser = UserFixture.createUser(uuid);
                        UserInfoResponse expectedResponse = new UserInfoResponse(defaultModelDtos, tryOnResultDtos);

                        given(userRepository.findByUuidWithLock(uuid)).willReturn(Optional.empty());
                        given(userRepository.save(any(User.class))).willReturn(newUser);

                        given(defaultModelConverter.createDefaultModel(eq(newUser), eq(Gender.FEMALE), eq(1)))
                                        .willReturn(initialModels.get(0));
                        given(defaultModelConverter.createDefaultModel(eq(newUser), eq(Gender.MALE), eq(2)))
                                        .willReturn(initialModels.get(1));

                        given(defaultModelRepository.saveAll(anyList())).willReturn(initialModels);
                        given(defaultModelRepository.findDefaultModelsByUserIdOrderBySortOrder(newUser.getId()))
                                        .willReturn(defaultModelDtos);
                        given(tryOnResultRepository.findTryOnResultsByUserIdOrderByIdDesc(newUser.getId()))
                                        .willReturn(tryOnResultDtos);
                        given(userConverter.toUserInfoResponse(defaultModelDtos, tryOnResultDtos))
                                        .willReturn(expectedResponse);

                        // When
                        UserInfoResponse result = userService.initializeUser(request);

                        // Then
                        assertThat(result).isNotNull();
                        assertThat(result.defaultModels()).hasSize(2);
                        assertThat(result.tryOnResults()).isEmpty();

                        then(userRepository).should().findByUuidWithLock(uuid);
                        then(userRepository).should(times(2)).save(any(User.class));
                        then(defaultModelRepository).should().saveAll(anyList());
                        then(defaultModelConverter).should().createDefaultModel(eq(newUser), eq(Gender.FEMALE), eq(1));
                        then(defaultModelConverter).should().createDefaultModel(eq(newUser), eq(Gender.MALE), eq(2));
                }

                @Test
                @DisplayName("성공: 기존 사용자 반환")
                void initializeUser_Success_ExistingUser() {
                        // Given
                        String uuid = "existing-user-uuid";
                        UserInitRequest request = RequestFixture.createUserInitRequest(uuid);
                        User existingUser = UserFixture.createUserWithId(1L, uuid);
                        UserInfoResponse expectedResponse = new UserInfoResponse(defaultModelDtos, tryOnResultDtos);

                        given(userRepository.findByUuidWithLock(uuid)).willReturn(Optional.of(existingUser));
                        given(defaultModelRepository.findDefaultModelsByUserIdOrderBySortOrder(existingUser.getId()))
                                        .willReturn(defaultModelDtos);
                        given(tryOnResultRepository.findTryOnResultsByUserIdOrderByIdDesc(existingUser.getId()))
                                        .willReturn(tryOnResultDtos);
                        given(userConverter.toUserInfoResponse(defaultModelDtos, tryOnResultDtos))
                                        .willReturn(expectedResponse);

                        // When
                        UserInfoResponse result = userService.initializeUser(request);

                        // Then
                        assertThat(result).isNotNull();
                        assertThat(result.defaultModels()).hasSize(2);

                        then(userRepository).should().findByUuidWithLock(uuid);
                        then(userRepository).should(never()).save(any(User.class));
                        then(defaultModelRepository).should(never()).saveAll(anyList());
                }

                @Test
                @DisplayName("성공: 동시성 중복 생성 시 기존 사용자 사용")
                void initializeUser_Success_ConcurrencyHandling() {
                        // Given
                        String uuid = "concurrent-user-uuid";
                        UserInitRequest request = RequestFixture.createUserInitRequest(uuid);
                        User existingUser = UserFixture.createUserWithId(1L, uuid);
                        UserInfoResponse expectedResponse = new UserInfoResponse(defaultModelDtos, tryOnResultDtos);

                        given(userRepository.findByUuidWithLock(uuid)).willReturn(Optional.empty());
                        given(userRepository.save(any(User.class)))
                                        .willThrow(new DataIntegrityViolationException("Duplicate UUID"));
                        given(userRepository.findByUuid(uuid)).willReturn(Optional.of(existingUser));
                        given(defaultModelRepository.findDefaultModelsByUserIdOrderBySortOrder(existingUser.getId()))
                                        .willReturn(defaultModelDtos);
                        given(tryOnResultRepository.findTryOnResultsByUserIdOrderByIdDesc(existingUser.getId()))
                                        .willReturn(tryOnResultDtos);
                        given(userConverter.toUserInfoResponse(defaultModelDtos, tryOnResultDtos))
                                        .willReturn(expectedResponse);

                        // When
                        UserInfoResponse result = userService.initializeUser(request);

                        // Then
                        assertThat(result).isNotNull();
                        assertThat(result.defaultModels()).hasSize(2);

                        then(userRepository).should().findByUuidWithLock(uuid);
                        then(userRepository).should().save(any(User.class));
                        then(userRepository).should().findByUuid(uuid);
                }
        }

        @Nested
        @DisplayName("현재 사용자 정보 조회")
        class GetCurrentUserInfo {

                @Test
                @DisplayName("성공: 현재 사용자 정보 조회")
                void getCurrentUserInfo_Success() {
                        // Given
                        Long userId = 1L;
                        UserInfoResponse expectedResponse = new UserInfoResponse(defaultModelDtos, tryOnResultDtos);

                        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                                mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
                                given(defaultModelRepository.findDefaultModelsByUserIdOrderBySortOrder(userId))
                                                .willReturn(defaultModelDtos);
                                given(tryOnResultRepository.findTryOnResultsByUserIdOrderByIdDesc(userId))
                                                .willReturn(tryOnResultDtos);
                                given(userConverter.toUserInfoResponse(defaultModelDtos, tryOnResultDtos))
                                                .willReturn(expectedResponse);

                                // When
                                UserInfoResponse result = userService.getCurrentUserInfo();

                                // Then
                                assertThat(result).isNotNull();
                                assertThat(result.defaultModels()).hasSize(2);
                                assertThat(result.tryOnResults()).isEmpty();

                                then(defaultModelRepository).should().findDefaultModelsByUserIdOrderBySortOrder(userId);
                                then(tryOnResultRepository).should().findTryOnResultsByUserIdOrderByIdDesc(userId);
                                then(userConverter).should().toUserInfoResponse(defaultModelDtos, tryOnResultDtos);
                        }
                }
        }

        @Nested
        @DisplayName("현재 사용자 간단 정보 조회")
        class GetCurrentUserSimpleInfo {

                @Test
                @DisplayName("성공: 현재 사용자 간단 정보 조회")
                void getCurrentUserSimpleInfo_Success() {
                        // Given
                        RecentlyUsedModel recentlyUsedModel = new RecentlyUsedModel(
                                        "https://test-bucket.s3.amazonaws.com/models/default-female-model.jpg",
                                        "default-female-model.jpg",
                                        "기본 여성 모델");
                        SimpleUserResponse expectedResponse = new SimpleUserResponse(recentlyUsedModel);

                        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
                                mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
                                given(userConverter.toSimpleUserResponse(testUser)).willReturn(expectedResponse);

                                // When
                                SimpleUserResponse result = userService.getCurrentUserSimpleInfo();

                                // Then
                                assertThat(result).isNotNull();
                                assertThat(result.recentlyUsedModel().modelUrl())
                                                .isEqualTo(testUser.getRecentlyUsedModelUrl());
                                assertThat(result.recentlyUsedModel().modelName())
                                                .isEqualTo(testUser.getRecentlyUsedModelName());

                                then(userConverter).should().toSimpleUserResponse(testUser);
                        }
                }
        }
}