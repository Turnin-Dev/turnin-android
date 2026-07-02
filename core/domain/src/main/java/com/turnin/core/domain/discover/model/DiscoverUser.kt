package com.turnin.core.domain.discover.model

import com.turnin.core.domain.model.DisplayId
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId

/**
 * 탐색용 사용자 모델
 *
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property displayId 사용자 표시 ID
 * @property profileImageUrl 사용자 프로필 url
 */
data class DiscoverUser(
    val userId: UserId,
    val userName: Name,
    val displayId: DisplayId,
    val profileImageUrl: String?,
)
