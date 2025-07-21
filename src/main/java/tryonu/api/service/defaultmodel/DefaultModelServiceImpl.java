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

@Service
@RequiredArgsConstructor
public class DefaultModelServiceImpl implements DefaultModelService {
    private final DefaultModelRepository defaultModelRepository;

    private final DefaultModelConverter defaultModelConverter;
    private final ImageUploadUtil imageUploadUtil;

    @Override
    @Transactional
    public DefaultModelResponse uploadDefaultModel(MultipartFile file) {
        // 이미지 S3 업로드
        String imageUrl = imageUploadUtil.uploadModelImage(file);

        // DefaultModel 엔티티 생성 및 저장
        DefaultModel defaultModel = defaultModelConverter.createDefaultModel(SecurityUtils.getCurrentUser(), imageUrl);
        DefaultModel saved = defaultModelRepository.save(defaultModel);

        // 응답 DTO 생성 및 반환
        return new DefaultModelResponse(saved.getId(), saved.getImageUrl());
    }
}
