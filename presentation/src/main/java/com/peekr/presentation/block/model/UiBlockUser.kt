package com.peekr.presentation.block.model

/**
 * UI용 차단 사용자
 *
 * @property id 차단 ID
 * @property userId 차단한 사용자 ID
 * @property displayId 차단한 사용자 표시 ID
 * @property name 차단한 사용자 명
 * @property profileImageUrl 차단한 사용자 프로필 사진 url
 */
data class UiBlockUser(
    val id: Long,
    val userId: Long,
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
)
