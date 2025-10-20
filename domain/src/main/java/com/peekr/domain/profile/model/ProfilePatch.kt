package com.peekr.domain.profile.model

import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.user.model.UserPatch

/**
 * 사용자 프로필 수정 요청
 *
 * @property displayId 사용자 표시 ID
 * @property name 사용자 이름
 * @property profileImageUrl 사용자 프로필 사진 url
 * @property introduce 사용자 소개 글
 */
data class ProfilePatch(
    val displayId: DisplayId,
    val name: Name,
    val profileImageUrl: String?,
    val introduce: Introduce,
)

fun ProfilePatch.toUserPatch(): UserPatch =
    UserPatch(
        displayId = displayId,
        name = name,
        profileImageUrl = profileImageUrl,
        introduce = introduce,
    )
