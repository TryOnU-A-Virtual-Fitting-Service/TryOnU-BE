package tryonu.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import tryonu.api.common.exception.enums.ErrorCode;

@Configuration
public class StorageConfig {

    @Value("${aws.credentials.access-key}")
    private String accessKey;

    @Value("${aws.credentials.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.default-model-folder}")
    private String defaultModelFolder;

    /**
     * S3 클라이언트 빈 생성
     * S3 업로드/다운로드 작업에 사용
     */
    @Bean
    public S3Client s3Client() {
        try {
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)
                    ))
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException(ErrorCode.S3_CLIENT_CREATION_FAILED.getMessage(), e);
        }
    }

    /**
     * S3 버킷 이름 반환
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * S3 리전 반환
     */
    public String getRegion() {
        return region;
    }

    /**
     * 기본 모델 폴더 경로 반환
     */
    public String getDefaultModelFolder() {
        return defaultModelFolder;
    }
    
}