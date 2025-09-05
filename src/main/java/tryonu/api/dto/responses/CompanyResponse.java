package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 회사 등록/조회 응답 DTO
 */
@Schema(description = "회사 등록/조회 응답")
public record CompanyResponse(
        
    @Schema(description = "회사명", example = "musinsa")
    String companyName,
    
    @Schema(description = "회사 도메인", example = "musinsa.com")
    String domain,

    @Schema(description = "슬로건 이미지 CDN URL", example = "https://cdn.example.com/company/musinsa/musinsa_slogan.svg")
    String sloganUrl,
    
    @Schema(description = "플러그인 키", example = "123e4567-e89b-12d3-a456-426614174000")
    String pluginKey
) {}
