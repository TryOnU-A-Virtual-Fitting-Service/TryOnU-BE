package tryonu.api.service.user;

import tryonu.api.dto.requests.UserInitRequest;
import tryonu.api.dto.responses.UserInfoResponse;
import tryonu.api.dto.responses.SimpleUserResponse;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface UserService {

    /**
     * 익명 사용자 초기화
     * 
     * @param request 초기화 요청 정보
     * @return 사용자 초기화 응답 (기본 모델, 피팅 모델 포함)
     */
    UserInfoResponse initializeUser(UserInitRequest request);
    
    /**
     * 현재 사용자 정보 조회 (X-UUID 헤더 기반)
     * 
     * @return 사용자 정보 응답 (기본 모델, 피팅 모델 포함)
     */
    UserInfoResponse getCurrentUserInfo();

    /**
     * 현재 사용자 간단 정보 조회 (X-UUID 헤더 기반)
     * 
     * @return 사용자 간단 정보 응답 (ID, UUID만 포함)
     */
    SimpleUserResponse getCurrentUserSimpleInfo();
}
