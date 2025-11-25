package com.peekr.presentation.profile.model

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
    val friendsTotal: Long,
    val introduce: String,
    val keywords: List<UiUserKeyword>,
)

fun Profile.toUiModel(): UiProfile =
    UiProfile(
        displayId = displayId.value,
        name = name.value,
        profileImageUrl = profileImageUrl,
        friendsTotal = friendsTotal,
        introduce = introduce.value,
        keywords = keywords.toUiModel(),
    )
