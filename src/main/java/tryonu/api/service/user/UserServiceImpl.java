package tryonu.api.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tryonu.api.domain.User;
import tryonu.api.dto.requests.UserInitRequest;
import tryonu.api.repository.defaultmodel.DefaultModelRepository;
import tryonu.api.repository.fittingmodel.FittingModelRepository;
import tryonu.api.repository.user.UserRepository;
import tryonu.api.domain.DefaultModel;
import tryonu.api.domain.FittingModel;
import tryonu.api.common.enums.Gender;
import tryonu.api.converter.DefaultModelConverter;
import tryonu.api.converter.FittingModelConverter;

import java.util.Optional;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor    
@Transactional(readOnly = true) 
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final DefaultModelRepository defaultModelRepository;
    private final FittingModelRepository fittingModelRepository;

    private final DefaultModelConverter defaultModelConverter;  
    private final FittingModelConverter fittingModelConverter;

    @Override
    @Transactional
    public void initializeUser(UserInitRequest request) {
        log.info("[UserService] 익명 사용자 초기화 시작: deviceId={}", request.deviceId());
        
        // 이미 존재하는 사용자인지 확인
        Optional<User> existingUser = userRepository.findByDeviceId(request.deviceId());
        if (existingUser.isPresent()) { // 이미 존재하는 사용자인 경우: 기존 사용자 정보 반환
            User user = existingUser.get();
            log.info("[UserService] 기존 사용자 발견: userId={}, deviceId={}", user.getId(), request.deviceId());
            return;
        }
        
        // 존재하지 않는 경우: 새로운 사용자 생성 후 반환
        User newUser = User.builder()
                .deviceId(request.deviceId())
                .build();
        User savedUser = userRepository.save(newUser);

        for (Gender gender : Gender.values()) {
            DefaultModel defaultModel = defaultModelConverter.createDefaultModel(savedUser, gender);
            defaultModelRepository.save(defaultModel);
            FittingModel fittingModel = fittingModelConverter.createFittingModel(savedUser, gender);
            fittingModelRepository.save(fittingModel);
        }
        
        log.info("[UserService] 새 사용자 생성 완료: userId={}, deviceId={}", savedUser.getId(), request.deviceId());
    }


}
