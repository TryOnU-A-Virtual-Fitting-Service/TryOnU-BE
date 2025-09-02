package tryonu.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "try_on_results", indexes = {
        @Index(name = "idx_try_on_results_is_deleted", columnList = "is_deleted"),
        @Index(name = "idx_try_on_results_user_id", columnList = "user_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TryOnResult extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "try_on_job_id", nullable = false, unique = true)
    String tryOnJobId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user; // 외래키 관계로 설정

    @Column(name = "model_url")
    String modelUrl;

    @Column(name = "default_model_id")
    Long defaultModelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloth_id")
    Cloth cloth;

    @Column(name = "image_url")
    String imageUrl;

    @Column(name = "virtual_fitting_id")
    String virtualFittingId; // 가상 피팅 API 응답 ID

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    @Setter
    Boolean isDeleted = false; // 삭제 여부. 삭제되면 true로

    // Setter methods
    public void setId(Long id) {
        this.id = id;
    }

    public void setTryOnJobId(String tryOnJobId) {
        this.tryOnJobId = tryOnJobId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    public void setDefaultModelId(Long defaultModelId) {
        this.defaultModelId = defaultModelId;
    }

    public void setCloth(Cloth cloth) {
        this.cloth = cloth;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVirtualFittingId(String virtualFittingId) {
        this.virtualFittingId = virtualFittingId;
    }

    /**
     * 피팅 결과 정보 업데이트
     */
    public void updateTryOnResult(Cloth cloth, User user, String modelUrl, String imageUrl,
            String virtualFittingId, Long defaultModelId) {
        this.cloth = cloth;
        this.user = user;
        this.modelUrl = modelUrl;
        this.imageUrl = imageUrl;
        this.virtualFittingId = virtualFittingId;
        this.defaultModelId = defaultModelId;
    }

}
