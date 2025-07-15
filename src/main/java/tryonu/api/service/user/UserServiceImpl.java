package tryonu.api.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tryonu.api.domain.User;
import tryonu.api.dto.requests.UserInitRequest;
import tryonu.api.dto.responses.UserResponse;
import tryonu.api.repository.user.UserRepository;

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

    @Override
    public Optional<User> findByDeviceId(String deviceId) {
        log.debug("[UserService] deviceId로 사용자 조회: deviceId={}", deviceId);
        
        try {
            User user = userRepository.findByDeviceIdAndIsDeletedFalseOrThrow(deviceId);
            return Optional.of(user);
        } catch (Exception e) {
            log.debug("[UserService] 사용자를 찾을 수 없음: deviceId={}", deviceId);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public UserResponse initializeUser(UserInitRequest request) {
        log.info("[UserService] 익명 사용자 초기화 시작: deviceId={}", request.deviceId());
        
        // 이미 존재하는 사용자인지 확인
        Optional<User> existingUser = findByDeviceId(request.deviceId());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            log.info("[UserService] 기존 사용자 발견: userId={}, deviceId={}", user.getId(), request.deviceId());
            return UserResponse.from(user);
        }
        
        // 새로운 사용자 생성
        User newUser = User.builder()
                .deviceId(request.deviceId())
                .name(null)
                .gender(null)
                .age(null)
                .height(null)
                .weight(null)
                .build();
        
        User savedUser = userRepository.save(newUser);
        log.info("[UserService] 새 사용자 생성 완료: userId={}, deviceId={}", savedUser.getId(), request.deviceId());
        
        return UserResponse.from(savedUser);
    }


}
