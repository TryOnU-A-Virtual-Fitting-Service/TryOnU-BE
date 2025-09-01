package tryonu.api.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

/**
 * 서비스 레이어 단위 테스트를 위한 베이스 클래스
 * - Mockito를 활용한 Mock 기반 단위 테스트
 * - 외부 의존성은 모두 Mock으로 처리
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {

    // 공통 테스트 설정이나 유틸리티 메소드가 필요할 경우 여기에 추가
}
