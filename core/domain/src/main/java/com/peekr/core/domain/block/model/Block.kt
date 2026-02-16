package com.peekr.core.domain.block.model

import com.peekr.core.domain.model.BlockId
import com.peekr.core.domain.model.UserId

/**
 * 차단
 *
 * @property id 차단 ID
 * @property blockerId 차단 요청한 사용자 ID
 * @property blockedId 차단 당한 사용자 ID
 * @property reasonId 차단 사유 ID
 * @property customReason 차단 기타 사유
 */
data class Block(
    val id: BlockId,
    val blockerId: UserId,
    val blockedId: UserId,
    val reasonId: BlockReasonId,
    val customReason: String?,
)
