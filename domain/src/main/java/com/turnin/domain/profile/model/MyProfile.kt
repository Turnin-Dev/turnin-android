package com.turnin.domain.profile.model

import com.turnin.core.domain.model.DisplayId
import com.turnin.core.domain.model.Introduce
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.user.model.CoreMyProfile

/**
 * 나의 프로필
 *
 * @property userId 사용자 ID
 * @property displayId 사용자 표시 ID
 * @property name 이름
 * @property profileImageUrl 프로필 사진 url
 * @property introduce 소개 글
 * @property friendsCount 친구 수
 * @property lastLoginAt 마지막 로그인 일시
 * @property active 사용자 활성화 여부
 */
data class MyProfile(
    val userId: UserId,
    val displayId: DisplayId,
    val name: Name,
    val profileImageUrl: String?,
    val introduce: Introduce,
    val friendsCount: Long,
    val lastLoginAt: Long,
    val active: Boolean,
)

fun CoreMyProfile.toMyProfile(): MyProfile =
    MyProfile(
        userId = userId,
        displayId = displayId,
        name = name,
        profileImageUrl = profileImageUrl,
        introduce = introduce,
        friendsCount = friendsCount,
        lastLoginAt = lastLoginAt,
        active = active,
    )
