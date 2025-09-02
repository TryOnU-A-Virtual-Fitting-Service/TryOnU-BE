package tryonu.api.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 사이즈 조언 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SizeAdviceResponse {

    /**
     * 사이즈 조언 ID
     */
    private Long id;

    /**
     * Try-on 작업 ID
     */
    private String tryOnJobId;

    /**
     * 사이즈 정보
     */
    private String sizeInfo;

    /**
     * 사이즈 조언 내용
     */
    private String advice;

    /**
     * 생성 일시
     */
    private Instant createdAt;

    /**
     * 수정 일시
     */
    private Instant updatedAt;
}