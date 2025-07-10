package tryonu.api.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import tryonu.api.domain.User;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    
    /**
     * 디바이스 ID로 사용자 조회
     */
    Optional<User> findByDeviceId(@NonNull String deviceId);
    
    /**
     * 디바이스 ID로 사용자 존재 여부 확인
     */
    boolean existsByDeviceId(@NonNull String deviceId);
} 