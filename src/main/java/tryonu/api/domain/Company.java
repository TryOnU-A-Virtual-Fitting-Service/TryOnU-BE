package tryonu.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    private Long id;
    
    /**
     * 회사명 (고유키로 사용)
     * 예: musinsa, spao, zigzag, ably
     */
    @Column(name = "company_name", nullable = false, unique = true, length = 50)
    private String companyName;
    
    /**
     * 회사 도메인 (고유키로 사용)
     * 예: musinsa.com, spao.com, zigzag.kr, ably.co.kr
     */
    @Column(name = "domain", nullable = false, unique = true, length = 100)
    private String domain;
    
    /**
     * 회사 표시명
     * 예: 무신사, 스파오, 지그재그, 에이블리
     */
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;
    
    /**
     * 로고 이미지 CDN URL
     */
    @Column(name = "logo_url", nullable = false, length = 500)
    private String logoUrl;
    
    /**
     * 회사 설명
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 활성화 여부
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


    
    @Builder
    public Company(String companyName, String domain, String displayName, String logoUrl, String description, Boolean isActive) {
        this.companyName = companyName;
        this.domain = domain;
        this.displayName = displayName;
        this.logoUrl = logoUrl;
        this.description = description;
        this.isActive = isActive != null ? isActive : true;
    }
    
    /**
     * 회사 정보 업데이트
     * 
     * @param domain 도메인
     * @param displayName 표시명
     * @param logoUrl 로고 URL
     * @param description 설명
     */
    public void updateCompanyInfo(String domain, String displayName, String logoUrl, String description) {
        if (domain != null) {
            this.domain = domain;
        }
        if (displayName != null) {
            this.displayName = displayName;
        }
        if (logoUrl != null) {
            this.logoUrl = logoUrl;
        }
        if (description != null) {
            this.description = description;
        }
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