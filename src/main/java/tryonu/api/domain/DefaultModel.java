package tryonu.api.domain;

import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "default_models")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DefaultModel {
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
