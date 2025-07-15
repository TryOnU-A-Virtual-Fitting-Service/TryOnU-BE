package tryonu.api.domain;

import jakarta.persistence.*;
import lombok.*;
import tryonu.api.common.enums.Gender;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_is_deleted", columnList = "is_deleted"),
    @Index(name = "idx_users_device_id", columnList = "device_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "device_id", nullable = false)
    String deviceId;

    @Column(name = "name", nullable = false)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    Gender gender;

    @Column(name = "age", nullable = false)
    Integer age;

    @Column(name = "height", nullable = false)
    Integer height;

    @Column(name = "weight", nullable = false)
    Integer weight;

    @Column(name = "is_deleted", nullable = false)
    @Setter
    @Builder.Default
    Boolean isDeleted = false; // 삭제 여부. 삭제되면 true로
}
