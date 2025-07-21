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
public class DeviceIdAuthenticationToken extends AbstractAuthenticationToken {

    private final User user;
    private final String deviceId;

    /**
     * 인증된 토큰 생성
     * 
     * @param user 인증된 사용자
     * @param authorities 권한 목록
     */
    public DeviceIdAuthenticationToken(User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.user = user;
        this.deviceId = user.getDeviceId();
        setAuthenticated(true);
    }

    /**
     * 미인증 토큰 생성
     * 
     * @param deviceId 디바이스 ID
     */
    public DeviceIdAuthenticationToken(String deviceId) {
        super(null);
        this.user = null;
        this.deviceId = deviceId;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return deviceId;
    }

    @Override
    public Object getPrincipal() {
        return deviceId;
    }

    public User getUser() {
        return user;
    }
} 