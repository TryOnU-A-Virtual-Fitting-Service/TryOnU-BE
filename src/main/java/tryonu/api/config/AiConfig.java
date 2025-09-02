package tryonu.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import java.time.Duration;

@Slf4j
@Configuration
public class AiConfig {

    @Value("${spring.ai.chat.model:unknown}")
    private String modelId;

    @Bean
    public BedrockRuntimeClient bedrockRuntimeClient(
            @Value("${spring.ai.bedrock.aws.region}") String region) {
        log.info("[AiConfig] Initializing BedrockRuntimeClient (region={}, modelId={})", region, modelId);
        return BedrockRuntimeClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .overrideConfiguration(c -> c
                        .apiCallTimeout(Duration.ofSeconds(30))
                        .apiCallAttemptTimeout(Duration.ofSeconds(10))
                )
                .build();
    }

}


