package tryonu.api.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import tryonu.api.domain.User;
import jakarta.persistence.LockModeType;

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
     * 디바이스 ID로 사용자 존재 여부 확인
     */
    boolean existsByUuidAndIsDeletedFalse(@NonNull String uuid);

    /**
     * uuid로 사용자 조회 (일반 조회 - 락 없음)
     */
    Optional<User> findByUuid(@NonNull String uuid);

    /**
     * uuid로 사용자 조회 (비관적 락 적용 - 동시성 제어용)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.uuid = :uuid")
    Optional<User> findByUuidWithLock(@Param("uuid") @NonNull String uuid);
}