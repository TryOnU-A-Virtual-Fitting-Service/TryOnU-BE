package tryonu.api.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tryonu.api.domain.User;
import tryonu.api.dto.requests.UserInitRequest;
import tryonu.api.dto.responses.UserInfoResponse;
import tryonu.api.dto.responses.DefaultModelDto;
import tryonu.api.dto.responses.TryOnResultDto;
import tryonu.api.repository.defaultmodel.DefaultModelRepository;
import tryonu.api.repository.tryonresult.TryOnResultRepository;
import tryonu.api.repository.user.UserRepository;
import tryonu.api.domain.DefaultModel;
import tryonu.api.common.enums.Gender;
import tryonu.api.converter.DefaultModelConverter;
import tryonu.api.common.auth.SecurityUtils;
import tryonu.api.converter.UserConverter;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor    
@Transactional(readOnly = true) 
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final DefaultModelRepository defaultModelRepository;
    private final TryOnResultRepository tryOnResultRepository;
    private final DefaultModelConverter defaultModelConverter;  
    private final UserConverter userConverter;

    @Override
    @Transactional
    public UserInfoResponse initializeUser(UserInitRequest request) {
        log.info("[UserService] 익명 사용자 초기화 시작: uuid={}", request.uuid());
        
        User user; 
        // 이미 존재하는 사용자인지 확인
        Optional<User> userOptional = userRepository.findByUuid(request.uuid());
        if (userOptional.isPresent() && !userOptional.get().getIsDeleted()) { 
            // 이미 존재하는 사용자인 경우: 기존 사용자 정보 사용
            user = userOptional.get();
            log.info("[UserService] 기존 사용자 발견: userId={}, uuid={}", user.getId(), request.uuid());
        } else {
            // 존재하지 않는 경우: 새로운 사용자 생성
            user = User.builder()
                    .uuid(request.uuid())
                    .build();
            user = userRepository.save(user);

            for (Gender gender : Gender.values()) {
                DefaultModel defaultModel = defaultModelConverter.createDefaultModel(user, gender);
                defaultModelRepository.save(defaultModel);
            }

            
            log.info("[UserService] 새 사용자 생성 완료: userId={}, uuid={}", user.getId(), request.uuid());
        }
        
        return buildUserInfoResponse(user.getId(), "사용자 초기화 응답 생성 완료");
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getCurrentUserInfo() {
        // Security Filter에서 이미 인증된 사용자만 여기까지 올 수 있음
        User currentUser = SecurityUtils.getCurrentUser();
        log.info("[UserService] 현재 사용자 정보 조회 시작 - userId: {}, uuid: {}", currentUser.getId(), currentUser.getUuid());
        return buildUserInfoResponse(currentUser.getId(), "사용자 정보 조회 완료");
    }

    /**
     * userId로 모델 리스트를 조회하여 UserInfoResponse를 생성한다.
     * @param userId 사용자 ID
     * @param logContext 로그 메시지에 들어갈 맥락
     * @return UserInfoResponse
     */
    private UserInfoResponse buildUserInfoResponse(Long userId, String logContext) {
        List<DefaultModelDto> defaultModels = defaultModelRepository.findDefaultModelsByUserIdOrderByIdDesc(userId);
        List<TryOnResultDto> tryOnResults = tryOnResultRepository.findTryOnResultsByUserIdOrderByIdDesc(userId);
        return userConverter.toUserInfoResponse(defaultModels, tryOnResults);
    }


}
