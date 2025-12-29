package com.peekr.core.domain.keywordGraph.model

import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId

/**
 * 사용자 노드 모델
 *
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 사용자 프로필 url
 */
data class UserNode(
    val userId: UserId,
    val userName: Name,
    val profileImageUrl: String?,
)
