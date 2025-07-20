package tryonu.api.service.fittingmodel;

import tryonu.api.dto.responses.AddFittingModelResponse;

public interface FittingModelService {

    /**
     * 기본 모델을 기반으로 새로운 피팅 모델을 추가합니다.
     * 
     * @param defaultModelId 기본 모델 ID
     * @return 추가된 피팅 모델 정보
     */
    AddFittingModelResponse addFittingModel(Long defaultModelId);
}