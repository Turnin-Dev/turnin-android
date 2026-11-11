package com.turnin.core.data.source.network.dto.pingPong.request

import com.squareup.moshi.JsonClass

/**
 * 핑퐁 생성 요청 바디
 *
 * @property question 질문 내용
 */
@JsonClass(generateAdapter = true)
data class CreatePingPongRequest(
    val question: String,
)
