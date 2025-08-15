package tryonu.api.common.notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@Profile("prod")
public class ProdSlackNotifier implements SlackNotifier {

    private final WebClient slackWebClient;

    public ProdSlackNotifier(@Qualifier("slackWebClient") WebClient slackWebClient) {
        this.slackWebClient = slackWebClient;
    }

    @Value("${monitoring.slack.webhook-url:https://hooks.slack.com/services/your-api-key}")
    private String webhookUrl;

    @Value("${monitoring.slack.enabled:true}")
    private boolean enabled;

    @Override
    public Mono<Void> send(String text) {
        if (!enabled) {
            return Mono.empty();
        }

        return slackWebClient
                .post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(Map.of("text", text)))
                .retrieve()
                .toBodilessEntity()
                .doOnError(e -> log.warn("[ProdSlackNotifier] 슬랙 전송 실패 - {}", e.getMessage()))
                .then();

    }

    @Override
    public Mono<Void> sendPayload(Map<String, Object> payload) {
        if (!enabled) {
            return Mono.empty();
        }
        return slackWebClient
                .post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payload))
                .retrieve()
                .toBodilessEntity()
                .doOnError(e -> log.warn("[ProdSlackNotifier] 슬랙 전송 실패 - {}", e.getMessage()))
                .then();
    }
}


