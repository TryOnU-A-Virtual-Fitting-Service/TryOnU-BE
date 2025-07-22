package tryonu.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fitting_models", indexes = {
    @Index(name = "idx_fitting_models_is_deleted", columnList = "is_deleted"),
    @Index(name = "idx_fitting_models_user_id", columnList = "user_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class FittingModel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    String imageUrl;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    @Setter
    Boolean isDeleted = false; // 삭제 여부. 삭제되면 true로

    /**
     * 피팅 모델 이미지 URL 업데이트
     */
    public void updateImageUrl(String newImageUrl) {
        this.imageUrl = newImageUrl;
    }
}
