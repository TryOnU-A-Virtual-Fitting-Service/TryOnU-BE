package tryonu.api.common.auth;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import tryonu.api.domain.User;

import java.util.Collection;

/**
 * DeviceId 기반 인증 토큰
 * Spring Security의 Authentication 인터페이스를 구현하여 deviceId 기반 인증을 지원합니다.
 */
@Getter
public class UuidAuthenticationToken extends AbstractAuthenticationToken {

    private final User user;
    private final String uuid;

    /**
     * 인증된 토큰 생성
     * 
     * @param user 인증된 사용자
     * @param authorities 권한 목록
     */
    public UuidAuthenticationToken(User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.user = user;
        this.uuid = user.getUuid();
        setAuthenticated(true);
    }

    /**
     * 미인증 토큰 생성
     * 
     * @param deviceId 디바이스 ID
     */
    public UuidAuthenticationToken(String uuid) {
        super(null);
        this.user = null;
        this.uuid = uuid;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return uuid;
    }

    @Override
    public Object getPrincipal() {
        return uuid;
    }

    public User getUser() {
        return user;
    }
} 