package tryonu.api.converter;

import org.springframework.stereotype.Component;
import tryonu.api.dto.responses.UserInfoResponse;
import tryonu.api.dto.responses.SimpleUserResponse;
import tryonu.api.dto.responses.DefaultModelDto;
import tryonu.api.dto.responses.TryOnResultDto;
import tryonu.api.domain.User;

import java.util.List;

@Component
public class UserConverter {

    /** 
     * SimpleUserResponse 생성
     */
    public SimpleUserResponse toSimpleUserResponse(User user) {
        return new SimpleUserResponse(user.getId(), user.getUuid());
    }

    /** 
     * UserInfoResponse 생성
     */
    public UserInfoResponse toUserInfoResponse(List<DefaultModelDto> defaultModels, List<TryOnResultDto> tryOnResults) {
        return new UserInfoResponse(defaultModels, tryOnResults);
    }
} 