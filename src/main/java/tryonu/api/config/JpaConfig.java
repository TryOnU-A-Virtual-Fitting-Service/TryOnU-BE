package tryonu.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Instant;
import java.util.Optional;

/**
 * JPA Auditing 설정
 * BaseEntity의 @CreatedDate, @LastModifiedDate 어노테이션을 활성화합니다.
 * UTC 타임스탬프를 사용하여 시간을 저장합니다.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    
    /**
     * UTC 타임스탬프를 사용하는 AuditorAware 구현체
     * @CreatedDate와 @LastModifiedDate에 UTC Instant를 설정합니다.
     */
    @Bean
    public AuditorAware<Instant> auditorProvider() {
        return () -> Optional.of(Instant.now());
    }
} 