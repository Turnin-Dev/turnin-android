package com.peekr.domain.profile.model

import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.user.model.ProfileImagePatch
import com.peekr.core.domain.user.model.UserPatch

/**
 * 사용자 프로필 수정 요청
 *
 * @property displayId 사용자 표시 ID
 * @property name 사용자 이름
 * @property oldProfileImageUrl 기존 프로필 사진 url
 * @property profileImagePatch 프로필 사진 패치
 * @property introduce 사용자 소개 글
 */
data class MyProfilePatch(
    val name: Name,
    val displayId: DisplayId,
    val oldProfileImageUrl: String?,
    val profileImagePatch: ProfileImagePatch,
    val introduce: Introduce,
)

fun MyProfilePatch.toUserPatch(): UserPatch =
    UserPatch(
        name = name,
        displayId = displayId,
        oldProfileImageUrl = oldProfileImageUrl,
        profileImagePatch = profileImagePatch,
        introduce = introduce,
    )
