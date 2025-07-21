package tryonu.api.repository.userinfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import tryonu.api.domain.UserInfo;

import java.util.Optional;

/**
 * JPA 기반 사용자 개인정보 Repository
 */
public interface JpaUserInfoRepository extends JpaRepository<UserInfo, Long> {
    
    /**
     * 사용자 ID로 삭제되지 않은 개인정보 조회
     */
    Optional<UserInfo> findByUser_IdAndIsDeletedFalse(@NonNull Long userId);
    
    /**
     * 사용자 ID로 삭제되지 않은 개인정보 존재 여부 확인
     */
    boolean existsByUser_IdAndIsDeletedFalse(@NonNull Long userId);
} 