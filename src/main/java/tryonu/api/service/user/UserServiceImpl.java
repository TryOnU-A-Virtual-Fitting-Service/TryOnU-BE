package tryonu.api.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tryonu.api.domain.User;
import tryonu.api.dto.requests.UserInitRequest;
import tryonu.api.dto.responses.UserInitResponse;
import tryonu.api.dto.responses.UserInfoResponse;
import tryonu.api.dto.responses.FittingModelDto;
import tryonu.api.dto.responses.DefaultModelDto;
import tryonu.api.repository.defaultmodel.DefaultModelRepository;
import tryonu.api.repository.fittingmodel.FittingModelRepository;
import tryonu.api.repository.user.UserRepository;
import tryonu.api.domain.DefaultModel;
import tryonu.api.domain.FittingModel;
import tryonu.api.common.enums.Gender;
import tryonu.api.converter.DefaultModelConverter;
import tryonu.api.converter.FittingModelConverter;
import tryonu.api.common.auth.SecurityUtils;

import java.util.List;
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
    public UserInitResponse initializeUser(UserInitRequest request) {
        log.info("[UserService] 익명 사용자 초기화 시작: deviceId={}", request.deviceId());
        
        User user;
        // 이미 존재하는 사용자인지 확인
        Optional<User> existingUser = userRepository.findByDeviceId(request.deviceId());
        if (existingUser.isPresent()) { 
            // 이미 존재하는 사용자인 경우: 기존 사용자 정보 사용
            user = existingUser.get();
            log.info("[UserService] 기존 사용자 발견: userId={}, deviceId={}", user.getId(), request.deviceId());
        } else {
            // 존재하지 않는 경우: 새로운 사용자 생성
            User newUser = User.builder()
                    .deviceId(request.deviceId())
                    .build();
            user = userRepository.save(newUser);

            for (Gender gender : Gender.values()) {
                DefaultModel defaultModel = defaultModelConverter.createDefaultModel(user, gender);
                defaultModelRepository.save(defaultModel);
                FittingModel fittingModel = fittingModelConverter.createFittingModel(user, gender);
                fittingModelRepository.save(fittingModel);
            }
            
            log.info("[UserService] 새 사용자 생성 완료: userId={}, deviceId={}", user.getId(), request.deviceId());
        }
        
        // 사용자의 모델 정보 조회 (id 내림차순 정렬)
        List<FittingModelDto> fittingModels = fittingModelRepository.findFittingModelsByUserIdOrderByIdDesc(user.getId());
        List<DefaultModelDto> defaultModels = defaultModelRepository.findDefaultModelsByUserIdOrderByIdDesc(user.getId());
        
        log.info("[UserService] 사용자 초기화 응답 생성 완료 - userId: {}, fittingModels: {}, defaultModels: {}", 
                user.getId(), fittingModels.size(), defaultModels.size());
                
        return new UserInitResponse(fittingModels, defaultModels);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getCurrentUserInfo() {
        // Security Filter에서 이미 인증된 사용자만 여기까지 올 수 있음
        User currentUser = SecurityUtils.getCurrentUser();
        log.info("[UserService] 현재 사용자 정보 조회 시작 - userId: {}, deviceId: {}", currentUser.getId(), currentUser.getDeviceId());
        
        // 사용자의 모델 정보 조회 (id 내림차순 정렬)
        List<FittingModelDto> fittingModels = fittingModelRepository.findFittingModelsByUserIdOrderByIdDesc(currentUser.getId());
        List<DefaultModelDto> defaultModels = defaultModelRepository.findDefaultModelsByUserIdOrderByIdDesc(currentUser.getId());
        
        log.info("[UserService] 사용자 정보 조회 완료 - userId: {}, fittingModels: {}, defaultModels: {}", 
                currentUser.getId(), fittingModels.size(), defaultModels.size());
                
        return new UserInfoResponse(fittingModels, defaultModels);
    }


}
