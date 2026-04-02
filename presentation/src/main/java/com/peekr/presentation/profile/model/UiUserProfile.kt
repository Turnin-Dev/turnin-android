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
 * @property friendStatus 친구 관계 상태 (내 프로필이 아닌 경우에만 null이 아님)
 * @property active 사용자 활성화 여부
 * @property isBlocked 차단 여부
 */
data class UiUserProfile(
    val userId: Long,
    val displayId: String,
    val name: String,
    val profileImageUrl: String?,
    val introduce: String,
    val friendsCount: Long,
    val lastLoginAt: Long,
    val friendStatus: FriendStatus,
    val active: Boolean,
    val isBlocked: Boolean,
) {
    companion object {
        val sample = UiUserProfile(
            userId = 1L,
            displayId = "Honggd123",
            name = "홍길동",
            profileImageUrl = null,
            introduce = "이 부분은 나를 간단히 소개할 수 있는 곳입니다.\n" +
                "1 ~ 2줄 정도로 간단히 본인을 소개하세요. 1 ~ 2줄 정도로 간단히 본인을 소개하세요.\n" +
                "이 부분은 나를 간단히 소개할 수 있는 곳입니다.\n" +
                "1 ~ 2줄 정도로 간단히 본인을 소개하세요. 1 ~ 2줄 정도로 간단히 본인을 소개하세요.",
            friendsCount = 86,
            lastLoginAt = 1000L,
            active = true,
            friendStatus = FriendStatus.NOTHING,
            isBlocked = false,
        )
    }
}

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
        isBlocked = isBlocked,
    )
