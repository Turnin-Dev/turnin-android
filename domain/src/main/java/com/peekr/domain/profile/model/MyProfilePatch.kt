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
 * @property oldProfileImageUrl 기존 사용자 프로필 사진 url
 * @property newProfileImageUrl 새로운 사용자 프로필 사진 url
 * @property introduce 사용자 소개 글
 */
data class MyProfilePatch(
    val name: Name,
    val displayId: DisplayId,
    val oldProfileImageUrl: String?,
    val newProfileImageUrl: String?,
    val introduce: Introduce,
)

fun MyProfilePatch.toUserPatch(): UserPatch =
    UserPatch(
        name = name,
        displayId = displayId,
        oldProfileImageUrl = oldProfileImageUrl,
        newProfileImageUrl = newProfileImageUrl,
        introduce = introduce,
    )
