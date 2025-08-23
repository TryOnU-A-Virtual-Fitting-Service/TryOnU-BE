package tryonu.api.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tryonu.api.config.DefaultModelConfig;
import tryonu.api.domain.DefaultModel;
import tryonu.api.domain.User;
import tryonu.api.common.enums.Gender;
import tryonu.api.dto.responses.DefaultModelResponse;
import tryonu.api.dto.responses.DefaultModelDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DefaultModelConverter {
    private final DefaultModelConfig defaultModelConfig;

    public DefaultModel createDefaultModel(User user, Gender gender, Integer sortOrder) {
        String modelName = gender == Gender.MALE ? "남자 모델" : "여자 모델";
        return DefaultModel.builder()
                .user(user)
                .imageUrl(defaultModelConfig.getDefaultModelUrl(gender))
                .modelName(modelName)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .build();
    }

    public DefaultModel createDefaultModel(User user, String imageUrl, Integer sortOrder) {
        return DefaultModel.builder()
                .user(user)
                .imageUrl(imageUrl)
                .modelName("커스텀 모델")
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .build();
    }

    public DefaultModel createDefaultModel(User user, String imageUrl, String modelName, Integer sortOrder) {
        return DefaultModel.builder()
                .user(user)
                .imageUrl(imageUrl)
                .modelName(modelName)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .build();
    }

    /**
     * DefaultModelResponse 생성
     */
    public DefaultModelResponse toDefaultModelResponse(DefaultModel defaultModel) {
        return new DefaultModelResponse(defaultModel.getId(), defaultModel.getImageUrl(), defaultModel.getModelName(), defaultModel.getSortOrder());
    }

    /**
     * DefaultModelDto 생성
     */
    public DefaultModelDto toDefaultModelDto(DefaultModel defaultModel) {
        return new DefaultModelDto(defaultModel.getId(), defaultModel.getImageUrl(), defaultModel.getModelName(), defaultModel.getSortOrder());
    }

    /**
     * DefaultModelDto 리스트 생성
     */
    public List<DefaultModelDto> toDefaultModelDtoList(List<DefaultModel> defaultModels) {
        return defaultModels.stream()
                .map(this::toDefaultModelDto)
                .collect(Collectors.toList());
    }
} 