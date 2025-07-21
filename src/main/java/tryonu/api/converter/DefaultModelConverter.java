package tryonu.api.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tryonu.api.config.DefaultModelConfig;
import tryonu.api.domain.DefaultModel;
import tryonu.api.domain.User;
import tryonu.api.common.enums.Gender;

@Component
@RequiredArgsConstructor
public class DefaultModelConverter {
    private final DefaultModelConfig defaultModelConfig;

    public DefaultModel createDefaultModel(User user, Gender gender) {
        return DefaultModel.builder()
                .user(user)
                .imageUrl(defaultModelConfig.getDefaultModelUrl(gender))
                .build();
    }

    public DefaultModel createDefaultModel(User user, String imageUrl) {
        return DefaultModel.builder()
                .user(user)
                .imageUrl(imageUrl)
                .build();
    }
} 