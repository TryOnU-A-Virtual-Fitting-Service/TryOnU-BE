package tryonu.api.service.defaultmodel;

import org.springframework.web.multipart.MultipartFile;
import tryonu.api.dto.responses.DefaultModelResponse;

public interface DefaultModelService {
    DefaultModelResponse uploadDefaultModel(String deviceId, MultipartFile file);
}
