package tryonu.api.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 기본 정보 엔티티
 * 사용자의 핵심 식별 정보(uuid)를 관리합니다.
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_is_deleted", columnList = "is_deleted"),
    @Index(name = "idx_users_uuid", columnList = "uuid")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "uuid", unique = true, nullable = false)
    String uuid;

    @Column(name = "is_deleted", nullable = false)
    @Setter
    @Builder.Default
    Boolean isDeleted = false;
}
