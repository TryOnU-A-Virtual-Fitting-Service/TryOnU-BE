package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 회사 로고 조회 응답 DTO
 * 
 * @param companyName 회사명
 * @param logoUrl 로고 이미지 CDN URL
 */
@Schema(description = "회사 로고 조회 응답")
public record LogoResponse(
    @Schema(description = "회사명", example = "musinsa")
    String companyName,
    
    @Schema(description = "로고 이미지 CDN URL", example = "https://cdn.example.com/logos/musinsa.png")
    String logoUrl
) {}