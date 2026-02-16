package com.peekr.core.domain.block.model

import com.peekr.core.domain.model.BlockId
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.UserId

/**
 * 차단 사용자 모델
 *
 * @property id 차단 ID
 * @property userId 차단한 사용자 ID
 * @property displayId 차단한 사용자 표시 ID
 * @property name 차단한 사용자 명
 * @property profileImageUrl 차단한 사용자 프로필 사진 url
 */
data class BlockUser(
    val id: BlockId,
    val userId: UserId,
    val displayId: DisplayId,
    val name: Name,
    val profileImageUrl: String?,
)
