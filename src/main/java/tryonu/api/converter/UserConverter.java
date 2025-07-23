package tryonu.api.converter;

import org.springframework.stereotype.Component;
import tryonu.api.dto.responses.UserInfoResponse;
import tryonu.api.dto.responses.FittingModelDto;
import tryonu.api.dto.responses.DefaultModelDto;

import java.util.List;

@Component
public class UserConverter {

    /**
     * UserInfoResponse 생성
     */
    public UserInfoResponse toUserInfoResponse(List<FittingModelDto> fittingModels, List<DefaultModelDto> defaultModels) {
        return new UserInfoResponse(fittingModels, defaultModels);
    }
} 