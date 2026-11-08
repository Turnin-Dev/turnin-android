package com.turnin.core.data.source.network.dto.pingPong.response

import com.squareup.moshi.JsonClass

/**
 * 질문자 정보 응답 바디
 *
 * @property userId 질문자 ID
 * @property userName 질문자 이름
 * @property profileImageUrl 질문자 프로필 사진 url
 */
@JsonClass(generateAdapter = true)
data class PingPongQuestionerResponse(
    val userId: Long,
    val userName: String,
    val profileImageUrl: String?,
)
