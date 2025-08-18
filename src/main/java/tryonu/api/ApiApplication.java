package tryonu.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "ThatzFit Virtual Fitting API",
        version = "1.0.0",
        description = "가상 피팅 서비스 API"
    )
)
@SecurityScheme(
    name = "DeviceId",
    type = SecuritySchemeType.APIKEY,
    in = io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER,
    paramName = "X-UUID",
    description = "UUID 헤더"
)
public class ApiApplication {

    public static void main(String[] args) {
        log.info("🚀 [ApiApplication] TryOnU Virtual Fitting API 시작");
        SpringApplication.run(ApiApplication.class, args);
        log.info("✅ [ApiApplication] TryOnU Virtual Fitting API 시작 완료");
    }

}
