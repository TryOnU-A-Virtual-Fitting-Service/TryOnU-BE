package tryonu.api.service.defaultmodel;

import org.springframework.web.multipart.MultipartFile;
import tryonu.api.dto.responses.DefaultModelResponse;
import tryonu.api.dto.responses.DefaultModelDto;

import java.util.List;

public interface DefaultModelService {
    /**
     * 기본 모델 업로드
     * 
     * @param file 업로드할 기본 모델 이미지 파일
     * @return 업로드된 기본 모델 정보
     */
    DefaultModelResponse uploadDefaultModel(MultipartFile file);
    
    /**
     * 현재 사용자의 기본 모델 목록 조회
     * 
     * @return 현재 사용자의 기본 모델 목록
     */
    List<DefaultModelDto> getCurrentUserDefaultModels();
}
