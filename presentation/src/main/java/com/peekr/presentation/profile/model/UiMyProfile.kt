package com.peekr.presentation.profile.model

import com.peekr.core.presentation.ui.model.UiUserKeyword
import com.peekr.core.presentation.ui.model.toUiModel
import com.peekr.domain.profile.model.MyProfile

/**
 * UI용 나의 프로필
 *
 * @property userId 사용자 ID
 * @property displayId 사용자 표시 ID
 * @property name 이름
 * @property profileImageUrl 프로필 사진 url
 * @property introduce 소개 글
 * @property friendsCount 친구 수
 * @property lastLoginAt 마지막 로그인 일시
 * @property keywords 키워드 리스트
 * @property active 사용자 활성화 여부
 */
data class UiMyProfile(
    val userId: Long,
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
    val introduce: String,
    val friendsCount: Long,
    val lastLoginAt: Long,
    val active: Boolean,
    val keywords: List<UiUserKeyword>,
)

fun MyProfile.toUiModel(): UiMyProfile =
    UiMyProfile(
        userId = userId.value,
        displayId = displayId.value,
        name = name.value,
        profileImageUrl = profileImageUrl,
        introduce = introduce.value,
        friendsCount = friendsCount,
        lastLoginAt = lastLoginAt,
        active = active,
        keywords = keywords.toUiModel(),
    )
