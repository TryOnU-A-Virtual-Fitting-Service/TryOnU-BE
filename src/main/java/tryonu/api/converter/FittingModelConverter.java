package tryonu.api.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tryonu.api.config.DefaultModelConfig;
import tryonu.api.domain.FittingModel;
import tryonu.api.domain.DefaultModel;
import tryonu.api.domain.User;
import tryonu.api.common.enums.Gender;

@Component
@RequiredArgsConstructor
public class FittingModelConverter {
    private final DefaultModelConfig defaultModelConfig;

    public FittingModel createFittingModel(User user, Gender gender) {
        return FittingModel.builder()
                .user(user)
                .imageUrl(defaultModelConfig.getDefaultModelUrl(gender))
                .build();
    }

    public FittingModel createFittingModel(DefaultModel defaultModel) {
        return FittingModel.builder()
                .user(defaultModel.getUser())
                .imageUrl(defaultModel.getImageUrl())
                .build();
    }
} 