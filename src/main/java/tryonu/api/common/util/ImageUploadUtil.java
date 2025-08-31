package tryonu.api.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.stereotype.Component;
import java.util.Set;
import jakarta.annotation.PostConstruct;
import tryonu.api.common.exception.CustomException;
import tryonu.api.common.exception.enums.ErrorCode;

/**
 * 이미지 업로드 유틸리티 클래스
 * S3에 이미지를 업로드하고 URL을 반환하는 공통 기능을 제공합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ImageUploadUtil {
    private final S3Client s3Client;
    private final WebClient imageDownloadWebClient;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.model-folder}")
    private String modelFolder;

    @Value("${aws.s3.cloth-folder}")
    private String clothFolder;

    @Value("${aws.s3.tryonresult-folder}")
    private String tryonResultFolder;

    @Value("${file.upload.allowed-types}")
    private String allowedExtensionsConfig;

    @Value("${file.upload.allowed-content-types}")
    private String allowedContentTypesConfig;

    @Value("${file.upload.max-size}")
    private long maxFileSize;

    @Value("${aws.cloudfront.domain}")
    private String cloudfrontDomain;

    private Set<String> allowedExtensions;
    private Set<String> allowedContentTypes;

    @PostConstruct
    private void initAllowedExtensions() {
        this.allowedExtensions = Set.of(allowedExtensionsConfig.split(","));
    }

    @PostConstruct
    private void initAllowedContentTypes() {
        this.allowedContentTypes = Set.of(allowedContentTypesConfig.split(","));
    }

    /**
     * S3에 이미지를 업로드합니다.
     *
     * @param file       업로드할 파일
     * @param folderPath S3 내 폴더 경로 (예: "users/profiles", "models", "clothes")
     * @return 업로드된 이미지의 S3 URL
     * @throws IllegalArgumentException 파일 검증 실패 시
     * @throws RuntimeException         업로드 실패 시
     */
    public String uploadToS3(MultipartFile file, String folderPath) {
        log.info("[ImageUploadUtil] 이미지 업로드 시작 - fileName={}, folderPath={}", file.getOriginalFilename(), folderPath);

        // 파일 검증 (확장자, Content-Type 포함)
        validateFile(file);

        try {
            // 파일명 생성 (중복 방지)
            String fileName = generateFileName(file.getOriginalFilename());
            String s3Key = folderPath + "/" + fileName;

            // S3 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();
            // 파일 업로드 (InputStream 안전하게 처리)
            try (InputStream inputStream = file.getInputStream()) {
                s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
            } // 자동 close 처리

            // S3 URL 생성 (CloudFront 도메인 사용)
            String imageUrl = cloudfrontDomain + "/" + s3Key;

            log.info("[ImageUploadUtil] 이미지 업로드 성공 - imageUrl={}", imageUrl);

            return imageUrl;

        } catch (Exception e) {
            log.error("[ImageUploadUtil] S3 업로드 실패 - fileName={}, error={}", file.getOriginalFilename(), e.getMessage(),
                    e);

            throw new RuntimeException("S3 업로드 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * S3에 이미지를 업로드합니다. (byte[] 버전)
     *
     * @param image       업로드할 이미지 바이트 배열
     * @param folderPath  S3 내 폴더 경로
     * @param contentType Content-Type (예: image/png)
     * @return 업로드된 이미지의 S3 URL
     */
    public String uploadToS3(byte[] image, String folderPath, String contentType) {
        try {
            String fileName = generateFileName("image.png");
            String s3Key = folderPath + "/" + fileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .contentLength((long) image.length)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(image));

            String imageUrl = cloudfrontDomain + "/" + s3Key;
            log.info("[ImageUploadUtil] 이미지 업로드 성공(byte[]) - imageUrl={}", imageUrl);

            return imageUrl;
        } catch (Exception e) {
            log.error("[ImageUploadUtil] S3 업로드 실패(byte[]) - error={}", e.getMessage(), e);

            throw new RuntimeException("S3 업로드 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 피팅 모델 이미지를 업로드합니다.
     */
    public String uploadModelImage(byte[] image) {
        return uploadToS3(image, modelFolder, "image/png");
    }

    public String uploadModelImage(MultipartFile file) {
        return uploadToS3(file, modelFolder);
    }

    /**
     * 의류 이미지를 업로드합니다.
     */
    public String uploadClothImage(MultipartFile file) {
        return uploadToS3(file, clothFolder);
    }

    public String uploadClothImage(byte[] image) {
        return uploadToS3(image, clothFolder, "image/jpeg");
    }

    /**
     * 트라이온 결과 이미지를 업로드합니다.
     */
    public String uploadTryOnResultImage(MultipartFile file) {
        return uploadToS3(file, tryonResultFolder);
    }

    public String uploadTryOnResultImage(byte[] image) {
        return uploadToS3(image, tryonResultFolder, "image/png");
    }

    /**
     * URL에서 트라이온 결과 이미지를 다운로드하여 S3에 업로드합니다.
     */
    public String uploadTryOnResultImageFromUrl(String imageUrl) {
        log.info("[ImageUploadUtil] URL에서 이미지 다운로드 시작 - url={}", imageUrl);

        try {
            // WebClient로 이미지 다운로드
            byte[] imageBytes = imageDownloadWebClient
                    .get()
                    .uri(imageUrl)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block(); // 동기 방식으로 처리

            if (imageBytes == null || imageBytes.length == 0) {
                throw new CustomException(ErrorCode.VIRTUAL_FITTING_FAILED, "이미지 다운로드에 실패했습니다.");
            }

            log.info("[ImageUploadUtil] 이미지 다운로드 완료 - size={}KB", imageBytes.length / 1024);

            // 다운로드된 이미지를 S3에 업로드
            String s3Url = uploadTryOnResultImage(imageBytes);

            log.info("[ImageUploadUtil] 이미지 다운로드 및 S3 업로드 완료 - originalUrl={}, s3Url={}", imageUrl, s3Url);

            return s3Url;

        } catch (Exception e) {
            log.error("[ImageUploadUtil] 이미지 다운로드 및 업로드 실패 - url={}, error={}", imageUrl, e.getMessage(), e);

            throw new CustomException(ErrorCode.VIRTUAL_FITTING_FAILED, "이미지 다운로드 및 업로드에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 파일을 검증합니다.
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "업로드할 파일이 없습니다.");
        }

        if (file.getSize() > maxFileSize) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "파일 크기가 " + maxFileSize + "바이트를 초과합니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "파일명이 없습니다.");
        }

        String extension = getFileExtension(originalFilename);
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new CustomException(
                    ErrorCode.INVALID_REQUEST, "지원하지 않는 파일 형식입니다. 지원 형식: " + String.join(", ", allowedExtensions) +
                            " (실제: " + extension + ")");
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType.toLowerCase())) {
            throw new CustomException(
                    ErrorCode.INVALID_REQUEST,
                    "지원하지 않는 Content-Type입니다. 지원 형식: " + String.join(", ", allowedContentTypes) +
                            " (실제: " + contentType + ")");
        }
    }

    /**
     * 고유한 파일명을 생성합니다.
     */
    private static String generateFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        return timestamp + "_" + uuid + "." + extension;
    }

    /**
     * 파일 확장자를 추출합니다.
     */
    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1).toLowerCase() : "";
    }
}