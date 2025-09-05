package tryonu.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tryonu.api.common.enums.Gender;

/**
 * 기본 모델 URL 설정 클래스
 * application.yml의 default-models 설정을 바인딩합니다.
 */
@Component
@Getter
public class DefaultModelConfig {
    
    @Value("${assets.default-models.male.url}")
    private String maleUrl;
    
    @Value("${assets.default-models.female.url}")
    private String femaleUrl;
    
    /**
     * 성별에 따른 기본 모델 URL을 반환합니다.
     * 
     * @param gender 성별
     * @return 기본 모델 URL
     */
    public String getDefaultModelUrl(Gender gender) {
        return switch (gender) {
            case MALE -> maleUrl;
            case FEMALE -> femaleUrl;
        };
    }
} 