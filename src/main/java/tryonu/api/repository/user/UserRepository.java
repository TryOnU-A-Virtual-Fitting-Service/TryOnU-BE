package tryonu.api.repository.user;

import org.springframework.lang.NonNull;
import tryonu.api.domain.User;  
import java.util.Optional;


public interface UserRepository {
    
    /**
     * 사용자 저장 (로깅 포함)
     */
    User save(@NonNull User user);
    
    /**
     * 사용자 ID로 조회 (예외처리 포함)
     */
    User findByIdAndIsDeletedFalseOrThrow(@NonNull Long userId);

    /**
     * 디바이스 ID로 사용자 조회
     */
    Optional<User> findByDeviceId(@NonNull String deviceId);
    
    /**
     * 디바이스 ID로 사용자 조회 (예외처리 포함)
     */
    User findByDeviceIdAndIsDeletedFalseOrThrow(@NonNull String deviceId);
    
    /**
     * 디바이스 ID로 사용자 존재 여부 확인
     */
    boolean existsByDeviceIdAndIsDeletedFalse(@NonNull String deviceId);
    
    /**
     * 사용자 소프트 삭제 (예외처리 포함)
     */
    void softDelete(@NonNull User user);
    
} 