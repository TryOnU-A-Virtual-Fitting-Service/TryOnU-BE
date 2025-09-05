package tryonu.api.service.tryon;

import tryonu.api.common.enums.Category;
import tryonu.api.domain.TryOnResult;
import tryonu.api.domain.User;
import tryonu.api.domain.DefaultModel;
import tryonu.api.dto.responses.TryOnResponse;

/**
 * 가상 피팅 결과를 영속화하는 짧은 쓰기 트랜잭션 경계 서비스
 */
public interface TryOnWriteService {

    /**
     * 외부 연산이 끝난 이후, DB에 필요한 엔티티들을 원자적으로 저장하고 응답을 만든다.
     */
    TryOnResponse saveAndBuildResponse(
            TryOnResult tryOnResult,
            Category category,
            String clothImageUrl,
            String productPageUrl,
            String modelUrl,
            String uploadedResultImageUrl,
            String virtualFittingId,
            DefaultModel defaultModel,
            User currentUser);
}
