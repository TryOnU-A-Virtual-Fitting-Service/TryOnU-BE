package tryonu.api.service.tryon;

import org.springframework.web.multipart.MultipartFile;
import tryonu.api.dto.responses.TryOnResponse;

/**
 * 가상 피팅 서비스 인터페이스
 */
public interface TryOnService {
    
    /**
     * 가상 피팅 실행
     *
     * @param modelUrl 모델 이미지 URL
     * @param productPageUrl 상품 페이지 URL
     * @param file 의류 이미지 파일
     * @return 가상 피팅 결과
     */
    TryOnResponse tryOn(String modelUrl, String productPageUrl, MultipartFile file);
} 