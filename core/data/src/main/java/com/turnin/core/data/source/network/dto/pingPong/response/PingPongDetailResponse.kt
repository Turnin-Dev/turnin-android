package com.turnin.core.data.source.network.dto.pingPong.response

import com.squareup.moshi.JsonClass

/**
 * 핑퐁 상세 응답 바디 (목록에서 사용하는 항목)
 *
 * @property pingPong 핑퐁
 * @property questioner 질문자 정보
 * @property answer 답변 (답변이 없거나 숨김 처리된 경우 `null`)
 */
@JsonClass(generateAdapter = true)
data class PingPongDetailResponse(
    val pingPong: PingPongResponse,
    val questioner: PingPongQuestionerResponse,
    val answer: PingPongAnswerResponse?,
)
