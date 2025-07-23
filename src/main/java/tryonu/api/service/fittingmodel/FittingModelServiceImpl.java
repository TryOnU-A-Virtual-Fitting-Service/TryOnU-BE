package tryonu.api.service.fittingmodel;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tryonu.api.domain.FittingModel;
import tryonu.api.dto.responses.AddFittingModelResponse;
import tryonu.api.repository.defaultmodel.DefaultModelRepository;
import tryonu.api.domain.DefaultModel;
import tryonu.api.repository.fittingmodel.FittingModelRepository;
import tryonu.api.converter.FittingModelConverter;

@Service
@RequiredArgsConstructor
public class FittingModelServiceImpl implements FittingModelService {

    private final DefaultModelRepository defaultModelRepository;
    private final FittingModelRepository fittingModelRepository;
    private final FittingModelConverter fittingModelConverter;

    @Override
    public AddFittingModelResponse addFittingModel(Long defaultModelId) {
        // 기본 모델 조회
        DefaultModel defaultModel = defaultModelRepository.findByIdAndIsDeletedFalseOrThrow(defaultModelId);

        // 피팅모델 생성 (Converter 사용)
        FittingModel fittingModel = fittingModelConverter.createFittingModel(defaultModel);

        // 피팅모델 저장
        FittingModel savedFittingModel = fittingModelRepository.save(fittingModel);
        
        return fittingModelConverter.toAddFittingModelResponse(savedFittingModel);
    }

}
