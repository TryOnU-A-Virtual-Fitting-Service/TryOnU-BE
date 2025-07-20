package tryonu.api.service.user;

import tryonu.api.dto.requests.UserInitRequest;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface UserService {

    /**
     * 익명 사용자 초기화
     * 
     * @param request 초기화 요청 정보
     */
    void initializeUser(UserInitRequest request);
}
