package tryonu.api.service.tryon;

import org.springframework.web.multipart.MultipartFile;
import tryonu.api.dto.requests.TryOnRequestDto;
import tryonu.api.dto.responses.TryOnJobInitResponse;
import tryonu.api.dto.responses.TryOnResponse;
import tryonu.api.dto.responses.TryOnResultDto;
import tryonu.api.dto.responses.UserInfoResponse;

import java.util.List;

/**
 * 가상 피팅 서비스 인터페이스
 */
public interface TryOnService {

    /**
     * 새로운 가상 피팅 작업(Job)을 생성합니다.
     * 클라이언트가 피팅 작업을 시작할 때 호출되는 메서드입니다.
     *
     * @return 생성된 피팅 작업 정보 응답 DTO
     */
    TryOnJobInitResponse createTryOnJob();

    /**
     * 가상 피팅 실행
     *
     * @param request 가상 피팅 요청 정보 (모델 URL, 기본 모델 ID, 상품 페이지 URL)
     * @param file    의류 이미지 파일
     * @return 가상 피팅 결과
     */
    TryOnResponse tryOn(TryOnRequestDto request, MultipartFile file);

    /**
     * 현재 사용자의 피팅 결과 목록 조회
     * 
     * @return 현재 사용자의 피팅 결과 목록
     */
    List<TryOnResultDto> getCurrentUserTryOnResults();

    /**
     * 현재 사용자의 기본 모델과 피팅 결과 목록 조회
     * 
     * @return 현재 사용자의 기본 모델과 피팅 결과 목록
     */
    UserInfoResponse getCurrentUserAllData();
}