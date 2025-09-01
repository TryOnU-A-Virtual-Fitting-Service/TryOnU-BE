package tryonu.api.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class BackgroundRemovalUtil {
    private final WebClient backgroundRemovalWebClient;
    private final WebClient imageDownloadWebClient; // 이미지 다운로드 전용

    /**
     * 배경 제거 API 호출 (MultipartFile → byte[])
     */
    public byte[] removeBackground(MultipartFile file) {
        try {
            // filename을 명시적으로 설정하여 서버가 파일명을 요구하는 경우에 대응
            String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg";

            // MultipartFile을 byte[]로 변환하여 여러 번 읽을 있게 함
            byte[] fileBytes = file.getBytes();

            // filename이 포함된 ByteArrayResource 생성 (여러 번 읽기 가능)
            Resource fileResource = new ByteArrayResource(
                    fileBytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };

            byte[] result = backgroundRemovalWebClient.post()
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("file", fileResource))
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            // null 응답 체크
            if (result == null || result.length == 0) {
                throw new CustomException(ErrorCode.BACKGROUND_REMOVAL_FAILED,
                        "배경 제거 API에서 유효하지 않은 응답을 받았습니다.");
            }

            return result;
        } catch (Exception e) {
            log.error("[BackgroundRemovalUtil] 배경 제거 실패 - fileName={}, error={}",
                    file.getOriginalFilename(), e.getMessage(), e);

            throw new CustomException(ErrorCode.BACKGROUND_REMOVAL_FAILED, "이미지 배경 제거에 실패했습니다.");
        }
    }

    /**
     * 배경 제거 API 호출 (URL에서 이미지 다운로드 → byte[])
     */
    public byte[] removeBackground(String imageUrl) {
        try {
            // 1. URL에서 이미지 다운로드 (전용 WebClient 사용)
            byte[] imageBytes = imageDownloadWebClient
                    .get()
                    .uri(imageUrl)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            if (imageBytes == null || imageBytes.length == 0) {
                throw new RuntimeException("URL에서 이미지를 다운로드할 수 없습니다: " + imageUrl);
            }

            log.info("[BackgroundRemovalUtil] 이미지 다운로드 완료 - imageUrl={}, size={}bytes",
                    imageUrl, imageBytes.length);

            // 2. 배경 제거 API 호출
            byte[] result = backgroundRemovalWebClient.post()
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("file",
                            new org.springframework.core.io.ByteArrayResource(imageBytes) {
                                @Override
                                public String getFilename() {
                                    return "image.png";
                                }
                            }))
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            // null 응답 체크
            if (result == null || result.length == 0) {
                throw new CustomException(ErrorCode.BACKGROUND_REMOVAL_FAILED,
                        "배경 제거 API에서 유효하지 않은 응답을 받았습니다.");
            }

            return result;
        } catch (Exception e) {
            log.error("[BackgroundRemovalUtil] URL 이미지 배경 제거 실패 - imageUrl={}, error={}",
                    imageUrl, e.getMessage(), e);
            throw new CustomException(ErrorCode.BACKGROUND_REMOVAL_FAILED, "이미지 배경 제거에 실패했습니다.");
        }
    }
}