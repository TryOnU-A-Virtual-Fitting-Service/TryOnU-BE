package tryonu.api.service.defaultmodel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tryonu.api.common.util.ImageUploadUtil;
import tryonu.api.domain.DefaultModel;
import tryonu.api.domain.User;
import tryonu.api.dto.responses.DefaultModelResponse;
import tryonu.api.repository.defaultmodel.DefaultModelRepository;
import tryonu.api.repository.user.UserRepository;
import tryonu.api.converter.DefaultModelConverter;

@Service
@RequiredArgsConstructor
public class DefaultModelServiceImpl implements DefaultModelService {
    private final UserRepository userRepository;
    private final DefaultModelRepository defaultModelRepository;

    private final DefaultModelConverter defaultModelConverter;
    private final ImageUploadUtil imageUploadUtil;

    @Override
    @Transactional
    public DefaultModelResponse uploadDefaultModel(String deviceId, MultipartFile file) {
        // 1. 사용자 조회
        User user = userRepository.findByDeviceIdAndIsDeletedFalseOrThrow(deviceId);
        
        // 2. 이미지 S3 업로드
        String imageUrl = imageUploadUtil.uploadModelImage(file);

        // 3. DefaultModel 엔티티 생성 및 저장
        DefaultModel defaultModel = defaultModelConverter.createDefaultModel(user, imageUrl);
        DefaultModel saved = defaultModelRepository.save(defaultModel);

        // 4. 응답 DTO 생성 및 반환
        return new DefaultModelResponse(saved.getId(), saved.getImageUrl());
    }
}
