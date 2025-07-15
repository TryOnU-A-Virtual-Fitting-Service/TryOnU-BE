package tryonu.api.domain;

import jakarta.persistence.*;
import lombok.*;
import tryonu.api.common.enums.Category;

@Entity
@Table(name = "clothes", indexes = {
    @Index(name = "idx_clothes_is_deleted", columnList = "is_deleted"),
    @Index(name = "idx_clothes_category", columnList = "category")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Cloth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Category category;

    @Column(name = "product_page_url", nullable = false, columnDefinition = "TEXT")
    String productPageUrl;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    String imageUrl;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    @Setter
    Boolean isDeleted = false;


}
