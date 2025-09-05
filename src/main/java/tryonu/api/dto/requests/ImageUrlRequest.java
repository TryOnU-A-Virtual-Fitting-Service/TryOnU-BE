package tryonu.api.dto.requests;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

/**
 * 이미지 URL 변환 요청 DTO
 */
public record ImageUrlRequest(
    @NotBlank(message = "이미지 URL은 필수입니다.")
    @URL(message = "올바른 URL 형식이어야 합니다.")
    String imageUrl
) {
}
