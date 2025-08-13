package tryonu.api.common.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;    
import java.util.Map;

@Slf4j
@Component
@Profile("local")
public class LocalSlackNotifier implements SlackNotifier {

    @Override
    public void send(String text) {
        log.info("[LocalSlackNotifier] 로컬 환경 -> 에러 슬랙 미전송 - {}", text);
    }

    @Override
    public void sendPayload(Map<String, Object> payload) {
        log.info("[LocalSlackNotifier] 로컬 환경 -> 에러 슬랙 미전송 - {}", payload);
    }
}
