package tryonu.api.domain;

import java.util.UUID;

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
     * 회사 도메인
     * 예: musinsa.com, spao.com, zigzag.kr, ably.co.kr
     */
    @Column(name = "domain", nullable = false, unique = true, length = 100)
    private String domain;

    /**
     * 고객사 식별자
     */
    @Column(name = "plugin_key", nullable = false, unique = true, length = 100)
    private String pluginKey;
    
    
    /**
     * 로고 이미지 CDN URL
     */
    @Column(name = "logo_url", nullable = false, length = 500)
    private String logoUrl;

    /**
     * 슬로건 이미지 CDN URL
     */
    @Column(name = "slogan_url", nullable = false, length = 500)
    private String sloganUrl;
    
    /**
     * btn 이미지 CDN URL
     */
    @Column(name = "btn_url", nullable = false, length = 500)
    private String btnUrl;
    
    /**
     * 활성화 여부
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;


    
    @Builder
    public Company(String companyName, String domain, String logoUrl, String sloganUrl, Boolean isActive) {
        this.companyName = companyName;
        this.domain = domain;
        this.logoUrl = logoUrl;
        this.sloganUrl = sloganUrl;
        this.isActive = isActive != null ? isActive : true;
        this.pluginKey = UUID.randomUUID().toString();
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