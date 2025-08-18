package tryonu.api.common.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.domain.User;

import java.util.Optional;

/**
 * Spring Security 관련 유틸리티 클래스
 * SecurityContext에서 현재 사용자 정보를 쉽게 가져올 수 있는 메서드들을 제공합니다.
 */
@Slf4j
@Component
public class SecurityUtils {

    /**
     * 현재 인증된 사용자를 반환합니다.
     * 인증되지 않은 경우 예외를 발생시킵니다.
     * 
     * @return 현재 사용자
     * @throws CustomException 인증되지 않은 경우
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("[SecurityUtils] 인증되지 않은 사용자 접근");
            throw new CustomException(ErrorCode.UNAUTHORIZED, "인증이 필요합니다.");
        }
        
        if (authentication instanceof UuidAuthenticationToken token) {
            User user = token.getUser();
            if (user == null) {
                log.warn("[SecurityUtils] 인증 토큰에 사용자 정보가 없음");
                throw new CustomException(ErrorCode.UNAUTHORIZED, "사용자 정보를 찾을 수 없습니다.");
            }
            return user;
        }
        
        log.warn("[SecurityUtils] 지원하지 않는 인증 타입: {}", authentication.getClass().getSimpleName());
        throw new CustomException(ErrorCode.UNAUTHORIZED, "지원하지 않는 인증 방식입니다.");
    }

    /**
     * 현재 인증된 사용자를 Optional로 반환합니다.
     * 인증되지 않은 경우 빈 Optional을 반환합니다.
     * 
     * @return 현재 사용자 (Optional)
     */
    public static Optional<User> getCurrentUserOptional() {
        try {
            return Optional.of(getCurrentUser());
        } catch (Exception e) {
            log.debug("[SecurityUtils] 현재 사용자 조회 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * 현재 사용자의 ID를 반환합니다.
     * 
     * @return 사용자 ID
     * @throws CustomException 인증되지 않은 경우
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * 현재 사용자의 deviceId를 반환합니다.
     * 
     * @return 현재 사용자의 deviceId
     * @throws CustomException 인증되지 않은 경우
     */
    public static String getCurrentUuid() {
        return getCurrentUser().getUuid();
    }

    /**
     * 현재 인증 상태를 확인합니다.
     * 
     * @return 인증된 경우 true
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }


} 