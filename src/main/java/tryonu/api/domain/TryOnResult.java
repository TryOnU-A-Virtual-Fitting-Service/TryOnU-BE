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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user; // 외래키 관계로 설정
    
    @Column(name = "model_url", nullable = false)
    String modelUrl;

    @Column(name = "default_model_id", nullable = true)
    Long defaultModelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloth_id", nullable = true)
    Cloth cloth;

    @Column(name = "size_advice", nullable = true, columnDefinition = "TEXT") // 사이즈 정보 생성 실패할 경우 null
    String sizeAdvice;

    @Column(name = "image_url", nullable = false)
    String imageUrl;

    @Column(name = "virtual_fitting_id", nullable = true)
    String virtualFittingId; // 가상 피팅 API 응답 ID

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    @Setter
    Boolean isDeleted = false; // 삭제 여부. 삭제되면 true로

}
