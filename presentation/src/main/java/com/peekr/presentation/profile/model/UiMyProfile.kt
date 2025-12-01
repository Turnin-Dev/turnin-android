package com.peekr.presentation.profile.model

import com.peekr.core.presentation.ui.model.UiUserKeyword
import com.peekr.core.presentation.ui.model.toUiModel
import com.peekr.domain.profile.model.Profile

/**
 * UI용 나의 프로필
 */
data class UiMyProfile(
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
    val introduce: String,
    val friendsCount: Long,
    val lastLoginAt: Long,
    val active: Boolean,
    val keywords: List<UiUserKeyword>,
)

fun Profile.toUiModel(): UiMyProfile =
    UiMyProfile(
        displayId = displayId.value,
        name = name.value,
        profileImageUrl = profileImageUrl,
        introduce = introduce.value,
        friendsCount = friendsCount,
        lastLoginAt = lastLoginAt,
        active = active,
        keywords = keywords.toUiModel(),
    )
