package tryonu.api.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import tryonu.api.domain.SizeAdvice;
import tryonu.api.domain.User;
import tryonu.api.dto.responses.SizeAdviceResponse;

/**
 * SizeAdvice 엔티티와 관련된 변환 로직을 처리하는 컨버터
 */
@Slf4j
@Component
public class SizeAdviceConverter {

    /**
     * SizeAdvice 엔티티를 SizeAdviceResponse로 변환
     *
     * @param sizeAdvice 사이즈 조언 엔티티
     * @return SizeAdviceResponse DTO
     */
    public SizeAdviceResponse toSizeAdviceResponse(@NonNull SizeAdvice sizeAdvice) {
        log.debug("[SizeAdviceConverter] SizeAdvice 엔티티 -> SizeAdviceResponse 변환 시작 - tryOnJobId: {}",
                sizeAdvice.getTryOnJobId());

        SizeAdviceResponse response = new SizeAdviceResponse(
                sizeAdvice.getTryOnJobId(),
                sizeAdvice.getAdvice());

        log.debug("[SizeAdviceConverter] SizeAdvice 엔티티 -> SizeAdviceResponse 변환 완료 - tryOnJobId: {}",
                sizeAdvice.getTryOnJobId());
        return response;
    }

    /**
     * tryOnJobId로 SizeAdvice 엔티티 생성
     *
     * @param tryOnJobId Try-on 작업 ID
     * @return SizeAdvice 엔티티
     */
    public SizeAdvice toSizeAdviceEntity(@NonNull String tryOnJobId, @NonNull User user) {
        log.debug("[SizeAdviceConverter] tryOnJobId로 SizeAdvice 엔티티 생성 시작 - tryOnJobId: {}", tryOnJobId);

        SizeAdvice sizeAdvice = SizeAdvice.builder()
                .tryOnJobId(tryOnJobId)
                .user(user)
                .build();

        log.debug("[SizeAdviceConverter] tryOnJobId로 SizeAdvice 엔티티 생성 완료 - tryOnJobId: {}", tryOnJobId);
        return sizeAdvice;
    }

    /**
     * 사용자와 사이즈 분석 결과를 포함한 SizeAdvice 엔티티 생성
     *
     * @param user       사용자 엔티티
     * @param tryOnJobId Try-on 작업 ID
     * @param sizeInfo   사이즈 정보
     * @param advice     사이즈 조언 내용
     * @return SizeAdvice 엔티티
     */
    public SizeAdvice toSizeAdviceEntity(@NonNull User user, @NonNull String tryOnJobId, String sizeInfo,
            @NonNull String advice) {
        log.debug("[SizeAdviceConverter] 사용자 정보와 사이즈 분석 결과로 SizeAdvice 엔티티 생성 시작 - tryOnJobId: {}", tryOnJobId);

        SizeAdvice sizeAdvice = SizeAdvice.builder()
                .user(user)
                .tryOnJobId(tryOnJobId)
                .sizeInfo(sizeInfo)
                .advice(advice)
                .build();

        log.debug("[SizeAdviceConverter] 사용자 정보와 사이즈 분석 결과로 SizeAdvice 엔티티 생성 완료 - tryOnJobId: {}", tryOnJobId);
        return sizeAdvice;
    }

    /**
     * SizeAdvice 엔티티 정보 업데이트
     *
     * @param sizeAdvice 기존 SizeAdvice 엔티티
     * @param sizeInfo   새로운 사이즈 정보
     * @param advice     새로운 사이즈 조언 내용
     * @return 업데이트된 SizeAdvice 엔티티
     */
    public SizeAdvice updateSizeAdviceEntity(@NonNull SizeAdvice sizeAdvice, String sizeInfo, String advice) {
        log.debug("[SizeAdviceConverter] SizeAdvice 엔티티 정보 업데이트 시작 - tryOnJobId: {}", sizeAdvice.getTryOnJobId());

        if (sizeInfo != null) {
            sizeAdvice.updateSizeInfoAndAdvice(sizeInfo, advice);
        }
        if (advice != null) {
            sizeAdvice.updateSizeInfoAndAdvice(sizeInfo, advice);
        }

        log.debug("[SizeAdviceConverter] SizeAdvice 엔티티 정보 업데이트 완료 - tryOnJobId: {}", sizeAdvice.getTryOnJobId());
        return sizeAdvice;
    }
}
