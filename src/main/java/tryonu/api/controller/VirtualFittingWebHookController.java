package tryonu.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tryonu.api.dto.responses.VirtualFittingStatusResponse;

@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
@Tag(name = "WebHook API", description = "외부 서비스로부터의 WebHook 수신")
public class VirtualFittingWebHookController {

    @Operation(
        summary = "가상피팅 결과 WebHook 수신",
        description = "FASHN API로부터 가상피팅 처리 완료 시 호출되는 WebHook 엔드포인트입니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "WebHook 수신 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 WebHook 데이터")
    })
    @PostMapping("/virtual-fitting")
    public ResponseEntity<String> receiveVirtualFittingResult(
            @RequestBody VirtualFittingStatusResponse webhookData
    ) {
        log.info("[VirtualFittingWebHook] 가상피팅 결과 수신 - id={}, status={}", 
                webhookData.id(), webhookData.status());
        
        try {
            // TODO: 가상피팅 결과 처리 로직 구현
            // 1. status가 "completed"인 경우: output URL들을 S3에 다운로드 및 저장
            // 2. status가 "failed"인 경우: 에러 로그 및 사용자 알림
            // 3. DB에 결과 저장 (TryOnResult 엔티티 등)
            
            if ("completed".equals(webhookData.status())) {
                log.info("[VirtualFittingWebHook] 가상피팅 성공 - id={}, outputUrls={}", 
                        webhookData.id(), webhookData.output());
                // 성공 처리 로직
            } else if ("failed".equals(webhookData.status())) {
                log.error("[VirtualFittingWebHook] 가상피팅 실패 - id={}, error={}", 
                        webhookData.id(), webhookData.error());
                // 실패 처리 로직
            }
            
            return ResponseEntity.ok("WebHook received successfully");
            
        } catch (Exception e) {
            log.error("[VirtualFittingWebHook] WebHook 처리 중 오류 발생 - id={}, error={}", 
                    webhookData.id(), e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
} 