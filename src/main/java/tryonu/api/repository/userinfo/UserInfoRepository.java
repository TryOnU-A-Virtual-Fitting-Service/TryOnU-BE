package tryonu.api.repository.userinfo;

import org.springframework.lang.NonNull;
import tryonu.api.domain.UserInfo;

/**
 * 사용자 개인정보 Repository 인터페이스
 */
public interface UserInfoRepository {
    
    /**
     * 사용자 개인정보 저장
     */
    UserInfo save(@NonNull UserInfo userInfo);
    
    /**
     * 사용자 ID로 개인정보 조회 (예외처리 포함)
     */
    UserInfo findByUserIdAndIsDeletedFalseOrThrow(@NonNull Long userId);
    
    /**
     * 사용자 ID로 개인정보 존재 여부 확인
     */
    boolean existsByUserIdAndIsDeletedFalse(@NonNull Long userId);
    
    /**
     * 사용자 개인정보 소프트 삭제
     */
    void softDelete(@NonNull UserInfo userInfo);
} 