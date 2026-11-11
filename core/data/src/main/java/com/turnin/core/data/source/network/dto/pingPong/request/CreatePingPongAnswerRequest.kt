package com.turnin.core.data.source.network.dto.pingPong.request

import com.squareup.moshi.JsonClass

/**
 * 핑퐁 답변 생성 요청 바디
 *
 * @property answer 답변 내용
 */
@JsonClass(generateAdapter = true)
data class CreatePingPongAnswerRequest(
    val answer: String,
)
