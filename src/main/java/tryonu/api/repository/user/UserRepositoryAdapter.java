package tryonu.api.repository.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.domain.User;


@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {
    
    private final JpaUserRepository jpaUserRepository;
    
    @Override
    public User save(@NonNull User user) {
        User savedUser = jpaUserRepository.save(user);
        log.debug("[UserRepositoryAdapter] 사용자 저장 - deviceId: {}", savedUser.getDeviceId());
        return savedUser;
    }
    
    @Override
    public User findByIdOrThrow(@NonNull Long userId) {
        return jpaUserRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("[UserRepositoryAdapter] 사용자를 찾을 수 없음 - userId: {}", userId);
                return new CustomException(ErrorCode.USER_NOT_FOUND, 
                    String.format("사용자 ID '%d'에 해당하는 사용자를 찾을 수 없습니다.", userId));
            });
    }
    
    @Override
    public User findByDeviceIdOrThrow(@NonNull String deviceId) {
        return jpaUserRepository.findByDeviceId(deviceId)
            .orElseThrow(() -> {
                log.error("[UserRepositoryAdapter] 사용자를 찾을 수 없음 - deviceId: {}", deviceId);
                return new CustomException(ErrorCode.USER_NOT_FOUND, 
                    String.format("디바이스 ID '%s'에 해당하는 사용자를 찾을 수 없습니다.", deviceId));
            });
    }
    
    
    @Override
    public boolean existsByDeviceId(@NonNull String deviceId) {
        boolean exists = jpaUserRepository.existsByDeviceId(deviceId);
        log.debug("[UserRepositoryAdapter] 사용자 존재 여부 확인 - deviceId: {}, exists: {}", deviceId, exists);
        return exists;
    }
    
    @Override
    public void softDelete(@NonNull User user) {
        user.setIsDeleted(true);
        jpaUserRepository.save(user);
        log.debug("[UserRepositoryAdapter] 사용자 소프트 삭제 - deviceId: {}", user.getDeviceId());
    }
    
} 