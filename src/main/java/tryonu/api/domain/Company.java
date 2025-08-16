package tryonu.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

/**
 * 기업 정보를 관리하는 엔티티
 * 로고 이미지 등의 에셋 정보를 포함합니다.
 */
@Entity
@Table(name = "companies")
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
public class Company extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    Long id;
    
    /**
     * 회사명 (고유키로 사용)
     * 예: musinsa, spao, zigzag, ably
     */
    @Column(name = "company_name", nullable = false, unique = true, length = 50)
    String companyName;
    
    /**
     * 회사 도메인 (고유키로 사용)
     * 예: musinsa.com, spao.com, zigzag.kr, ably.co.kr
     */
    @Column(name = "domain", nullable = false, unique = true, length = 100)
    String domain;
    
    /**
     * 회사 표시명
     * 예: 무신사, 스파오, 지그재그, 에이블리
     */
    @Column(name = "display_name", nullable = false, length = 100)
    String displayName;
    
    /**
     * 로고 이미지 CDN URL
     */
    @Column(name = "logo_url", nullable = false, length = 500)
    String logoUrl;

    /**
     * 활성화 여부
     */
    @Column(name = "is_active", nullable = false)
    Boolean isActive;

    @Column(unique = true, nullable = false)
    String pluginKey;

    @Column(nullable = false)
    String secretKey;

    
    @Builder
    public Company(String companyName, String domain, String displayName, String logoUrl, Boolean isActive) {
        this.companyName = companyName;
        this.domain = domain;
        this.displayName = displayName;
        this.logoUrl = logoUrl;
        this.isActive = isActive != null ? isActive : true;
        this.pluginKey = "pk_" + UUID.randomUUID().toString().replace("-", "");
        // 실제 프로덕션에서는 더 강력한 암호화 라이브러리 사용을 권장합니다.
        this.secretKey = "sk_" + RandomStringUtils.randomAlphanumeric(32);

    }

    /**
     * 회사 활성화
     */
    public void activate() {
        this.isActive = true;
    }
    
    /**
     * 회사 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }
}