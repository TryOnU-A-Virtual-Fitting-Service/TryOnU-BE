package tryonu.api.domain;

import lombok.*;
import jakarta.persistence.*;
import tryonu.api.common.enums.Gender;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 사용자 개인정보 엔티티
 * 사용자의 개인정보(이름, 성별, 나이, 키, 몸무게)를 관리합니다.
 */
@Entity
@Table(name = "user_infos", indexes = {
    @Index(name = "idx_user_infos_user_id", columnList = "user_id"),
    @Index(name = "idx_user_infos_is_deleted", columnList = "is_deleted")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class UserInfo extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    User user;
    
    @Column(name = "name")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    Gender gender;
    
    @Column(name = "age")
    @Min(value = 1, message = "Age must be positive")
    @Max(value = 150, message = "Age must be realistic")
    Integer age;
    
    @Column(name = "height")
    @Min(value = 1, message = "Height must be positive")
    @Max(value = 300, message = "Height must be realistic")
    Integer height;
    
    @Column(name = "weight")
    @Min(value = 1, message = "Weight must be positive")
    @Max(value = 500, message = "Weight must be realistic")
    Integer weight;
    

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    @Setter
    Boolean isDeleted = false;
    
} 