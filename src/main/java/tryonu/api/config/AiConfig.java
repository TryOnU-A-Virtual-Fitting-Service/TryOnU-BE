package tryonu.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

@Slf4j
@Configuration
public class AiConfig {

    @Value("${spring.ai.chat.model:unknown}")
    private String modelId;

    @Bean
    public BedrockRuntimeClient bedrockRuntimeClient(
            @Value("${spring.ai.bedrock.aws.region}") String region,
            @Value("${spring.ai.bedrock.aws.access-key}") String accessKey,
            @Value("${spring.ai.bedrock.aws.secret-key}") String secretKey) {
        log.info("[AiConfig] Initializing BedrockRuntimeClient (region={}, modelId={})", region, modelId);
        return BedrockRuntimeClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

}


