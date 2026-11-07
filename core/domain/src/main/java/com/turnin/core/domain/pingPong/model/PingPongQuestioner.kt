package com.turnin.core.domain.pingPong.model

import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId

/**
 * 질문자 정보
 *
 * @property userId 질문자 ID
 * @property userName 질문자 이름
 * @property profileImageUrl 질문자 프로필 사진 url
 */
data class PingPongQuestioner(
    val userId: UserId,
    val userName: Name,
    val profileImageUrl: String?,
)
