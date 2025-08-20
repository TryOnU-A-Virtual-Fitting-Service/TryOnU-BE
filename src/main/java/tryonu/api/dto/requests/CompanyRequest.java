package tryonu.api.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 회사 등록 요청 DTO
 */
@Schema(description = "회사 등록 요청")
public record CompanyRequest(
    
    @Schema(description = "회사명 (고유키로 사용)", example = "musinsa")
    @NotBlank(message = "회사명은 필수입니다.")
    @Size(max = 50, message = "회사명은 50자를 초과할 수 없습니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "회사명은 소문자와 숫자만 사용 가능합니다.")
    String companyName,
    
    @Schema(description = "회사 도메인", example = "musinsa.com")
    @NotBlank(message = "도메인은 필수입니다.")
    @Size(max = 100, message = "도메인은 100자를 초과할 수 없습니다.")
    @Pattern(regexp = "^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "올바른 도메인 형식이 아닙니다.")
    String domain,
        
    @Schema(description = "로고 이미지 CDN URL", example = "https://cdn.thatzfit.com/company/musinsa/musinsa_header.svg")
    @NotBlank(message = "로고 URL은 필수입니다.")
    @Size(max = 500, message = "로고 URL은 500자를 초과할 수 없습니다.")
    @Pattern(regexp = "^https?://.*", message = "올바른 URL 형식이 아닙니다.")
    String logoUrl,
        
    @Schema(description = "활성화 여부", example = "true", defaultValue = "true")
    Boolean isActive
    
) {}
