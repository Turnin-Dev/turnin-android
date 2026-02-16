package com.peekr.presentation.block.model

/**
 * UI용 차단
 *
 * @property id 차단 ID
 * @property blockerId 차단 요청한 사용자 ID
 * @property blockedId 차단 당한 사용자 ID
 * @property reasonId 차단 사유 ID
 * @property customReason 차단 기타 사유
 */
data class UiBlock(
    val id: Long,
    val blockerId: Long,
    val blockedId: Long,
    val reasonId: Long,
    val customReason: String?,
)
