package tryonu.api.converter;

import org.springframework.stereotype.Component;
import tryonu.api.dto.responses.UserInfoResponse;
import tryonu.api.dto.responses.SimpleUserResponse;
import tryonu.api.dto.responses.DefaultModelDto;
import tryonu.api.dto.responses.TryOnResultDto;
import tryonu.api.dto.responses.RecentlyUsedModel;
import tryonu.api.domain.User;

import java.util.List;

@Component
public class UserConverter {

    /** 
     * SimpleUserResponse 생성
     */
    public SimpleUserResponse toSimpleUserResponse(User user) {
        RecentlyUsedModel recentlyUsedModel = createRecentlyUsedModel(user.getRecentlyUsedModelId(), user.getRecentlyUsedModelUrl(), user.getRecentlyUsedModelName());
        return new SimpleUserResponse(recentlyUsedModel);
    }

    /**
     * URL에서 파일명을 추출하여 RecentlyUsedModel 생성
     */
    private RecentlyUsedModel createRecentlyUsedModel(Long defaultModelId, String modelUrl, String modelName) {
        if (modelUrl == null || modelUrl.isEmpty()) {
            return null;
        }
        
        String imageName = extractImageNameFromUrl(modelUrl);
        return new RecentlyUsedModel(defaultModelId, modelUrl, imageName, modelName);
    }

    /**
     * URL에서 이미지 파일명 추출
     */
    private String extractImageNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < url.length() - 1) {
            return url.substring(lastSlashIndex + 1);
        }
        
        return url; // URL에 '/'가 없으면 전체 문자열 반환
    }

    /** 
     * UserInfoResponse 생성
     */
    public UserInfoResponse toUserInfoResponse(List<DefaultModelDto> defaultModels, List<TryOnResultDto> tryOnResults) {
        return new UserInfoResponse(defaultModels, tryOnResults);
    }
} 