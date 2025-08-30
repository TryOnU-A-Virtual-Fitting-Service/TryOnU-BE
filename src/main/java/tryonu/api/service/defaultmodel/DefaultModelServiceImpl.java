package tryonu.api.service.defaultmodel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tryonu.api.common.util.ImageUploadUtil;
import tryonu.api.domain.DefaultModel;
import tryonu.api.dto.requests.DefaultModelBatchUpdateRequest;
import tryonu.api.dto.responses.DefaultModelResponse;
import tryonu.api.repository.defaultmodel.DefaultModelRepository;
import tryonu.api.converter.DefaultModelConverter;
import tryonu.api.common.auth.SecurityUtils;
import tryonu.api.domain.User;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.common.util.BackgroundRemovalUtil;
import tryonu.api.dto.responses.DefaultModelDto;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultModelServiceImpl implements DefaultModelService {
    private final DefaultModelRepository defaultModelRepository;

    private final DefaultModelConverter defaultModelConverter;
    private final ImageUploadUtil imageUploadUtil;

    private final BackgroundRemovalUtil backgroundRemovalUtil;

    @Override
    @Transactional
    public DefaultModelResponse uploadDefaultModel(MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "업로드할 파일이 비어있습니다.");
        }

        User currentUser = SecurityUtils.getCurrentUser();

        // 배경 제거
        byte[] backgroundRemovedImage = backgroundRemovalUtil.removeBackground(file);

        // 이미지 S3 업로드
        String imageUrl = imageUploadUtil.uploadModelImage(backgroundRemovedImage);

        // 자동으로 다음 sortOrder 계산
        Integer nextSortOrder = getNextSortOrder(currentUser.getId());

        // DefaultModel 엔티티 생성 및 저장
        DefaultModel defaultModel = defaultModelConverter.createDefaultModel(currentUser, imageUrl, "커스텀 모델",
                nextSortOrder);
        DefaultModel saved = defaultModelRepository.save(defaultModel);

        return defaultModelConverter.toDefaultModelResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DefaultModelDto> getCurrentUserDefaultModels() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<DefaultModelDto> defaultModels = defaultModelRepository
                .findDefaultModelsByUserIdOrderBySortOrder(currentUserId);
        return defaultModels;
    }

    @Override
    @Transactional
    public void batchUpdateDefaultModels(DefaultModelBatchUpdateRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 요청된 모든 ID 수집
        List<Long> requestedIds = request.defaultModels().stream()
                .map(item -> item.id())
                .toList();

        if (requestedIds.isEmpty()) {
            return;
        }

        // 사용자 소유 및 존재하는 모델들 조회
        List<DefaultModel> existingModels = defaultModelRepository.findAllByIdsAndUserIdAndIsDeletedFalse(requestedIds,
                currentUserId);

        // 요청된 ID와 실제 존재하는 ID 비교 (권한 확인)
        if (existingModels.size() != requestedIds.size()) {
            List<Long> foundIds = existingModels.stream().map(DefaultModel::getId).toList();
            List<Long> notFoundIds = requestedIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new CustomException(ErrorCode.DEFAULT_MODEL_NOT_FOUND,
                    String.format("사용자가 소유하지 않은 기본 모델입니다: %s", notFoundIds));
        }

        // ID를 키로 하는 Map으로 변환하여 조회 성능 개선 (O(N^2) -> O(N))
        Map<Long, DefaultModel> modelMap = existingModels.stream()
                .collect(Collectors.toMap(DefaultModel::getId, Function.identity()));

        // 각 요청 항목에 대해 처리
        request.defaultModels().forEach(item -> {
            DefaultModel model = modelMap.get(item.id());

            switch (item.status()) {
                case UPDATE -> {
                    // sortOrder가 null이면 기존 값 유지
                    if (item.sortOrder() != null) {
                        model.setSortOrder(item.sortOrder());
                    }
                    // modelName이 null이면 기존 값 유지
                    if (item.modelName() != null) {
                        model.setModelName(item.modelName());
                    }
                }
                case DELETE -> model.setIsDeleted(true);
            }
        });

        // 일괄 저장
        defaultModelRepository.saveAll(existingModels);
    }

    /**
     * 다음 sortOrder 계산 (사용자의 최대 sortOrder + 1)
     */
    private Integer getNextSortOrder(Long userId) {
        Integer maxSortOrder = defaultModelRepository.findMaxSortOrderByUserId(userId);
        return maxSortOrder + 1;
    }

}
