package tryonu.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "try_on_results")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TryOnResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user; // 외래키 관계로 설정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fitting_model_id", nullable = false)
    FittingModel fittingModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloth_id", nullable = true) // 기본 모델로 생성된 결과일 경우 null
    Cloth cloth;

    @Column(name = "size_advice", nullable = true, columnDefinition = "TEXT") // 사이즈 정보 생성 실패할 경우 null
    String sizeAdvice;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    String imageUrl;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    @Setter
    Boolean isDeleted = false; // 삭제 여부. 삭제되면 true로

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    Boolean isDefault = false;  // 기본 모델로 생성 된 결과 여부. 기본 결과이면 true로 설정

}
