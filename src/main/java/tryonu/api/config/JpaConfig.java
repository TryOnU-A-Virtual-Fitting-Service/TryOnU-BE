package tryonu.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

;
/**
 * JPA Auditing 설정
 * BaseEntity의 @CreatedDate, @LastModifiedDate 어노테이션을 활성화합니다.
 * UTC 타임스탬프를 사용하여 시간을 저장합니다.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    
} 