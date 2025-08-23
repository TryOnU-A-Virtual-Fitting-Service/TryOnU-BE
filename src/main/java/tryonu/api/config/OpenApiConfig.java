package tryonu.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springdoc.core.customizers.OpenApiCustomizer;

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
     * ApiResponseWrapper를 자동으로 unwrapping하는 customizer
     * 컨트롤러에서 @ApiResponse(schema = @Schema(implementation = T.class))로 지정한 경우
     * 자동으로 ApiResponseWrapper<T> 구조로 변환합니다.
     * 
     * @return OpenApiCustomizer
     */
    @Bean
    public OpenApiCustomizer unwrapResponseWrapper() {
        return openApi -> {
            log.info("🔧 [OpenApiConfig] ApiResponseWrapper unwrapping customizer 적용");
            
            // 모든 경로의 응답 스키마를 검사하여 ApiResponseWrapper 구조로 변환
            openApi.getPaths().forEach((path, pathItem) -> {
                pathItem.readOperations().forEach(operation -> {
                    if (operation.getResponses() != null) {
                        operation.getResponses().forEach((statusCode, response) -> {
                            if (response.getContent() != null) {
                                response.getContent().forEach((contentType, content) -> {
                                    if (content.getSchema() != null && content.getSchema().get$ref() != null) {
                                        // 스키마 참조가 있는 경우 ApiResponseWrapper로 래핑
                                        wrapSchemaWithApiResponseWrapper(openApi, content);
                                    }
                                });
                            }
                        });
                    }
                });
            });
        };
    }

    /**
     * 스키마를 ApiResponseWrapper로 래핑합니다.
     * 
     * @param openApi OpenAPI 객체
     * @param content MediaType 객체
     */
    private void wrapSchemaWithApiResponseWrapper(OpenAPI openApi, MediaType content) {
        Schema<?> originalSchema = content.getSchema();
        String originalRef = originalSchema.get$ref();
        
        if (originalRef != null && !originalRef.contains("ApiResponseWrapper")) {
            // 원본 스키마 이름 추출
            String schemaName = originalRef.substring(originalRef.lastIndexOf("/") + 1);
            
            // 새로운 래퍼 스키마 이름 생성
            String wrapperName = schemaName + "Wrapper";
            
            // ComposedSchema를 사용하여 ApiResponseWrapper와 원본 스키마를 조합
            ComposedSchema wrapperSchema = new ComposedSchema();
            wrapperSchema.addAllOfItem(new Schema<>().$ref("#/components/schemas/ApiResponseWrapper"));
            wrapperSchema.addAllOfItem(new Schema<>().$ref(originalRef));
            
            // 래퍼 스키마를 components에 추가
            openApi.getComponents().getSchemas().put(wrapperName, wrapperSchema);
            
            // content의 스키마를 래퍼 스키마로 변경
            content.setSchema(new Schema<>().$ref("#/components/schemas/" + wrapperName));
            
            log.debug("🔧 [OpenApiConfig] 스키마 래핑 완료: {} -> {}", schemaName, wrapperName);
        }
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
                        .description("로컬 개발 서버"),
                    new Server()
                        .url("http://localhost:3000" + contextPath)
                        .description("로컬 프론트엔드 서버")
                );
        }
    }
} 