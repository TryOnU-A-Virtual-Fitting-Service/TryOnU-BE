package tryonu.api.service.defaultmodel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tryonu.api.common.util.ImageUploadUtil;
import tryonu.api.domain.DefaultModel;
import tryonu.api.dto.responses.DefaultModelResponse;
import tryonu.api.repository.defaultmodel.DefaultModelRepository;
import tryonu.api.converter.DefaultModelConverter;
import tryonu.api.common.auth.SecurityUtils;
import tryonu.api.domain.User;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;
import tryonu.api.common.util.BackgroundRemovalUtil;

@Service
@RequiredArgsConstructor
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

        // DefaultModel 엔티티 생성 및 저장
        DefaultModel defaultModel = defaultModelConverter.createDefaultModel(currentUser, imageUrl);
        DefaultModel saved = defaultModelRepository.save(defaultModel);

        // 응답 DTO 생성 및 반환
        return new DefaultModelResponse(saved.getId(), saved.getImageUrl());
    }

    
}
