package tryonu.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tryonu.api.common.wrapper.ApiResponseWrapper;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/monitoring")
@Tag(name = "모니터링 API", description = "운영 모니터링 관련 API")
public class MonitoringController {

    @GetMapping("/memory")
    @Operation(summary = "힙 메모리 사용량 조회", description = "현재 JVM 힙 메모리 사용량과 비율을 반환합니다.")
    public ApiResponseWrapper<Map<String, Object>> getHeapMemory() {
        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory() / 1024 / 1024;
        long free = runtime.freeMemory() / 1024 / 1024;
        long used = total - free;
        long max = runtime.maxMemory() / 1024 / 1024;
        double usagePercent = max > 0 ? (used * 100.0 / max) : 0.0;

        Map<String, Object> data = Map.of(
                "heapUsedMB", used,
                "heapTotalMB", total,
                "heapMaxMB", max,
                "usagePercent", Math.round(usagePercent * 10) / 10.0
        );
        log.debug("[MonitoringController] 메모리 조회 - {}", data);
        return ApiResponseWrapper.ofSuccess(data);
    }
}


