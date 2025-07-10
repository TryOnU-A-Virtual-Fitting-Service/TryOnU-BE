package tryonu.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fitting_models")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class FittingModel {
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

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    Boolean isDefault = false; // 기본 모델 여부. 기본 모델이면 true로 설정


}
