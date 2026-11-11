package com.turnin.core.data.source.network.dto.pingPong.response

import com.squareup.moshi.JsonClass
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.pingPong.model.PingPongQuestioner

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

fun PingPongQuestionerResponse.toDomainModel(): PingPongQuestioner =
    PingPongQuestioner(
        userId = UserId(userId),
        userName = Name(userName),
        profileImageUrl = profileImageUrl,
    )
