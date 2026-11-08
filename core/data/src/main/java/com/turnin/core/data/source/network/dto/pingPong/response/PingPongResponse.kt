package com.turnin.core.data.source.network.dto.pingPong.response

import com.squareup.moshi.JsonClass

/**
 * 핑퐁 응답 바디
 *
 * @property id 핑퐁 ID
 * @property userKeywordId 질문이 달린 사용자 키워드(게시물) ID
 * @property questionerId 질문자 ID
 * @property question 질문 내용
 * @property createdAt 생성 일자
 * @property updatedAt 수정 일자
 */
@JsonClass(generateAdapter = true)
data class PingPongResponse(
    val id: Long,
    val userKeywordId: Long,
    val questionerId: Long,
    val question: String,
    val createdAt: Long,
    val updatedAt: Long,
)
