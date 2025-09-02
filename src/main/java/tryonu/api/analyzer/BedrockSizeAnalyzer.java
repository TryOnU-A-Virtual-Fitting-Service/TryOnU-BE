package tryonu.api.analyzer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import software.amazon.awssdk.core.SdkBytes;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class BedrockSizeAnalyzer implements SizeAnalyzer {

    private final BedrockRuntimeClient bedrockRuntimeClient;

    @Value("${spring.ai.chat.model}")
    private String modelId;

    @Override
    public SizeAnalyzeResult analyze(SizeAnalyzeRequest sizeAnalyzeRequest) {
        String system = "You are a helpful fashion sizing assistant. Answer in Korean, <= 2 sentences.";
        String userText = "[tryOnJobId] " + sizeAnalyzeRequest.tryOnJobId() + "\n" +
                "[sizeInfo] " + sizeAnalyzeRequest.sizeInfo();

        try {
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> systemBlock = Map.of(
                    "type", "text",
                    "text", system);

            Map<String, Object> userContent = Map.of(
                    "type", "text",
                    "text", userText);

            Map<String, Object> userMessage = Map.of(
                    "role", "user",
                    "content", List.of(userContent));

            Map<String, Object> payload = new HashMap<>();
            payload.put("anthropic_version", "bedrock-2023-05-31");
            payload.put("max_tokens", 500);
            payload.put("system", List.of(systemBlock));
            payload.put("messages", List.of(userMessage));

            String requestJson = mapper.writeValueAsString(payload);

        InvokeModelRequest invoke = InvokeModelRequest.builder()
                .modelId(modelId)
                .contentType("application/json")
                .accept("application/json")
                .body(SdkBytes.fromUtf8String(requestJson))
                .build();

        InvokeModelResponse response = bedrockRuntimeClient.invokeModel(invoke);
        String responseBody = response.body().asUtf8String();

        String advice = extractTextFromAnthropicResponse(responseBody);
        if (advice == null || advice.isBlank()) {
            advice = "사이즈 정보를 확인할 수 없어 일반적인 정사이즈 착용을 권장합니다.";
        }
        return new SizeAnalyzeResult(advice.trim());
        } catch (Exception e) {
            log.warn("[BedrockSizeAnalyzer] Request build/invoke failed: {}", e.getMessage());
            return new SizeAnalyzeResult("사이즈 정보를 확인할 수 없어 일반적인 정사이즈 착용을 권장합니다.");
        }
    }

    private String extractTextFromAnthropicResponse(String json) {
        try {
            int idx = json.indexOf("\"text\":");
            if (idx == -1) return null;
            int start = json.indexOf('"', idx + 7);
            int end = json.indexOf('"', start + 1);
            if (start == -1 || end == -1) return null;
            return json.substring(start + 1, end);
        } catch (Exception e) {
            log.warn("[BedrockSizeAnalyzer] Failed to parse response: {}", e.getMessage());
            return null;
        }
    }
}


