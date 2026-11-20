package com.turnin.core.domain.contentReport.model

import com.turnin.core.domain.model.PingPongAnswerId
import com.turnin.core.domain.model.PingPongId

/**
 * 콘텐츠 신고 대상
 *
 * 콘텐츠 유형마다 참조하는 ID가 다르므로, 유형과 ID를 한 쌍으로 묶어 잘못된 조합을 막는다.
 */
sealed interface ContentReportType {
    /**
     * 핑퐁 질문
     *
     * @property pingPongId 신고 대상 핑퐁 ID
     */
    data class PingPongQuestion(
        val pingPongId: PingPongId,
    ) : ContentReportType

    /**
     * 핑퐁 답변
     *
     * @property pingPongAnswerId 신고 대상 핑퐁 답변 ID
     */
    data class PingPongAnswer(
        val pingPongAnswerId: PingPongAnswerId,
    ) : ContentReportType
}
