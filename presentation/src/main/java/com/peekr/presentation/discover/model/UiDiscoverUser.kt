package com.peekr.presentation.discover.model

import com.peekr.core.domain.discover.model.DiscoverUser

/**
 * UI용 탐색 사용자 모델
 *
 * @property userId 사용자 ID
 * @property userName 사용자 명
 * @property displayId 사용자 표시 ID
 * @property profileImageUrl 사용자 프로필 url
 */
data class UiDiscoverUser(
    val userId: Long,
    val userName: String,
    val displayId: String,
    val profileImageUrl: String?,
) {
    companion object {
        val sample = UiDiscoverUser(
            userId = 1L,
            userName = "홍길동",
            displayId = "Hong123",
            profileImageUrl = null,
        )
    }
}

fun DiscoverUser.toUiModel(): UiDiscoverUser =
    UiDiscoverUser(
        userId = userId.value,
        userName = userName.value,
        displayId = displayId.value,
        profileImageUrl = profileImageUrl,
    )
