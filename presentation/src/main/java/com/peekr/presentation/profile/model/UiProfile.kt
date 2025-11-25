package com.peekr.presentation.profile.model

import com.peekr.core.domain.model.FriendshipStatus
import com.peekr.core.presentation.ui.model.UiUserKeyword
import com.peekr.core.presentation.ui.model.toUiModel
import com.peekr.domain.profile.model.Profile

/**
 * UI용 사용자 프로필
 */
data class UiProfile(
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
    val introduce: String,
    val friendsCount: Long,
    val lastLoginAt: Long,
    val active: Boolean,
    val friendshipStatus: FriendshipStatus?,
    val keywords: List<UiUserKeyword>,
)

fun Profile.toUiModel(): UiProfile =
    UiProfile(
        displayId = displayId.value,
        name = name.value,
        profileImageUrl = profileImageUrl,
        introduce = introduce.value,
        friendsCount = friendsCount,
        lastLoginAt = lastLoginAt,
        active = active,
        friendshipStatus = friendshipStatus,
        keywords = keywords.toUiModel(),
    )
