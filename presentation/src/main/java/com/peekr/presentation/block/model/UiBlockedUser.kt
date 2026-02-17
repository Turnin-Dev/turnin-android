package com.peekr.presentation.block.model

import com.peekr.core.domain.block.model.BlockedUser

/**
 * UI용 차단 사용자
 *
 * @property id 차단 ID
 * @property userId 차단한 사용자 ID
 * @property displayId 차단한 사용자 표시 ID
 * @property name 차단한 사용자 명
 * @property profileImageUrl 차단한 사용자 프로필 사진 url
 */
data class UiBlockedUser(
    val id: Long,
    val userId: Long,
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
)

fun BlockedUser.toUiModel(): UiBlockedUser =
    UiBlockedUser(
        id = id.value,
        userId = userId.value,
        displayId = displayId.value,
        name = name.value,
        profileImageUrl = profileImageUrl,
    )
