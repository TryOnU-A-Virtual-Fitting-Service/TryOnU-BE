package tryonu.api.domain;

import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "default_models", indexes = {
    @Index(name = "idx_default_models_is_deleted", columnList = "is_deleted"),
    @Index(name = "idx_default_models_user_id", columnList = "user_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DefaultModel extends BaseEntity {
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
    Boolean isDeleted = false;    
}
