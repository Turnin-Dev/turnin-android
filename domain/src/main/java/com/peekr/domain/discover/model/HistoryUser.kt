package com.peekr.domain.discover.model

import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId

/**
 * 히스토리 사용자
 *
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property profileImageUrl 프로필 사진 URL
 */
data class HistoryUser(
    val userId: UserId,
    val userName: Name,
    val profileImageUrl: String?,
)
