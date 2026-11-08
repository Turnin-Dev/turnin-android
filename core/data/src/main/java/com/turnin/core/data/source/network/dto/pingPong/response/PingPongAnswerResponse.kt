package com.turnin.core.data.source.network.dto.pingPong.response

import com.squareup.moshi.JsonClass

/**
 * 답변 응답 바디
 *
 * @property id 답변 ID
 * @property pingPongId 답변이 달린 핑퐁 ID
 * @property answer 답변 내용
 * @property createdAt 생성 일자
 * @property updatedAt 수정 일자
 */
@JsonClass(generateAdapter = true)
data class PingPongAnswerResponse(
    val id: Long,
    val pingPongId: Long,
    val answer: String,
    val createdAt: Long,
    val updatedAt: Long,
)
