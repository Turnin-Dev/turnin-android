package com.turnin.core.domain.pingPong.model

/**
 * 핑퐁 상세 모델 (목록에서 사용하는 항목)
 *
 * @property pingPong 핑퐁
 * @property questioner 질문자 정보
 * @property answer 답변 (답변이 없거나 숨김 처리된 경우 `null`)
 */
data class PingPongDetail(
    val pingPong: PingPong,
    val questioner: PingPongQuestioner,
    val answer: PingPongAnswer?,
)
