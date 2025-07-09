package tryonu.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        log.info("🚀 [ApiApplication] TryOnU Virtual Fitting API 시작");
        SpringApplication.run(ApiApplication.class, args);
        log.info("✅ [ApiApplication] TryOnU Virtual Fitting API 시작 완료");
    }

}
