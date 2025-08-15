package tryonu.api.common.notification;

import java.util.Map;
import reactor.core.publisher.Mono;

public interface SlackNotifier {

    Mono<Void> send(String text);

    Mono<Void> sendPayload(Map<String, Object> payload);
}

