package tryonu.api.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class BackgroundRemovalUtil {
    private final WebClient backgroundRemovalWebClient;

    /**
     * 배경 제거 API 호출 (MultipartFile → byte[])
     */
    public byte[] removeBackground(MultipartFile file) {
        return backgroundRemovalWebClient.post()
                .uri("/ml/remove-background")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", file.getResource()))
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }
} 