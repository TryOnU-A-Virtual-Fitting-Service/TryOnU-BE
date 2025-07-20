package tryonu.api.domain;

import lombok.*;
import jakarta.persistence.*;
import tryonu.api.common.enums.Gender;

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
@Setter
public class UserInfo extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    User user;
    
    @Column(name = "name")
    String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    Gender gender;
    
    @Column(name = "age")
    Integer age;
    
    @Column(name = "height")
    Integer height;
    
    @Column(name = "weight")
    Integer weight;
    

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    @Setter
    Boolean isDeleted = false;
    
} 