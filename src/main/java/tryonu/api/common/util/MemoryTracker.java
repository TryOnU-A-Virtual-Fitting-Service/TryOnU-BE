package tryonu.api.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 메모리 사용량 추적 유틸리티
 * S3 업로드 등 메모리 집약적 작업의 메모리 사용량을 상세 추적
 */
@Slf4j
@Component
public class MemoryTracker {
    
    private final MeterRegistry meterRegistry;
    private final Map<String, Long> operationStartMemory = new ConcurrentHashMap<>();
    
    public MemoryTracker(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // 현재 메모리 사용량을 실시간으로 게이지에 등록
        Gauge.builder("custom.memory.heap.used", () -> getCurrentHeapUsageMB())
                .description("현재 힙 메모리 사용량 (MB)")
                .register(meterRegistry);
    }
    
    /**
     * 작업 시작 시 메모리 상태 기록
     */
    public void startTracking(String operationName, String fileSize) {
        long currentMemory = getCurrentHeapUsage();
        String key = operationName + "-" + Thread.currentThread().threadId();
        operationStartMemory.put(key, currentMemory);
        
        log.info("🟢 [MemoryTracker] {} 시작 - 파일크기: {}, 시작메모리: {}MB", 
                operationName, fileSize, currentMemory / 1024 / 1024);
    }
    
    /**
     * 작업 종료 시 메모리 사용량 계산 및 로깅
     */
    public void endTracking(String operationName, boolean success) {
        String key = operationName + "-" + Thread.currentThread().threadId();
        Long startMemory = operationStartMemory.remove(key);
        
        if (startMemory != null) {
            long endMemory = getCurrentHeapUsage();
            long memoryDiff = endMemory - startMemory;
            double memoryDiffMB = memoryDiff / 1024.0 / 1024.0;
            
            // 메트릭 기록
            meterRegistry.counter("custom.memory.operation.usage", 
                    "operation", operationName,
                    "status", success ? "success" : "failed")
                    .increment(Math.abs(memoryDiffMB));
            
            String status = success ? "성공" : "실패";
            
            log.info("🔴 [MemoryTracker] {} {} - 메모리변화: {}{:.1f}MB, 현재메모리: {}MB", 
                    operationName, status, (memoryDiff > 0 ? "+" : ""), memoryDiffMB, endMemory / 1024 / 1024);
            
            // 메모리 사용량이 100MB 이상 증가한 경우 경고
            if (memoryDiffMB > 100) {
                log.warn("⚠️ [MemoryTracker] {} 대용량 메모리 사용 감지: +{:.1f}MB", operationName, memoryDiffMB);
            }
        }
    }
    
    /**
     * 현재 힙 메모리 사용량 (바이트)
     */
    private long getCurrentHeapUsage() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
    
    /**
     * 현재 힙 메모리 사용량 (MB) - Gauge용
     */
    private double getCurrentHeapUsageMB() {
        return getCurrentHeapUsage() / 1024.0 / 1024.0;
    }
    
    /**
     * 즉시 메모리 상태 로깅
     */
    public void logCurrentMemoryStatus(String context) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        
        double usagePercent = (double) usedMemory / maxMemory * 100;
        
        log.info("📊 [MemoryTracker] {} - 힙메모리: {}MB/{}MB ({:.1f}%), 여유: {}MB", 
                context, usedMemory, maxMemory, usagePercent, freeMemory);
    }
} 