package com.peekr.presentation.profile.model

import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.presentation.keyword.model.UiKeyword

/**
 * UI용 사용자 프로필
 */
data class UiUserProfile(
    val displayId: DisplayId,
    val name: Name,
    val profileImageUrl: String?,
    val friendsTotal: Long,
    val introduce: Introduce,
    val keywords: List<UiKeyword>,
)
