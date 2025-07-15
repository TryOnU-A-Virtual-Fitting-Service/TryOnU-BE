package tryonu.api.service.user;

import tryonu.api.domain.User;
import tryonu.api.dto.requests.UserInitRequest;
import tryonu.api.dto.responses.UserResponse;

import java.util.Optional;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface UserService {
    
    /**
     * deviceId로 사용자 조회
     * 
     * @param deviceId 디바이스 ID
     * @return 사용자 정보 (Optional)
     */
    Optional<User> findByDeviceId(String deviceId);
    
    /**
     * 익명 사용자 초기화
     * 
     * @param request 초기화 요청 정보
     * @return 초기화된 사용자 정보
     */
    UserResponse initializeUser(UserInitRequest request);
    

}
