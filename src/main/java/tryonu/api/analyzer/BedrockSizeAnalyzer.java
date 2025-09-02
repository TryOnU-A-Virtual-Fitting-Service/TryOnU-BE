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
        String system =
                "다음은 옷의 평균 사이즈야.\n" +
                "| SIZE | 가슴둘레   | 어깨너비     | 소매길이     | 총길이        |\n" +
                "|------|------------|-------------|--------------|--------------|\n" +
                "| 90 S  | 90~96      | 43          | 59.5         | 73           |\n" +
                "| 95 M  | 95~100     | 43~45       | 60.5         | 69~74        |\n" +
                "| 100 L | 100~104    | 44.5~47     | 61.5~62      | 71~75        |\n" +
                "| 105 XL | 105~108    | 46~49       | 62.5~63.5    | 73~76        |\n" +
                "| 110 XXL | 110~112   | 47.5~51     | 63.5~65      | 75~77        |\n" +
                "사용자한테 일반적인 옷 평균 수치(방금 전에 보낸 값)에 비해 어깨너비나 등등 다 어떻게 다른지해서 오버핏인지 슬림핏인지 그런 것들을 판단해서 서브 메시지를 줘서 사이즈 판단에 도움을 주려해 거기에 필요한 글을 4줄 정도 써줄래? \n" +
                "앞에다가 이 부분은 일반적인 사이즈보다는 몇 cm~ 정도는 더 크게 나온 제품이다. 이런 정보도 섞어줄 수 있어?\n" +
                "경향은 다 동일하니까 특정 사이즈를 기준으로 말할 필요는 없어.";
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
            payload.put("system", system);
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


