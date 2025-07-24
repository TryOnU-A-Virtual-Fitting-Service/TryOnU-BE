package tryonu.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 및 Swagger UI 설정
 * API 문서화를 위한 Swagger 설정을 관리합니다.
 */
@Slf4j
@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;
    
    @Value("${app.swagger.servers.staging-http}")
    private String stagingHttpUrl;
    
    @Value("${app.swagger.servers.staging-https}")
    private String stagingHttpsUrl;
    
    @Value("${app.swagger.servers.local}")
    private String localUrl;

    /**
     * OpenAPI 설정
     * Swagger UI에서 사용할 API 문서 정보와 서버 URL을 설정합니다.
     * 
     * @return OpenAPI 설정 객체
     */
    @Bean
    public OpenAPI customOpenAPI() {
        log.info("📚 [OpenApiConfig] OpenAPI 설정 초기화");

        return new OpenAPI()
                .info(new Info()
                        .title("ThatzFit API")
                        .description("가상 피팅 서비스 API 문서")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ThatzFit Team")
                                .email("dev@thatzfit.com")))
                .servers(List.of(
                        new Server()
                                .url(stagingHttpUrl + contextPath)
                                .description("스테이징 서버 (HTTP)"),
                        new Server()
                                .url(stagingHttpsUrl + contextPath)
                                .description("스테이징 서버 (HTTPS)"),
                        new Server()
                                .url(localUrl + contextPath)
                                .description("로컬 개발 서버")
                ));
    }
} 