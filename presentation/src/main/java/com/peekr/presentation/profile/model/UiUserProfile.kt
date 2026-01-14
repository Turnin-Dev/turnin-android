package com.peekr.presentation.profile.model

import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.domain.profile.model.UserProfile

/**
 * UI용 사용자 프로필
 *
 * @property displayId 사용자 표시 ID
 * @property name 이름
 * @property profileImageUrl 프로필 사진 url
 * @property introduce 소개 글
 * @property friendsCount 친구 수
 * @property lastLoginAt 마지막 로그인 일시
 * @property keywords 키워드 리스트
 * @property friendStatus 친구 관계 상태 (내 프로필이 아닌 경우에만 null이 아님)
 * @property active 사용자 활성화 여부
 */
data class UiUserProfile(
    val userId: Long,
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
    val introduce: String,
    val friendsCount: Long,
    val lastLoginAt: Long,
    val keywords: List<UiKeywordDetail>,
    val friendStatus: FriendStatus,
    val active: Boolean,
)

fun UserProfile.toUiModel(): UiUserProfile =
    UiUserProfile(
        userId = userId.value,
        displayId = displayId.value,
        name = name.value,
        profileImageUrl = profileImageUrl,
        introduce = introduce.value,
        friendsCount = friendsCount,
        lastLoginAt = lastLoginAt,
        active = active,
        friendStatus = friendStatus,
        keywords = emptyList(),
    )
