package tryonu.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 회사 애셋 조회 응답 DTO
 * 현재는 로고 이미지를 제공하며, 향후 다른 애셋도 추가 예정
 * 
 * @param domain 회사 도메인
 * @param assetUrl 애셋 CDN URL
 */
@Schema(description = "회사 애셋 조회 응답")
public record AssetResponse(
    @Schema(description = "회사 도메인", example = "musinsa.com")
    String domain,
    
    @Schema(description = "로고 CDN URL", example = "https://cdn.example.com/company/musinsa/musinsa_header.svg")
    String logoUrl

    // 향후 확장 가능
) {}