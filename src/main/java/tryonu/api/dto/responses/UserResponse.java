package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import tryonu.api.common.enums.Gender;
import tryonu.api.domain.User;

/**
 * 사용자 정보 응답 DTO
 */
@Schema(description = "사용자 정보 응답")
public record UserResponse(
    
    @Schema(description = "디바이스 ID", example = "device-12345")
    String deviceId,
    
    @Schema(description = "사용자 이름", example = "홍길동")
    String name,
    
    @Schema(description = "성별", example = "MALE")
    Gender gender,
    
    @Schema(description = "나이", example = "25")
    Integer age,
    
    @Schema(description = "키(cm)", example = "175")
    Integer height,
    
    @Schema(description = "몸무게(kg)", example = "70")
    Integer weight
    
) {
    
    /**
     * User 엔티티로부터 UserResponse 생성
     * 
     * @param user User 엔티티
     * @return UserResponse
     */
    public static UserResponse from(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserResponse(
            user.getDeviceId(),
            user.getName(),
            user.getGender(),
            user.getAge(),
            user.getHeight(),
            user.getWeight()
        );
    }
    
    /**
     * 빈 사용자 정보 생성 (새로운 사용자용)
     * 
     * @param deviceId 디바이스 ID
     * @return 빈 사용자 정보
     */
    public static UserResponse empty(String deviceId) {
        return new UserResponse(
            deviceId,
            null,
            null,
            null,
            null,
            null
        );
    }
} 