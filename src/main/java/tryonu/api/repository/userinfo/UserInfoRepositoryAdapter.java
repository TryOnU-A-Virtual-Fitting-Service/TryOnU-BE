package tryonu.api.repository.userinfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.domain.UserInfo;

/**
 * 사용자 개인정보 Repository Adapter
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserInfoRepositoryAdapter implements UserInfoRepository {
    
    private final JpaUserInfoRepository jpaUserInfoRepository;

    @Override
    public UserInfo save(@NonNull UserInfo userInfo) {
        UserInfo savedUserInfo = jpaUserInfoRepository.save(userInfo);
        log.debug("[UserInfoRepositoryAdapter] 사용자 개인정보 저장 - userId: {}", savedUserInfo.getUser().getId());
        return savedUserInfo;
    }
    
    @Override
    public UserInfo findByUserIdAndIsDeletedFalseOrThrow(@NonNull Long userId) {
        return jpaUserInfoRepository.findByUser_IdAndIsDeletedFalse(userId)
            .orElseThrow(() -> {
                log.error("[UserInfoRepositoryAdapter] 사용자 개인정보를 찾을 수 없음 - userId: {}", userId);
                return new CustomException(ErrorCode.USER_INFO_NOT_FOUND, 
                    String.format("사용자 ID '%d'에 해당하는 개인정보를 찾을 수 없습니다.", userId));
            });
    }
    
    @Override   
    public boolean existsByUserIdAndIsDeletedFalse(@NonNull Long userId) {
        boolean exists = jpaUserInfoRepository.existsByUser_IdAndIsDeletedFalse(userId);
        log.debug("[UserInfoRepositoryAdapter] 사용자 개인정보 존재 여부 확인 - userId: {}, exists: {}", userId, exists);
        return exists;
    }
    
    @Override
    public void softDelete(@NonNull UserInfo userInfo) {
        userInfo.setIsDeleted(true);
        jpaUserInfoRepository.save(userInfo);
        log.debug("[UserInfoRepositoryAdapter] 사용자 개인정보 소프트 삭제 - userId: {}", userInfo.getUser().getId());
    }
} 