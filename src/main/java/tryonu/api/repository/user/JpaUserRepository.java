package tryonu.api.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import tryonu.api.domain.User;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    
    /**
     * id와 삭제되지 않은 사용자 조회
     */
    Optional<User> findByIdAndIsDeletedFalse(Long id);
    
    /**
     * 디바이스 ID로 삭제되지 않은 사용자 조회
     */
    Optional<User> findByUuidAndIsDeletedFalse(@NonNull String uuid);
    
    /**
     * 디바이스 ID로 삭제되지 않은 사용자 존재 여부 확인
     */
    boolean existsByUuidAndIsDeletedFalse(@NonNull String uuid);

    /**
     * uuid로 사용자 조회
     */
    Optional<User> findByUuid(@NonNull String uuid);
} 