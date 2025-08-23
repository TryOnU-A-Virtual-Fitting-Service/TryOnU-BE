package tryonu.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.util.List;

/**
 * OpenAPI 3.0 및 Swagger UI 설정
 * API 문서화를 위한 Swagger 설정을 관리합니다.
 * 환경별로 다른 서버 URL을 설정하고 ApiResponseWrapper를 자동으로 unwrapping합니다.
 */
@Slf4j
@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Value("${app.swagger.servers.staging-https}")
    private String stagingHttpsUrl;

    @Value("${app.swagger.servers.staging-http}")
    private String stagingHttpUrl;

    @Value("${app.swagger.servers.local:http://localhost:8080}")
    private String localUrl;

    @Value("${app.swagger.servers.prod-https}")
    private String prodHttpsUrl;

    private final Environment environment;

    public OpenApiConfig(Environment environment) {
        this.environment = environment;
    }

    /**
     * OpenAPI 설정
     * Swagger UI에서 사용할 API 문서 정보와 서버 URL을 환경별로 설정합니다.
     * 
     * @return OpenAPI 설정 객체
     */
    @Bean
    public OpenAPI customOpenAPI() {
        String activeProfile = getActiveProfile();
        log.info("📚 [OpenApiConfig] OpenAPI 설정 초기화 - 환경: {}", activeProfile);

        List<Server> servers = getServersByProfile(activeProfile);
        
        // 설정된 서버 정보 로깅
        log.info("🌐 [OpenApiConfig] 환경별 서버 설정 완료:");
        servers.forEach(server -> 
            log.info("   - {}: {}", server.getDescription(), server.getUrl())
        );

        return new OpenAPI()
                .info(new Info()
                        .title("ThatzFit API")
                        .description("가상 피팅 서비스 API 문서")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ThatzFit Team")
                                .email("tryonu.team@gmail.com")))
                .servers(servers);
    }

    /**
     * 현재 활성화된 프로파일을 우선순위에 따라 가져옵니다.
     * 우선순위: prod > staging > dev > local
     * 
     * @return 활성화된 프로파일명
     */
    private String getActiveProfile() {
        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            return "prod";
        }
        if (environment.acceptsProfiles(Profiles.of("dev"))) {
            return "dev";
        }
        return "local";
    }

    /**
     * 프로파일별로 서버 목록을 설정합니다.
     * 
     * @param profile 활성화된 프로파일
     * @return 서버 목록
     */
    private List<Server> getServersByProfile(String profile) {
        switch (profile) {
            case "dev":
                return List.of(
                    new Server()
                        .url(stagingHttpsUrl + contextPath)
                        .description("개발 서버 (HTTPS)"),
                    new Server()
                        .url(stagingHttpUrl + contextPath)
                        .description("개발 서버 (HTTP)")
                );
            case "prod":
                return List.of(
                    new Server()
                        .url(prodHttpsUrl + contextPath)
                        .description("운영 서버 (HTTPS)")
                );
            default: // local 환경 및 기타 환경
                return List.of(
                    new Server()
                        .url(localUrl + contextPath)
                        .description("로컬 개발 서버")
                );
        }
    }
} 