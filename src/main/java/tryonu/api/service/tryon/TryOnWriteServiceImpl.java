package tryonu.api.service.tryon;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import tryonu.api.common.enums.Category;
import tryonu.api.domain.Cloth;
import tryonu.api.domain.DefaultModel;
import tryonu.api.domain.TryOnResult;
import tryonu.api.domain.User;
import tryonu.api.dto.responses.TryOnResponse;
import tryonu.api.repository.cloth.ClothRepository;
import tryonu.api.repository.tryonresult.TryOnResultRepository;
import tryonu.api.repository.user.UserRepository;
import tryonu.api.converter.TryOnResultConverter;

@Service
@RequiredArgsConstructor
public class TryOnWriteServiceImpl implements TryOnWriteService {

    private final TryOnResultRepository tryOnResultRepository;
    private final ClothRepository clothRepository;
    private final TryOnResultConverter tryOnResultConverter;
    private final UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TryOnResponse saveAndBuildResponse(
            TryOnResult tryOnResult,
            Category category,
            String clothImageUrl,
            String productPageUrl,
            String modelUrl,
            String uploadedResultImageUrl,
            String virtualFittingId,
            DefaultModel defaultModel,
            User currentUser) {
        Cloth cloth = tryOnResultConverter.toClothEntity(clothImageUrl, productPageUrl, category);
        clothRepository.save(cloth);

        // 기존 tryOnResult 업데이트
        tryOnResult.updateTryOnResult(
                cloth,
                currentUser,
                modelUrl,
                uploadedResultImageUrl,
                virtualFittingId,
                defaultModel.getId());
        tryOnResultRepository.save(tryOnResult);

        currentUser.updateRecentlyUsedModelUrl(uploadedResultImageUrl);
        currentUser.updateRecentlyUsedModelName(defaultModel.getModelName());
        currentUser.updateRecentlyUsedModelId(defaultModel.getId());
        userRepository.save(currentUser);

        return tryOnResultConverter.toTryOnResponse(tryOnResult, defaultModel.getModelName());
    }
}
