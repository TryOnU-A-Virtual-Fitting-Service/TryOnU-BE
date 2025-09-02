package tryonu.api.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사이즈 조언 엔티티
 * 사용자의 사이즈 분석 결과를 기반으로 한 조언 정보를 관리합니다.
 */
@Entity
@Table(name = "size_advices", indexes = {
        @Index(name = "idx_size_advices_try_on_job_id", columnList = "try_on_job_id"),
        @Index(name = "idx_size_advices_user_id", columnList = "user_id"),
        @Index(name = "idx_size_advices_is_deleted", columnList = "is_deleted")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class SizeAdvice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "try_on_job_id", nullable = false, unique = true)
    private String tryOnJobId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "advice", columnDefinition = "TEXT")
    private String advice;

    @Column(name = "size_info", columnDefinition = "TEXT")
    private String sizeInfo;

    @Column(name = "is_deleted")
    @Builder.Default
    @Setter
    private Boolean isDeleted = false;

    /**
     * 조언 내용을 업데이트합니다.
     */
    public void updateSizeInfoAndAdvice(String sizeInfo, String advice) {
        this.sizeInfo = sizeInfo;
        this.advice = advice;
    }

    /**
     * 소프트 삭제된 사이즈 조언을 복구합니다.
     */
    public void restore() {
        this.isDeleted = false;
    }
}
