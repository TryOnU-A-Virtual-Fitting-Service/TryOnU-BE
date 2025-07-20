package tryonu.api.service.fittingmodel;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tryonu.api.domain.FittingModel;
import tryonu.api.dto.responses.AddFittingModelResponse;
import tryonu.api.repository.defaultmodel.DefaultModelRepository;
import tryonu.api.domain.DefaultModel;
import tryonu.api.repository.fittingmodel.FittingModelRepository;

@Service
@RequiredArgsConstructor
public class FittingModelServiceImpl implements FittingModelService {

    private final DefaultModelRepository defaultModelRepository;
    private final FittingModelRepository fittingModelRepository;

    @Override
    public AddFittingModelResponse addFittingModel(Long defaultModelId) {
        // 기본 모델 조회
        DefaultModel defaultModel = defaultModelRepository.findByIdAndIsDeletedFalseOrThrow(defaultModelId);

        // 피팅모델 생성
        FittingModel fittingModel = FittingModel.builder()
            .imageUrl(defaultModel.getImageUrl())
            .build();

        // 피팅모델 저장
        FittingModel savedFittingModel = fittingModelRepository.save(fittingModel);
            
        // 임시로 더미 데이터 반환
        return new AddFittingModelResponse(savedFittingModel.getId(), savedFittingModel.getImageUrl());
    }

}
