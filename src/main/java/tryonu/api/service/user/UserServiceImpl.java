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
import tryonu.api.dto.responses.SimpleUserResponse;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

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
        // 이미 존재하는 사용자인지 확인 (PESSIMISTIC_WRITE 락으로 동시성 제어)
        Optional<User> userOptional = userRepository.findByUuid(request.uuid());
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (user.getIsDeleted()) {
                // Soft-delete된 사용자 복구
                user.restore();
                user = userRepository.save(user);
                log.info("[UserService] 삭제된 사용자 복구: userId={}, uuid={}", user.getId(), request.uuid());
            } else {
                // 활성 사용자
                log.info("[UserService] 기존 사용자 발견: userId={}, uuid={}", user.getId(), request.uuid());
            }
        } else {
            // 존재하지 않는 경우: 새로운 사용자 생성
            user = User.builder()
                    .uuid(request.uuid())
                    .build();
            user = userRepository.save(user);

            // 기본 모델들을 배치로 생성하여 한 번에 저장
            List<DefaultModel> initialModels = new ArrayList<>();
            int initialSortOrder = 1;
            for (Gender gender : Gender.values()) {
                initialModels.add(defaultModelConverter.createDefaultModel(user, gender, initialSortOrder++));
            }
            defaultModelRepository.saveAll(initialModels);

            
            log.info("[UserService] 새 사용자 생성 완료: userId={}, uuid={}", user.getId(), request.uuid());
        }

        List<DefaultModelDto> defaultModels = defaultModelRepository.findDefaultModelsByUserIdOrderBySortOrder(user.getId());
        List<TryOnResultDto> tryOnResults = tryOnResultRepository.findTryOnResultsByUserIdOrderByIdDesc(user.getId());
        return userConverter.toUserInfoResponse(defaultModels, tryOnResults);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getCurrentUserInfo() {
        // Security Filter에서 이미 인증된 사용자만 여기까지 올 수 있음
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<DefaultModelDto> defaultModels = defaultModelRepository.findDefaultModelsByUserIdOrderBySortOrder(currentUserId);
        List<TryOnResultDto> tryOnResults = tryOnResultRepository.findTryOnResultsByUserIdOrderByIdDesc(currentUserId);
        return userConverter.toUserInfoResponse(defaultModels, tryOnResults);
    }

    @Override
    @Transactional(readOnly = true)
    public SimpleUserResponse getCurrentUserSimpleInfo() {
        // Security Filter에서 이미 인증된 사용자만 여기까지 올 수 있음
        User currentUser = SecurityUtils.getCurrentUser();
        return userConverter.toSimpleUserResponse(currentUser);
    }




}
