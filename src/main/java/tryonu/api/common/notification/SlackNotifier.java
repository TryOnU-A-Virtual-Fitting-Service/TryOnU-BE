package tryonu.api.common.notification;

import java.util.Map;

public interface SlackNotifier {

    void send(String text);

    void sendPayload(Map<String, Object> payload);
}

